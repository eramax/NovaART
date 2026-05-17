# NovaART: Wayland-Native Android App Runtime

## Overview

NovaART is a Wayland-native Android app runtime that embeds ART (Android Runtime), implements AOSP framework JNI stubs, and renders apps on Wayland surfaces. Built first for Ubuntu (glibc/Wayland), then ported to QOS (Alpine/musl).

## Architecture

```
APK (DEX) ──dex2oat──▶ .oat (native ELF for target arch)
                            │
AOSP framework (API 28+) ──▶ .oat (pre-compiled per arch)
                            │
                      ART runtime (libart.so)
                            │
                  libnovaart_jni.a (our JNI stubs)
                      │              │              │
                  wl_egl           wl_shm         wl_seat
                 (GLES apps)   (Canvas apps)    (input)
                      │              │              │
                      └──────┬───────┘              │
                             │                      │
                     Wayland compositor (GNOME/river/sway)
```

### Key Components

1. **ART runtime** (`libart.so`) — Built from AOSP source with JIT + dex2oat. Handles DEX loading, class resolution, GC, and JIT compilation.

2. **Android framework** — AOSP `frameworks/base` Java source compiled to DEX via `d8`, then AOT-compiled to OAT via `dex2oat`.

3. **JNI bridge** — Our replacement `JNINativeMethod` stubs. Implements framework native methods. Rendering → Wayland, Input → `wl_seat`, Audio → PipeWire.

4. **Wayland client** — Direct `libwayland-client` (no GTK4). Creates `xdg_toplevel` windows, `wl_egl` for GLES, `wl_shm` for Canvas, `wl_seat` for input.

### Threading Model

```
┌─────────────────────────────────────────────┐
│ Main Thread (Wayland + Looper)               │
│  - Wayland event dispatch (wl_display)        │
│  - Android Looper message pump                │
│  - Choreographer frame callbacks (wl_callback)│
│  - Activity lifecycle calls                   │
│  - EGL context (thread-local)                 │
└─────────────────────────────────────────────┘
                                                       
┌─────────────────────────────────────────────┐
│ ART GC Threads                                │
│  - Concurrent mark-sweep GC                   │
│  - Background JIT compilation                 │
│  - No Wayland/EGL access                      │
└─────────────────────────────────────────────┘
```

The main thread owns: Wayland display connection, EGL context, Android Looper, and Choreographer. ART's GC and JIT threads run in the background with no Wayland access — all Android API calls from app threads are marshalled to the main thread's Looper queue.

### Looper + Choreographer

Android apps assume a `Looper`/`Handler` message queue on the main thread with `Choreographer` driven by vsync. We provide:

- `android.os.Looper` — message pump integrated with Wayland `wl_display` event dispatch
- `android.view.Choreographer` — frame callbacks driven by `wl_callback` (Wayland frame events)
- vsync timing approximated from `wl_callback` `tv_sec_hi/lo` + `tv_nsec`

Without this, animated apps (gles3jni, GD) will either spin-poll or stall.

### JNI Stub Strategy: Systematic + No-Crash

- Scan AOSP `frameworks/base/core/jni/` for `gRegJNI[]` entries
- Generate stub implementations with safe defaults
- Classify stubs by return type:
  1. **Primitive** (int, bool, float) → return 0/false/0.0
  2. **jlong handle** → allocate minimal sentinel struct, return address
  3. **jobject/jstring/jarray** → return NULL (caller checks)
- Unimplemented stubs log a warning — apps limp along instead of crashing

### Lifecycle: Simplified In-Process

- No Binder IPC. No system services (no ActivityManagerService, WindowManagerService).
- In-process ActivityManager calls lifecycle methods via JNI.
- Intents are local hash maps, not Binder parcels.
- Each Activity → xdg_toplevel: create→open, resume→render, pause→hide, destroy→close.

### System Services: Stub-First

| Service | Approach |
|---|---|
| PackageManager | Read AndroidManifest.xml from APK, return stored metadata |
| ConnectivityManager | Stub returning "WiFi connected" |
| WifiManager | Stub returning "WiFi enabled" |
| SensorManager | Stub returning empty sensor list |
| Vibrator | No-op |
| AudioTrack | MVP: no-op. Later: PipeWire stream |
| Storage | Map /data/data/<pkg> → ~/.local/share/novaart/<pkg>/ |
| Clipboard | wl_data_device |
| Notifications | MVP: no-op. Later: libnotify |

## Project Structure

```
deps/NovaART/
├── build-host.sh          # Ubuntu host build script
├── src/                   # Runtime source
│   ├── meson.build
│   ├── main.c             # Entry point
│   ├── nova.h             # Common header
│   ├── wayland.c          # Wayland display, window, registry
│   ├── egl.c              # wl_egl surface for GLES
│   ├── art.c              # ART initialization
│   ├── jni/               # JNI stubs
│   │   ├── meson.build
│   │   ├── android_runtime.c   # gRegJNI[] table
│   │   ├── core_jni_helpers.*  # Registration helpers
│   │   ├── android_os_*        # OS stubs
│   │   ├── android_view_*      # Input stubs
│   │   ├── android_graphics_*  # Canvas stubs (later → Skia)
│   │   └── stubs/              # Generated stubs
│   ├── looper.c           # Main thread Looper + Choreographer
│   ├── shm.c              # wl_shm buffer pool
│   ├── input.c            # wl_seat → Android events
│   ├── apk.c              # APK loading + manifest parse
│   └── activity.c         # Activity lifecycle management
├── docs/
│   ├── design.md          # This document
│   ├── plan.md            # Implementation plan
│   └── review.md          # External review findings
├── scripts/
│   ├── generate_stubs.sh  # JNI stub generator
│   ├── extract_framework.sh # Pull Java from AOSP
│   └── compile-framework.sh # javac → d8 → dex2oat
├── aosp/                  # AOSP master-art checkout
└── output/                # Build artifacts
```

## Build Pipeline

### Stage 1: Build ART from AOSP (Host)

```sh
# Uses AOSP's master-art manifest (177 projects)
# ART_TARGET_LINUX=true for glibc host build
# Output: libart.so, dex2oat, etc.
cd deps/aosp-full
source build/envsetup.sh
lunch silvermont-eng
make libart dex2oat -j$(nproc)
```

### Stage 2: Compile AOSP Framework

```sh
# Full android.jar, not a manual subset
# AOSP's make framework target produces consistent DEX
# Compile to .oat via dex2oat
scripts/compile-framework.sh
```

### Stage 3: Build Runtime + JNI Stubs

```sh
# Generate JNI stubs from AOSP JNI source scan
scripts/generate_stubs.sh
# Build runtime binary
./build-host.sh
```

### Stage 4: Host Testing

```sh
LD_LIBRARY_PATH=/path/to/aosp/out/host/linux-x86_64/lib64 \
  ./output/bin/novaart app.apk
```

## QOS Integration (Future)

After MVP is proven on Ubuntu, package as QOS component:

```
components/novaart-runtime/
├── component.yaml
└── rootfs/
    ├── usr/bin/novaart
    └── usr/lib/novaart/
        ├── libart.so
        ├── libnovaart_jni.a
        └── framework/*.oat
```

Key changes for QOS/musl: ART must either be built in a glibc container and extracted, or ported to musl. The runtime binary itself should be compiled for musl/Alpine.

## Implementation Phases

### Phase 1: MVP — gles3jni Works on Host
1. Build ART from AOSP for glibc host
2. Create Wayland window (xdg_toplevel + wl_egl)
3. Implement Looper + Choreographer with wl_callback vsync
4. Load gles3jni APK: parse manifest, create Activity, run lifecycle
5. Route GLES calls through wl_egl → working rendering
6. **Phase 1 gate checkpoint** (end of Task 6)
7. ~3-5 weeks

### Phase 2: Canvas Support
1. Compile Skia from AOSP for desktop
2. Implement Canvas JNI stubs → Skia
3. wl_shm buffer transport for Canvas rendering
4. Test with GD, 2048
5. ~2-3 weeks

### Phase 3: Broader App Support
1. Implement no-crash trampolines for all remaining JNI stubs
2. Fix SurfaceView → wl_subsurface
3. Input handling via wl_seat
4. AssetManager + XmlBlock for resource loading
5. Test with Bomber, Taponium, Replica Island
6. ~3-5 weeks

### Phase 4: Complete API 28+ Coverage + Audio + QOS
1. Complete remaining API 28+ framework stubs
2. Audio stub → PipeWire
3. Storage mapping
4. Multi-window (multiple activities)
5. QOS component integration
6. ~4-8 weeks

## Risks

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| AOSP framework has too many undeclared deps | High | High | Use full `make framework` from AOSP |
| JNI handle stubs cause NPE cascade | Med | High | Classify by return type; sentinel structs for jlong |
| Choreographer/Looper absent causes render hangs | Med | High | Implement minimal Looper + wl_callback vsync before Phase 1 |
| Skia standalone build fails | Med | Med | Fall back to Cairo for MVP Canvas path |
| ART on musl (future QOS port) | Med | High | Build ART in glibc container, extract artifacts |
