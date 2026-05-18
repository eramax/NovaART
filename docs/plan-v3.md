# NovaART v3 Implementation Plan

## Architecture Overview

NovaART runs real AOSP framework Java code (`android.*` classes) on ART, with
**local Binder-shaped service stubs** replacing `system_server`, and Android
display routed to Wayland surfaces.

No containers, no kernel modules. Binder is kept as an **API shape** (AIDL
interfaces, IBinder) but implemented **in-process** — `ServiceManager.getService()`
returns local stubs, `transact()` dispatches directly, no kernel IPC.

```
 frameworks/base/ (Java source)          art/ + libcore/ (AOSP master-art)
          │                                        │
          │ selected packages                       │ banchan build
          │ + Nova forked choke points              │
          ▼                                        ▼
   nova-framework.jar                boot.oat (java.*) + libart.so
          │                                        │
          └──────────────┬─────────────────────────┘
                         ▼
                   ART Runtime
              (JNI_CreateJavaVM)
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
    JNI Native        Local AIDL       Wayland Bridge
    Backends          Service Stubs    (wl_egl GLES
    (MessageQueue,    (IWindowMgr,      wl_shm Canvas
     Surface,          IDisplayMgr,     wl_seat input)
     EGL10 impl)       IPackageMgr)
```

**Key architectural insight:** Don't patch hundreds of Binder call sites.
Instead, implement local AIDL service stubs that AOSP framework code calls
naturally via IBinder/IInterface. AOSP's `Binder` base class already supports
in-process `transact()` dispatch — no kernel module or Unix sockets needed.
Service stubs register via `ServiceManager.addService()` at startup.

---

## Native Library Compatibility Boundary

This is an explicit architectural boundary, not a bug or workaround.

**What NovaART handles generally (Phases 1–3):**
- ✅ DEX bytecode (Java/Kotlin) — has no CPU architecture, runs on any ART
- ✅ Java-only APKs — the majority of F-Droid (~60%+), many games and utilities
- ✅ APKs with optional native code where the Java path is sufficient

**What requires additional work (Phase 4):**
- ⚠️  APKs with `lib/x86_64/` native `.so` files — works once Bionic namespace is solved
- ❌  APKs with `lib/arm64-v8a/` only — requires binary translation (FEX-Emu / Box64)

**Why this boundary exists:** Android native `.so` files are compiled against
Android's Bionic C library (`libc.so`, `libm.so`, `libdl.so`). The NovaART
host process uses glibc. These two C libraries use different symbol versioning
schemes and cannot coexist in the same process without namespace isolation.
This is a real ABI boundary, not a missing-file problem.

**Verify any APK before testing:**
```bash
unzip -l yourapp.apk | grep "^.*lib/"
# No output        = Java-only = safe for all phases
# lib/x86_64/      = safe for Phase 4 (once Bionic solved)
# lib/arm64-v8a/   = blocked until binary translation (Phase 4+)
```

---

## APK Acquisition Tooling

NovaART uses two CLI tools to download test APKs without needing a running
Android device or app store UI.

### fdroidcl — F-Droid Command-Line Client

```bash
# Install (requires Go)
go install mvdan.cc/fdroidcl@latest

# Usage
fdroidcl update                          # sync F-Droid repository index
fdroidcl search "2048"                   # find apps by name
fdroidcl show com.androbaby.game2048     # inspect metadata, version, arch
fdroidcl download com.androbaby.game2048 # download APK to current directory
```

F-Droid build metadata explicitly lists native library dependencies. If an
app's F-Droid page shows no `lib/` entries in the APK, it is Java-only and
safe for Phases 1–3.

### apkeep — Multi-Source APK Downloader

```bash
# Install (requires Rust/Cargo)
cargo install apkeep

# Download from F-Droid
apkeep -a com.neverball.neverball -d fdroid .

# Download from APKPure (for universal APKs with x86_64 slice)
apkeep -a org.telegram.messenger -d apkpure .
```

`apkeep` is preferred for Phase 4 APKs from APKMirror/APKPure where you need
to select a specific architecture variant (`x86_64` or `universal`).

### APK staging convention

```
deps/NovaART/apks/
├── phase1/          # GLES pipeline validation
│   └── gles3jni.apk
├── phase2/          # Java Canvas + input
│   ├── 2048.apk
│   ├── shattered-pixel-dungeon.apk
│   ├── mindustry.apk
│   ├── antimine.apk
│   └── simple-calculator.apk
├── phase3/          # Full framework, network, fragments
│   ├── wikipedia.apk
│   ├── newpipe.apk
│   ├── antennapod.apk
│   ├── k9mail.apk
│   └── organic-maps.apk
├── phase4-native/   # x86_64 native .so (post-Bionic)
│   ├── vlc-x86_64.apk
│   ├── firefox-x86_64.apk
│   ├── telegram-universal.apk
│   └── signal-universal.apk
└── phase5-store/    # App store clients (post-PackageManager)
    └── fdroid.apk
```

Download all Phase 1–3 APKs:
```bash
bash scripts/download-test-apks.sh
```

---

## Stage 0: Prerequisites

### 0.1 Build ART + bootclasspath from AOSP

**Important:** This must be done from a clean `master-art`-aligned checkout
state. Do not sync `frameworks/base` into the active ART build tree before
this step. If `frameworks/base/Android.bp` is present, AOSP flips unbundled
ART builds from prebuilt SDK mode to source SDK mode
(`UNBUNDLED_BUILD_SDKS_FROM_SOURCE=true`), which expands the dependency graph
dramatically and defeats the thin-manifest assumption.

For the exact command sequence, see `scripts/repro-sync-art.sh`.

```sh
cd deps/aosp-full
source build/envsetup.sh
banchan com.android.art x86_64
m com.android.art -j$(nproc)
```

For NovaART's host/glibc runtime:
```sh
cd /path/to/deps/NovaART
bash scripts/build-host-art-runtime.sh
```

Produces host artifacts NovaART stages and runs against:
- `deps/aosp-full/out/host/linux-x86/lib64/libart.so`
- `deps/aosp-full/out/host/linux-x86/bin/dex2oat64`
- `deps/aosp-full/out/host/linux-x86/bin/dalvikvm64`

### 0.2 Create framework-source worktree

Keep `deps/aosp-full` reserved for the known-good ART build state. Use a
separate repo client for framework sources:

- `deps/aosp-full` → ART build tree (`master-art`)
- `deps/aosp-framework-src` → framework source tree (`android-latest-release`)

```sh
mkdir -p deps/aosp-framework-src && cd deps/aosp-framework-src
repo init --worktree --partial-clone --no-use-superproject \
  -b android-latest-release \
  -u https://android.googlesource.com/platform/manifest
repo sync -c -j$(nproc) \
  frameworks/base external/skia external/freetype \
  external/harfbuzz_ng external/libpng external/icu
```

### 0.3 Project structure

```
deps/NovaART/
├── src/                    # C source: Wayland, EGL, ART bootstrap, event loop
├── src/jni/                # JNI stub modules + registration framework
├── src/java/nova/          # Nova-owned Java: ActivityThread, WindowManager, services
├── src/java/nova-shims/    # Hidden-API shims needed for framework compilation
├── src/java/aosp-forks/    # Forked AOSP choke points
├── apks/                   # Test APKs organised by phase (see above)
├── scripts/                # Build, stage, download, smoke-run scripts
├── build-host.sh           # Meson build with auto toolchain detection
├── Makefile                # Stable operator surface for all commands
└── output/                 # Built binary + staged runtime
```

### 0.4 Smoke-test milestone

```sh
cd deps/NovaART
bash scripts/smoke-run-gles3jni.sh
```

Current success criterion:
- ART initialises successfully
- JNI registration completes without `ClassNotFoundException` or `NoSuchMethodError`
- `gles3jni` process stays alive under bounded timeout

---

## Phase 1: GLES Pipeline (GPU Rendering Stack)

**Goal:** Rotating triangle visible on screen via `gles3jni` APK.

**Validates:** ART bootstrap, EGL JNI bridge, Wayland surface creation, GLES
passthrough to host GPU.

**Does NOT validate:** Generic Android native `.so` loading (see Native Library
Compatibility Boundary above).

### Framework classes (Phase 1)

**Unmodified from AOSP (~35 files):**

OpenGL/EGL Java wrappers (`frameworks/base/opengl/java/`):
- `android/opengl/GLSurfaceView.java`
- `com/google/android/gles_jni/EGLImpl.java`, `EGLContextImpl.java`,
  `EGLDisplayImpl.java`, `EGLSurfaceImpl.java`, `EGLConfigImpl.java`, `GLImpl.java`
- `javax/microedition/khronos/egl/EGL10.java`, `EGL11.java`, `EGLConfig.java`,
  `EGLContext.java`, `EGLDisplay.java`, `EGLSurface.java`
- `javax/microedition/khronos/opengles/GL.java`, `GL10.java`, `GL10Ext.java`,
  `GL11.java`, `GL11Ext.java`, `GL11ExtensionPack.java`
- `android/opengl/GLDebugHelper.java`, `EGLLogWrapper.java`, `GLException.java`

Core OS/util (`frameworks/base/core/java/`):
- `android/os/Bundle.java`, `Handler.java`, `Looper.java`, `Message.java`,
  `MessageQueue.java`, `IBinder.java`, `Binder.java`, `IInterface.java`,
  `RemoteException.java`, `Parcelable.java`, `Parcel.java`, `SystemClock.java`,
  `Trace.java`
- `android/util/Log.java`, `AttributeSet.java`

Content/app basics:
- `android/content/Context.java`, `ContextWrapper.java`, `Intent.java`,
  `ComponentName.java`
- `android/app/Application.java`

Graphics/view value types:
- `android/graphics/PixelFormat.java`, `Rect.java`, `Point.java`
- `android/view/SurfaceHolder.java`, `Surface.java`, `Display.java`,
  `WindowManager.java`, `LayoutParams.java`

**Fork/adapt for NovaART (~15 choke point files):**

Activity/bootstrap:
- `android/app/Activity.java`, `ActivityThread.java`, `Instrumentation.java`,
  `LoadedApk.java`, `ContextImpl.java`

Window/view stack:
- `android/view/Window.java`, `WindowManagerImpl.java`, `WindowManagerGlobal.java`,
  `ViewRootImpl.java`, `SurfaceView.java`, `View.java`, `ViewGroup.java`
- `com/android/internal/policy/PhoneWindow.java`

Display:
- `android/hardware/display/DisplayManagerGlobal.java`
- `android/view/SurfaceControl.java` (forked to bypass BLAST buffer queue)
- `android/view/Choreographer.java` (skipped — synchronous rendering for Phase 1)

Resources (thin subset):
- `android/content/res/Resources.java`, `AssetManager.java`, `Configuration.java`
- `android/util/DisplayMetrics.java`

### AIDL service stubs (Phase 1)

| AIDL Service | Minimum Stub Methods |
|---|---|
| `IWindowManager` | `openSession()` → local IWindowSession |
| `IWindowSession` | `addToDisplay()` → Wayland surface + ADD_OKAY. `relayout()` → valid SurfaceControl + InsetsState. `remove()` → destroy. `finishDrawing()` → no-op |
| `IDisplayManager` | `getDisplayInfo()` → 1920×1080@60Hz. `registerCallback()` → no-op |
| `IPackageManager` | `getApplicationInfo()`, `getActivityInfo()`, `checkPermission()` → GRANTED |

### JNI native methods (Phase 1)

**MessageQueue** (critical — Looper integration):
- `nativeInit()`, `nativeDestroy()`, `nativePollOnce()`, `nativeWake()`
  integrated with Wayland epoll loop

**Surface** (critical — display backing):
- `nativeCreate()`, `nativeRelease()`, `nativeIsValid()`

**EGL10 JNI** (critical — gles3jni uses EGL10, not EGL14):
- `eglGetDisplay`, `eglInitialize`, `eglChooseConfig`, `eglCreateContext`,
  `eglCreateWindowSurface`, `eglMakeCurrent`, `eglSwapBuffers`,
  `eglDestroyContext`, `eglDestroySurface`, `eglQueryString`,
  `eglQuerySurface`, `eglGetError`, `eglTerminate`

Not needed for Phase 1 (forked out): `SurfaceControl`, `DisplayEventReceiver`, Canvas JNI.

### Nova-owned code (Phase 1)

**Java (~15 files):**
- `NovaActivityThread.java` — app bootstrap, Activity lifecycle dispatch
- `NovaInstrumentation.java` — direct lifecycle calls
- `NovaLoadedApk.java` — APK metadata, classloader, native lib path
- `NovaContextImpl.java` — `getSystemService()`, package/resources hooks
- `NovaWindowManagerGlobal.java`, `NovaWindowManagerImpl.java`
- `NovaViewRootImpl.java` — attach, measure/layout, window session
- `NovaSurfaceView.java` — simplified fork, no SurfaceControl/BLAST/HWUI
- `NovaPhoneWindow.java`, `NovaDecorView.java` — minimal policy window
- `NovaServiceManager.java`, `NovaWindowManagerService.java`,
  `NovaWindowSession.java`, `NovaDisplayManagerService.java`,
  `NovaPackageManagerService.java`
- `NovaApkLoader.java` — parse manifest, extract native libs

**C (~8 files):**
- `art.c` — JNI_CreateJavaVM + bootclasspath init
- `wayland.c` — display connect, registry, compositor, xdg-toplevel
- `egl.c` — wl_egl_window, EGLDisplay/Context/Surface
- `window.c` — Nova window objects backing IWindowSession
- `surface.c` — backing for android.view.Surface native methods
- `message_queue.c` — native backend for MessageQueue (nativePollOnce/Wake + epoll)
- `egl_jni.c` — adapted AOSP `com_google_android_gles_jni_EGLImpl.cpp`
- `jni_registration.c` — register all Nova/AOSP-adapted JNI methods

### Phase 1 Gate

| Criterion | Verification |
|---|---|
| ART creates JavaVM with bootclasspath | Smoke script exits 0 |
| Wayland window opens, EGL context created | `wl_display_connect` succeeds |
| `gles3jni` APK loads, Activity starts | `onCreate()` observed in log |
| GLES calls forwarded to host GPU | No EGL errors in log |
| Rotating triangle visible on screen | Visual confirmation |

**Estimated duration:** Steps A–G = ~2 weeks

### Phase 1 Test APKs

| APK | Package | Download |
|---|---|---|
| gles3jni (host-native replacement) | AOSP sample | Built from `third_party/gles3jni/` |
| GLMark2-ES | `org.glmark2.glmark2` | `fdroidcl download org.glmark2.glmark2` |

Note: gles3jni native `.so` is replaced with a host-native build from AOSP
sample sources. This is a bounded Phase 1 tactic — it validates the renderer
stack but not generic Android native `.so` loading. See the Native Library
Compatibility Boundary section.

---

## Phase 2: Java Canvas + Input

**Goal:** Real Java-only apps render, accept touch/click, run game loops.

**Validates:** Canvas/Skia pipeline, wl_shm shared memory buffers, Looper +
Choreographer vsync, wl_seat touch and keyboard input.

**New additions over Phase 1:**
- Skia build (from `external/skia` or system package)
- `Canvas` JNI — `drawRect`, `drawBitmap`, `drawText`, `drawPath`
- `Paint` JNI — stroke, fill, color, typeface
- `Bitmap` JNI — allocation, pixel access, format conversion
- `wl_shm` buffer pool — CPU-rendered frame submission to Wayland
- `Choreographer` connected to `wl_callback` frame events (vsync)
- `wl_seat` input dispatch — pointer → `MotionEvent`, keyboard → `KeyEvent`

### Phase 2 Gate

| Criterion | Verification |
|---|---|
| Canvas app renders first frame | Visual confirmation |
| Animation runs at stable framerate | Choreographer vsync connected |
| Touch/tap delivered to app correctly | Tile moves in 2048 |
| Keyboard input delivered | Text entry works |

**Estimated duration:** ~2 weeks after Phase 1

### Phase 2 Test APKs

All Java-only — verified with `unzip -l app.apk | grep "lib/"` returning empty.

| APK | Package | Why | Download |
|---|---|---|---|
| 2048 | `com.androbaby.game2048` | Pure Canvas game, simple touch input | `fdroidcl download com.androbaby.game2048` |
| Shattered Pixel Dungeon | `com.shatteredpixel.shatteredpixeldungeon` | Complex game loop, audio, Canvas scene graph | `fdroidcl download com.shatteredpixel.shatteredpixeldungeon` |
| Mindustry | `io.anuke.mindustry` | Custom Canvas renderer, continuous frame loop | `fdroidcl download io.anuke.mindustry` |
| Antimine | `dev.lucanlm.antimine` | Material UI, touch grid, Kotlin/Java | `fdroidcl download dev.lucanlm.antimine` |
| Simple Calculator | `com.simplemobiletools.calculator` | Minimal — good regression target | `fdroidcl download com.simplemobiletools.calculator` |

---

## Phase 3: Full Framework — Network, Fragments, Services

**Goal:** Real-world apps reach their main screen and perform at least one core
function.

**Validates:** Fragment navigation, RecyclerView, network stack (OkHttp),
ContentProvider, background Service lifecycle, full Resources/Assets pipeline.

**New additions over Phase 2:**
- `ContentProvider` stub — local in-process implementation
- `ConnectivityManager` stub — returns "connected, WiFi"
- `NotificationManager` stub — accepts notifications, no-op display
- `JobScheduler` stub — accepts jobs, no-op scheduling
- Background `Service` lifecycle — `onCreate()`, `onStartCommand()`, `onDestroy()`
- Full `Resources` loading from APK `res/` folder — layouts, strings, drawables
- `WebView` support — uses system WebKitGTK or Chromium embedded (optional)

### Phase 3 Gate

| Criterion | Verification |
|---|---|
| App launches to main screen | Visual confirmation |
| At least one core function works | e.g., Wikipedia article loads |
| No crash on common service calls | No `NullPointerException` on service stubs |
| Back navigation works | Fragment back stack functions |

**Estimated duration:** ~4 weeks after Phase 2

### Phase 3 Test APKs

| APK | Package | Why | Download |
|---|---|---|---|
| Wikipedia | `org.wikipedia` | RecyclerView, network, fragments, WebView | `fdroidcl download org.wikipedia` |
| NewPipe | `org.schabi.newpipe` | Video streaming, ExoPlayer (Java), no GMS | `fdroidcl download org.schabi.newpipe` |
| AntennaPod | `de.danoeh.antennapod` | Media playback, notifications, background service | `fdroidcl download de.danoeh.antennapod` |
| K-9 Mail | `com.fsck.k9` | AccountManager, ContentProvider, complex UI | `fdroidcl download com.fsck.k9` |
| Organic Maps | `app.organicmaps` | Custom renderer, large assets, no GMS | `apkeep -a app.organicmaps -d fdroid .` |
| Lichess | `org.lichess.mobileapp` | Network (WebSocket), complex UI, pure Java | `fdroidcl download org.lichess.mobileapp` |

### MicroG / GMS Strategy (Phase 3)

The in-process Binder architecture naturally enables MicroG — its AIDL service
implementations register via the same `ServiceManager` the Nova stubs use.

**Priority order:**

1. **Signature spoofing (10 lines, high impact)**
   `NovaPackageManagerService.checkSignatures()` returns `SIGNATURE_MATCH`
   for `com.google.android.gms`. Unblocks all apps that only check GMS
   presence at install/launch.

2. **Inline GMS stubs (`NovaGmsStub.java`)**
   Satisfies the 5–10 most common GMS API calls without running MicroG:
   - `isGooglePlayServicesAvailable()` → `SUCCESS`
   - `getLastLocation()` → null
   - `getFCMToken()` → fake token string
   - `getAccounts()` → empty array

3. **Real MicroG co-loading (only if needed)**
   Required only for FCM push or Google Sign-In. Requires multi-APK loading
   in `NovaApkLoader.java` and `NovaAccountManagerService.java` stub.

---

## Phase 4: Native `.so` Compatibility (Bionic Namespace)

**Goal:** APKs with `lib/x86_64/` native libraries run unmodified.

**This is the phase where the Phase 1 workaround ends.** Generic Android
Bionic native `.so` files cannot load in the current glibc host process.
This phase introduces proper Android native library loading.

**Approach options (choose one before starting Phase 4):**

| Option | Description | Effort | Coverage |
|---|---|---|---|
| **A: Android linker namespace isolation** | Load `linker64` + Bionic in-process with namespace separation from glibc | High | Full x86_64 APK support |
| **B: Minimal Bionic shim layer** | Provide a thin `libc.so` / `libm.so` with LIBC symbol versions that forward to glibc equivalents | Medium | Works for simple native libs; breaks on complex pthread/TLS usage |
| **C: Scope to Java-only + x86_64 slice APKs** | Only support APKs with no native code OR with `lib/x86_64/` compiled against system glibc | Low | Covers ~70% of F-Droid + apps explicitly built for desktop Linux |

Recommendation: Start with Option B as a fast validation path. Move to Option
A if apps with complex native dependencies are required.

**arm64-only APKs** require binary translation (FEX-Emu or Box64) and are
out of scope for Phase 4. This is a separate Phase 5 track.

### Phase 4 Test APKs

Download x86_64 or universal variants explicitly — never arm64-only.

| APK | x86_64 Available | Download |
|---|---|---|
| VLC for Android | ✅ Universal APK | `apkeep -a org.videolan.vlc -d apkpure .` |
| Firefox / Fennec | ✅ x86_64 variant | `apkeep -a org.mozilla.firefox -d apkpure .` |
| Telegram | ✅ Universal APK | `apkeep -a org.telegram.messenger -d apkpure .` |
| Signal | ✅ Universal APK | `apkeep -a org.thoughtcrime.securesms -d apkpure .` |
| Termux | ✅ x86_64 build | GitHub releases: `termux/termux-app` |

---

## Phase 5: App Store Integration + Filesystem + Package Installer

**Goal:** F-Droid (or another store) runs inside NovaART, can download APKs,
and install them into a persistent per-app data filesystem. Users interact
with a real app catalog rather than the CLI.

**This phase is what makes NovaART a user-facing product rather than a
developer testing environment.**

### 5.1 Persistent Filesystem

Android apps expect a structured filesystem per app:

```
output/data/
├── app/
│   ├── com.androbaby.game2048-1/   ← installed APK location
│   │   └── base.apk
│   └── org.wikipedia-1/
│       └── base.apk
├── data/
│   ├── com.androbaby.game2048/     ← app private data (like AppData on Windows)
│   │   ├── files/
│   │   ├── cache/
│   │   └── databases/
│   └── org.wikipedia/
│       ├── files/
│       └── cache/
└── media/                          ← shared storage (like Documents/Downloads)
    └── 0/
        └── Download/
```

**New Nova-owned code for Phase 5:**
- `NovaStorageManager.java` — maps `Context.getFilesDir()`, `getCacheDir()`,
  `getExternalFilesDir()` to host filesystem paths under `output/data/`
- `NovaFileProvider.java` — Android `FileProvider` URI resolution
- `filesystem.c` — creates and manages per-app directory trees on the host

### 5.2 Real PackageManager

The Phase 1–3 `NovaPackageManagerService` is a stub that only knows about
the one APK passed on the command line. Phase 5 replaces it with a real
implementation:

- **APK registry** — JSON database of installed apps at `output/data/packages.json`
- **Install path** — `NovaPackageManager.install(apkPath)`:
  1. Parse `AndroidManifest.xml` (already done in `NovaApkLoader.java`)
  2. Copy APK to `output/data/app/{package}-{version}/base.apk`
  3. Run `dex2oat` on the DEX inside the APK → compiled OAT cache
  4. Register in `packages.json`
  5. Create `output/data/data/{package}/` directory tree
- **Uninstall path** — remove APK, OAT cache, and data directory
- **Query methods** — `getInstalledApplications()`, `getInstalledPackages()`,
  `resolveActivity()` for Intent resolution

### 5.3 PackageInstaller AIDL Stub

Apps (including F-Droid) install other APKs via the `PackageInstaller` AIDL
interface. Minimum stub methods:

| Method | Stub Behaviour |
|---|---|
| `createSession()` | Return a local `IPackageInstallerSession` |
| `IPackageInstallerSession.write()` | Stream APK bytes to a temp file |
| `IPackageInstallerSession.commit()` | Call `NovaPackageManager.install(tempFile)` |
| `IPackageInstallerSession.abandon()` | Delete temp file |

### 5.4 App Launcher

Once multiple APKs are installed, NovaART needs a way to launch them by
package name rather than file path:

```bash
# Direct file path (current)
./output/bin/novaart apks/phase2/2048.apk

# Package name (Phase 5)
./output/bin/novaart --package com.androbaby.game2048
./output/bin/novaart --package org.fdroid.fdroid      # F-Droid itself
```

This requires `NovaPackageManager` to resolve the package name to its
installed APK path.

### 5.5 F-Droid as the Phase 5 Milestone App

F-Droid is the ideal Phase 5 milestone target because it exercises every new
capability:

| F-Droid Feature | Requires |
|---|---|
| Browse catalog (list UI) | Phase 2 — RecyclerView, Canvas |
| Search and app detail pages | Phase 3 — Fragments, network |
| Download APK | Phase 3 — DownloadManager, network |
| Install APK | Phase 5 — PackageInstaller, PackageManager |
| Launch installed app | Phase 5 — App launcher by package name |

**Phase 5 Gate:** F-Droid opens, browses catalog, downloads an APK, installs
it, and the installed app launches from F-Droid.

### Phase 5 Test APKs

| APK | Package | Download |
|---|---|---|
| F-Droid | `org.fdroid.fdroid` | `fdroidcl download org.fdroid.fdroid` |
| Aurora Store (GPlay frontend) | `com.aurora.store` | `fdroidcl download com.aurora.store` |
| Droid-ify (modern F-Droid client) | `com.looker.droidify` | `fdroidcl download com.looker.droidify` |

---

## Implementation Order

```
Step A:  ART build (banchan + build-host-art-runtime.sh) → libart.so + boot.oat
Step B:  Wayland bridge (wayland.c, egl.c) → window + GLES surface
Step C:  ART bootstrap (art.c + JNI_CreateJavaVM) → JavaVM running
Step D:  MessageQueue JNI (nativePollOnce/Wake + epoll) → Looper works
         [Gate: Looper test — post Message, verify dispatch, before Step E]
Step E:  AIDL stub codegen + service stubs (IWindowSession, IPackageManager, etc.)
Step F:  Framework compile (AOSP sources + Nova forks + AIDL stubs → nova-framework.jar)
         Script: scripts/build-framework.sh
Step G:  Surface JNI + EGL10 JNI → gles3jni renders  ──────────── PHASE 1 GATE
Step H:  Skia + Canvas JNI + wl_shm + Choreographer + wl_seat ──── PHASE 2 GATE
Step I:  ContentProvider + Service + full Resources + WebView ───── PHASE 3 GATE
Step J:  Bionic namespace isolation or shim layer ───────────────── PHASE 4 GATE
Step K:  Filesystem + PackageManager + PackageInstaller + Launcher ─ PHASE 5 GATE
Step L:  F-Droid installs and launches apps ─────────────────────── PHASE 5 MILESTONE
```

---

## Key Files

| File | Purpose |
|---|---|
| `deps/aosp-full/` | AOSP master-art checkout — ART build tree only |
| `deps/aosp-framework-src/` | AOSP framework source tree — read-only source input |
| `src/art.c` | JNI_CreateJavaVM bootstrap, bootclasspath setup |
| `src/wayland.c` | Wayland display, registry, xdg-toplevel, wl_seat |
| `src/egl.c` | wl_egl_window, EGLDisplay/Context/Surface, swap |
| `src/window.c` | Nova window objects backing IWindowSession |
| `src/surface.c` | Native backing for android.view.Surface |
| `src/message_queue.c` | Native backend for MessageQueue (epoll + Wayland) |
| `src/filesystem.c` | Per-app directory tree management (Phase 5) |
| `src/egl_jni.c` | JNI for AOSP EGL10 wrappers |
| `src/jni_registration.c` | Register all Nova/AOSP-adapted JNI methods |
| `src/java/nova/` | Nova-owned Java: ActivityThread, WindowManager*, service stubs |
| `src/java/nova-shims/` | Hidden-API shims for Phase 1 overlay compilation |
| `src/java/aosp-forks/` | Forked AOSP choke points: Activity, ViewRootImpl, SurfaceView |
| `scripts/build-framework.sh` | javac + d8 → nova-framework-dex.jar |
| `scripts/stage-art.sh` | Stage ART artifacts from AOSP build to output/ |
| `scripts/download-test-apks.sh` | Download all phase APKs via fdroidcl + apkeep |
| `scripts/smoke-run-gles3jni.sh` | Bounded smoke test for Phase 1 |
| `apks/` | Test APKs organised by phase |
| `output/data/` | Persistent app filesystem (Phase 5) |
| `progress.md` | Hand-maintained engineering log — append verified milestones only |

---

## Risks and Mitigations

| Risk | Likelihood | Mitigation |
|---|---|---|
| `frameworks/base` circular deps in `javac` | **High** | Use `make framework` from AOSP as fallback — extract `framework.jar`, replace packages with Nova forks progressively |
| Overlay source set grows uncontrolled | High | Keep `build-framework.sh` source list explicit; add shims for hidden types rather than widening the source set |
| `SurfaceControl`/BLAST crash in `ViewRootImpl` | **High** | Fork `SurfaceView` + `ViewRootImpl` to bypass BLAST entirely; call `window.c` directly for Wayland surface |
| Bionic `.so` ABI mismatch (Phase 1 tactical workaround) | **Known** | Explicitly scoped to Phase 1; general solution deferred to Phase 4 |
| Looper hang with no output | Medium | Add Looper test gate after Step D before Step E |
| ART can't load `nova-framework.jar` | Low | Bootclasspath loading is ART's core function |
| Binder stubs miss call paths | Medium | Monitor logs, add stub methods on crash |
| `frameworks/base` API mismatch with ART version | Medium | Use same `main` branch for both; keep two AOSP worktrees separate |
| Skia not in `master-art` tree | Medium | Use system `libskia` package or check out `external/skia` separately |
| Phase 3 complex apps (Facebook, etc.) | **Overly optimistic** | Treat as stretch goal; gate Phase 3 on "first Wikipedia article loads" |
| Phase 5 `PackageInstaller` complexity | Medium | Implement `commit()` as synchronous call into `NovaPackageManager`; no async session state needed for MVP |
| F-Droid architecture check fails | Medium | `NovaPackageManager.getSystemAvailableFeatures()` must return `x86_64` ABI; `Build.SUPPORTED_ABIS` stub must reflect host architecture |

### SurfaceControl/BLAST avoidance

`ViewRootImpl` in modern AOSP creates a `SurfaceControl` and uses BLAST buffer
queue for frame submission. Fork `ViewRootImpl` to call `window.c` Wayland
surface directly. Fork `SurfaceView` to avoid `SurfaceControl.createDisplay()`.

### `make framework` fallback

If `javac` hits circular dependency walls:
```sh
cd deps/aosp-full && source build/envsetup.sh && lunch aosp_x86_64-eng
m framework   # Soong builds full framework.jar
```
Extract `out/target/product/generic_x86_64/system/framework/framework.jar`,
then progressively replace packages with Nova fork versions.

### Looper test gate (Step D → Step E)

Before framework compile, verify: create JVM, instantiate a `Looper`, post a
`Message`, confirm dispatch via `nativePollOnce()`/`nativeWake()` integration
with Wayland epoll. This 30-minute test prevents days of debugging inside the
full framework where a hang has no visible output.
