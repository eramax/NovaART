# Nova — Comprehensive Plan v4
### Source of Truth · All Phases · Validation · Test Apps

> **What is Nova?**
> Nova is a Linux-native Android application runtime. It runs unmodified Android APK files
> directly on a Linux desktop — with Wayland rendering, PipeWire audio, Vulkan GPU acceleration,
> and host kernel integration — without containers, virtual machines, or Android system images.
>
> Nova is built as a first-class AOSP product. It uses AOSP's own build system (Soong),
> ships as a `vendor/nova` overlay, and is built with a single command:
>
> ```bash
> source build/envsetup.sh && lunch nova-eng && m -j$(nproc) nova
> ```
>
> **Prior work:** The earlier NovaART prototype (proof-of-concept under `deps/NovaART/`) validated
> the core ART bootstrap, GLSurfaceView rendering, TextureView detection, Canvas pipeline,
> BitmapFactory/libpng decoding, and SoundPool stubs. That code is a reference and can be
> partially reused. It is NOT the target architecture — Nova replaces it entirely with the
> AOSP-native approach described here.

---

## 1. Architecture

### 1.1 What Nova Is Not

Nova is NOT:
- A container (no LXC, no Docker, no Waydroid kernel module)
- A virtual machine (no QEMU, no VirtualBox)
- A JVM-based runner (ART ≠ JVM — ART runs DEX bytecode, not Java `.class` files)
- A stub-heavy shim layer (stubs will never make real apps work — see §1.3)

### 1.2 The Full Stack

```
┌─────────────────────────────────────────────────────────────┐
│                    APK  (.dex + .so)                        │
│                   unchanged, unmodified                     │
├─────────────────────────────────────────────────────────────┤
│              AOSP Framework Java (99% unchanged)            │
│   Activity · View · Canvas · MediaPlayer · Animator · ...   │
│   Source: frameworks/base — built by AOSP Soong, unmodified │
├──────────────────────────┬──────────────────────────────────┤
│  nova-framework.jar      │   nova-framework Java delta      │
│  (~12 classes only)      │   Only what diverges from Linux: │
│                          │   WindowManager → Wayland        │
│                          │   AudioManager → PipeWire        │
│                          │   SurfaceView  → wl_egl          │
├──────────────────────────┴──────────────────────────────────┤
│              ART  (libart.so) — AOSP, UNCHANGED             │
│     DEX loading · dex2oat · JIT · GC · class resolution     │
├─────────────────────────────────────────────────────────────┤
│        Bionic linker64 — AOSP, LINKER CONFIG ONLY           │
│   ld.config.nova.txt redirects 4 platform libraries:        │
│   libvulkan.so   → libnova_vulkan.so   (gfxstream)         │
│   libandroid.so  → libnova_android.so  (ANativeWindow/Wl)  │
│   libOpenSLES.so → libnova_audio.so    (PipeWire)          │
│   libEGL.so      → libnova_egl.so      (host EGL)          │
├─────────────────────────────────────────────────────────────┤
│              Linux Kernel + Hardware                        │
│   Wayland compositor · Vulkan driver · PipeWire · KMS       │
│                  COMPLETELY UNCHANGED                       │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 Why Not Stubs

A Java stub that returns `null` or `0` does not let an app run — it lets
an app crash silently or produce wrong output. The prototype proved this:
`ValueAnimator.start()` was a no-op, so Material Life never drew a single
frame. The correct approach is to use **real AOSP implementations** for
everything possible and only override the 4 hardware-boundary libraries
plus the ~12 Java classes that have Linux-specific behaviour.

### 1.4 The Four Override Libraries (Nova's Core Ownership)

| Android library | What apps use it for | Nova replacement | Linux backend |
|---|---|---|---|
| `libandroid.so` | `ANativeWindow`, `AInputQueue`, `AAssetManager` | `libnova_android.so` | Wayland `wl_surface`, `wl_seat` |
| `libvulkan.so` | Vulkan instance + device creation | `libnova_vulkan.so` | gfxstream → host Vulkan driver |
| `libOpenSLES.so` | Audio playback + recording | `libnova_audio.so` | PipeWire |
| `libEGL.so` | EGL display, surface, context | `libnova_egl.so` | host EGL + `wayland-egl` |

Everything else — `libc`, `libm`, `pthread`, `libstdc++`, `liblog` — comes from
Bionic unchanged. On x86_64 Linux, Bionic's syscall ABI is identical to glibc's
because both call into the Linux kernel using the same `syscall` interface.

---

## 2. Repository Structure

### 2.1 AOSP Sync with local_manifest

Nova lives entirely in `vendor/nova/`. AOSP is never forked.

`.repo/local_manifests/nova.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<manifest>
  <remote name="nova" fetch="https://github.com/your-org" />

  <!-- Nova vendor tree — the only repository you maintain -->
  <project name="nova/vendor_nova"
           path="vendor/nova"
           remote="nova"
           revision="main" />

  <!-- gfxstream — Google's Vulkan translation layer (Apache 2.0) -->
  <project name="nova/platform_hardware_google_gfxstream"
           path="hardware/google/gfxstream"
           remote="nova"
           revision="nova-main" />
</manifest>
```

Sync:
```bash
repo init -u https://android.googlesource.com/platform/manifest -b android-15.0.0_r1
repo sync -j$(nproc)
# vendor/nova/ and hardware/google/gfxstream/ are now present
```

### 2.2 vendor/nova/ Layout

```
vendor/nova/
├── Android.bp                    # nova binary
├── vendorsetup.sh                # registers lunch target
├── products/
│   └── nova.mk                   # lunch target definition
├── linker/
│   └── ld.config.nova.txt        # Bionic linker namespace config
├── libnova_android/
│   ├── Android.bp                # overrides: libandroid
│   ├── nova_native_window.c      # ANativeWindow → wl_surface
│   ├── nova_input.c              # AInputQueue → wl_seat
│   └── nova_asset_manager.c      # AAssetManager (reads from APK)
├── libnova_vulkan/
│   ├── Android.bp                # overrides: libvulkan
│   └── nova_vulkan.c             # gfxstream guest encoder init
├── libnova_audio/
│   ├── Android.bp                # overrides: libOpenSLES
│   └── nova_audio.c              # OpenSL ES → PipeWire
├── libnova_egl/
│   ├── Android.bp                # overrides: libEGL
│   └── nova_egl.c                # EGL → host EGL + wayland-egl
├── nova-framework/
│   ├── Android.bp
│   └── src/
│       ├── nova/internal/
│       │   ├── Launcher.java         # APK boot, manifest parse, lifecycle
│       │   ├── RenderCoordinator.java # GLSurfaceView vs TextureView routing
│       │   └── ViewDispatcher.java   # touch/key dispatch
│       └── android/
│           ├── app/Activity.java     # lifecycle → xdg_toplevel
│           ├── view/WindowManager.java # Wayland surface management
│           ├── view/SurfaceView.java
│           ├── view/TextureView.java
│           ├── animation/ValueAnimator.java # Choreographer-driven
│           └── media/AudioTrack.java # PipeWire stream
├── scripts/
│   ├── audit-coverage.py         # APK scanner — finds missing classes/methods
│   ├── smoke-phase1.sh
│   ├── smoke-phase2.sh
│   ├── smoke-phase3.sh
│   └── validate.sh               # runs all atest suites
└── tests/
    ├── nova_unit_tests/
    └── nova_smoke_tests/
```

---

## 3. Build System

### 3.1 Lunch Target

`vendor/nova/vendorsetup.sh`:
```bash
add_lunch_combo nova-eng
add_lunch_combo nova-userdebug
```

`vendor/nova/products/nova.mk`:
```makefile
PRODUCT_NAME   := nova
PRODUCT_DEVICE := nova_host
PRODUCT_BRAND  := Nova
PRODUCT_MODEL  := Nova Android Runtime

PRODUCT_PACKAGES += \
    nova             \
    libnova_android  \
    libnova_vulkan   \
    libnova_audio    \
    libnova_egl      \
    nova-framework

# Replace AOSP platform libraries with Nova's
PRODUCT_PACKAGES_OVERRIDE := \
    libandroid   \
    libvulkan    \
    libOpenSLES  \
    libEGL

include build/make/target/product/base_system.mk
```

### 3.2 Override Mechanism

Each Nova library declares `overrides:` in `Android.bp`. Soong replaces the
named AOSP module in the build graph — no source patching required:

```python
cc_library_shared {
    name: "libnova_android",
    overrides: ["libandroid"],    # ← Soong replaces AOSP's libandroid
    host_supported: true,
    srcs: ["nova_native_window.c", "nova_input.c", "nova_asset_manager.c"],
    shared_libs: ["libwayland-client", "liblog"],
}
```

### 3.3 Linker Namespace (No Bionic Fork Needed)

`vendor/nova/linker/ld.config.nova.txt` controls which library file loads
when an app calls `dlopen("libvulkan.so")`. Bionic's own namespace system
handles the redirect — no Bionic source modification required:

```
[nova_app]
additional.namespaces = nova_platform

namespace.default.links = nova_platform
namespace.default.link.nova_platform.shared_libs = \
    libvulkan.so:libandroid.so:libOpenSLES.so:libEGL.so

namespace.nova_platform.isolated = true
namespace.nova_platform.search.paths = /out/host/linux-x86/lib64/nova
```

### 3.4 Single Build Command

```bash
source build/envsetup.sh
lunch nova-eng
m -j$(nproc) nova
# Result: out/host/linux-x86/bin/nova
```

Run any APK:
```bash
./out/host/linux-x86/bin/nova /path/to/app.apk
```

---

## 4. Phases

Each phase has a concrete gate test. The phase is not complete until the
gate test passes. No exceptions.

---

### Phase 0 — Foundation (2–3 weeks)
**Goal:** AOSP builds, nova binary exists, ART bootstraps, empty window appears.

#### Tasks

**P0-T1: AOSP sync and nova lunch target**
- Write `.repo/local_manifests/nova.xml`
- Write `vendor/nova/vendorsetup.sh` and `vendor/nova/products/nova.mk`
- Verify: `lunch nova-eng` succeeds

**P0-T2: nova binary skeleton**
- Write `vendor/nova/Android.bp` — `cc_binary_host { name: "nova" }`
- Write `src/main.c` — parses APK path from argv, prints "Nova starting"
- Verify: `m -j$(nproc) nova` builds, `./nova test.apk` prints startup log

**P0-T3: ART bootstrap**
- Wire `JNI_CreateJavaVM` call into `main.c` using AOSP's `libart.so`
- Load `nova-framework.jar` on the bootclasspath
- Verify: ART initialises without crash, JNIEnv is valid
- **Reuse:** `src/art.c` from NovaART prototype — the `JNI_CreateJavaVM` setup
  is directly applicable

**P0-T4: Wayland window**
- Write `nova_wayland.c` — `wl_display`, `wl_compositor`, `xdg_toplevel`
- Handle `configure`, `close`, resize callbacks
- Request server-side decorations via `xdg-decoration-unstable-v1`
- libdecor fallback for GNOME
- Set window title from APK's `android:label`, app_id from package name
- **Reuse:** `src/wayland.c` from NovaART prototype is directly applicable

**P0-T5: APK manifest parsing**
- Parse binary AndroidManifest.xml (AXML format) to extract:
  - `package` name
  - Main activity class (action=MAIN category=LAUNCHER)
  - `android:label` for window title
- **Reuse:** `src/art.c` APK parsing logic from NovaART prototype

**P0-T6: Linker namespace config**
- Write `vendor/nova/linker/ld.config.nova.txt`
- Wire `-Xlinker-config` option into `JNI_CreateJavaVM` call
- Verify: `dlopen("libvulkan.so")` in a test JNI method loads `libnova_vulkan.so`

#### Gate Test — Phase 0
```bash
bash vendor/nova/scripts/smoke-phase0.sh
# PASS criteria:
# ✓ nova binary builds with m -j$(nproc) nova
# ✓ ART initialises (JNIEnv valid, no abort)
# ✓ Empty Wayland window appears with correct APK title
# ✓ Window close button exits cleanly (onDestroy log)
# ✓ dlopen("libvulkan.so") loads libnova_vulkan.so (linker config active)
```

#### AOSP Tests to Run
```bash
atest --host art_gtests            # ART on Linux host
atest --host bionic_unit_tests     # Bionic on Linux host
```

---

### Phase 1 — GLES Rendering (2–3 weeks)
**Goal:** OpenGL ES apps render at 60 FPS. GLSurfaceView path is complete.

#### Test App
**`gles3jni`** — AOSP sample. Pure GLES3, no framework dependencies beyond
`GLSurfaceView`. Rotating triangle. This is the canonical GLES gate test.

#### Tasks

**P1-T1: EGL bridge**
- Write `libnova_egl/nova_egl.c`:
  - `eglGetDisplay` → `wl_display` via `wayland-egl`
  - `eglCreateWindowSurface` → `wl_egl_window`
  - `eglSwapBuffers` → Wayland frame commit
- **Reuse:** `src/egl.c` and `src/jni/com_google_android_gles_jni_EGLImpl.c`
  from NovaART prototype

**P1-T2: GLES20/GLES30 JNI bridge**
- `libnova_android/nova_opengl.c` — maps `android.opengl.GLES20` native methods
  to desktop GL calls (1:1 mapping for x86_64)
- **Reuse:** `src/jni/android_opengl_GLES20.c` from NovaART prototype

**P1-T3: GLSurfaceView orchestration**
- `nova-framework/android/opengl/GLSurfaceView.java` — drives GL thread,
  calls `onDrawFrame` on vsync
- `GLSurfaceView` uses Choreographer for vsync timing
- **Reuse:** `src/java/aosp/android/opengl/GLSurfaceView.java` from prototype

**P1-T4: Activity + Application lifecycle**
- `nova-framework/nova/internal/Launcher.java` — reads manifest, instantiates
  Application and Activity, drives onCreate → onResume
- `nova-framework/android/app/Activity.java` — lifecycle methods + content view
- **Reuse:** `src/java/nova-shims/nova/internal/Launcher.java` from prototype,
  needs adaptation to new AOSP build structure

**P1-T5: Native lib staging**
- Extract APK native `.so` into temp dir; replace with host-built version
  for apps where this is valid (gles3jni case)
- Handle `FileAlreadyExistsException` on repeated runs

#### Gate Test — Phase 1
```bash
bash vendor/nova/scripts/smoke-phase1.sh
# PASS criteria:
# ✓ gles3jni.apk: window appears, triangle renders and rotates
# ✓ GL thread runs stable at 60 FPS for 10 seconds
# ✓ No EGL errors in log
# ✓ Window title: "GLES3 JNI" (from APK manifest)
```

#### AOSP Tests to Run
```bash
atest --host art_gtests
atest --host CtsOpenGlTestCases   # GLES conformance on host
```

---

### Phase 2 — Canvas + Touch + Audio + Window Chrome (3–4 weeks)
**Goal:** 2D/Canvas apps render, touch works, sound plays, window has full chrome.

#### Test Apps
| App | What it tests |
|---|---|
| **Pixel Dungeon** (`com.watabou.pixeldungeon`) | GLSurfaceView + SoundPool + touch |
| **Material Life** (`com.juankysoriano.materiallife`) | TextureView + ValueAnimator + Canvas |
| **2048** | Canvas UI + touch + Handler timers |

#### Tasks

**P2-T1: Canvas software rendering pipeline**
- `libnova_android/nova_canvas.c` — `android.graphics.Canvas` JNI methods
  → Skia raster backend → `wl_shm` buffer → Wayland
- Bitmap: `nCreateBitmap`, `nGetPixels`, `nSetPixels`, `eraseColor`
- Paint: `nSetColor`, `nSetStyle`, `nSetStrokeWidth`, `nSetAntiAlias`
- Canvas ops: `drawRect`, `drawCircle`, `drawBitmap`, `drawText`, `save`, `restore`,
  `translate`, `scale`, `rotate`, `clipRect`
- **Reuse:** `src/canvas_render.c`, `src/softgfx.c`,
  `src/jni/android_graphics_Canvas.c` from prototype

**P2-T2: BitmapFactory + image decoding**
- `libnova_android/nova_bitmap_factory.c` — PNG via libpng, JPEG via libjpeg-turbo
- **Reuse:** `src/jni/android_graphics_BitmapFactory.c` from prototype

**P2-T3: TextureView path**
- `nova-framework/android/view/TextureView.java`
- `nova-framework/android/graphics/SurfaceTexture.java`
- `nova-framework/android/view/Surface.java` — `lockCanvas` / `unlockCanvasAndPost`
  routing through `CanvasRender.submitFrame`
- **Reuse:** TextureView + SurfaceTexture + Surface shims from prototype

**P2-T4: ValueAnimator + ObjectAnimator**
- `nova-framework/android/animation/ValueAnimator.java`
  — drives frame loop via `Choreographer.postFrameCallback`
  — `start()`, `cancel()`, `setDuration()`, `addUpdateListener()`
  — `ofFloat()`, `ofInt()`, `ofArgb()` static factories
- `nova-framework/android/animation/ObjectAnimator.java`
  — extends ValueAnimator, uses reflection to call setter on target
- **Reuse:** Implement fresh; prototype stubs are insufficient

**P2-T5: Wayland pointer/touch → MotionEvent dispatch**
- `nova_wayland.c`: add `wl_pointer_listener` (button, motion) and
  `wl_touch_listener` (down, up, motion)
- `nova-framework/nova/internal/ViewDispatcher.java`:
  `dispatchTouch(action, x, y)` → `MotionEvent.obtain()` → `rootView.dispatchTouchEvent()`
- `nova-framework/android/view/MotionEvent.java` — `obtain()`, `getX()`, `getY()`,
  `getAction()`, `recycle()`
- **Reuse:** Wayland input listener structure from prototype's `wayland.c`

**P2-T6: Keyboard → KeyEvent dispatch**
- `nova_wayland.c`: `wl_keyboard_listener.key` → `ViewDispatcher.dispatchKey()`
- `nova-framework/android/view/KeyEvent.java` — constructor + getters
- `Activity.dispatchKeyEvent()` → content view dispatch

**P2-T7: PipeWire audio — SoundPool + MediaPlayer**
- `libnova_audio/nova_audio.c`:
  - `nova_audio_init()` — initialise PipeWire
  - `nova_sound_load(path)` — decode PCM via libsndfile, return sound ID
  - `nova_sound_play(id, lv, rv, loop, rate)` — create `pw_stream`, write PCM
  - `nova_media_player_start(path)` — loop stream for background music
- `libnova_audio/nova_opensles.c` — OpenSL ES `slCreateEngine` dispatch
- `nova-framework/android/media/SoundPool.java` — route to JNI
- `nova-framework/android/media/MediaPlayer.java` — route to JNI
- **Reuse:** `src/audio.c` design from Phase 2 task file; rewrite against
  AOSP build system

**P2-T8: Window chrome — title, close, maximize, resize**
- Set `xdg_toplevel_set_title` and `xdg_toplevel_set_app_id` from manifest
- Handle `xdg_toplevel_close` → `Launcher.dispatchDestroy()` → `Activity.onDestroy()`
- Handle `xdg_toplevel_configure` (width/height) → `Activity.onConfigurationChanged()`
- libdecor fallback for GNOME compositors
- **Reuse:** decoration work from prototype's `wayland.c`

**P2-T9: Handler.postDelayed — real timer scheduling**
- `nova-framework/android/os/Handler.java`:
  - `postDelayed(Runnable, delay)` — enqueue with deadline
  - `post(Runnable)` — enqueue with delay=0
  - `removeCallbacks(Runnable)` — cancel pending
- `nova-framework/android/os/MessageQueue.java`:
  - sorted queue by deadline
  - sleep for `min(next_deadline - now, 16ms)` between dispatches
- **Reuse:** Implement fresh; prototype implementation needs deadline sorting

**P2-T10: Coverage audit**
- Run `vendor/nova/scripts/audit-coverage.py apks/phase2/ --out out/gaps.md`
- Fix all gaps with danger rating `!` (object-returning methods)
- Fix all gaps with danger rating `⚠` used by 2+ test APKs

#### Gate Test — Phase 2
```bash
bash vendor/nova/scripts/smoke-phase2.sh
# PASS criteria:
# ✓ Pixel Dungeon: renders dungeon view, hero visible
# ✓ Pixel Dungeon: touch on tile → hero moves to that tile
# ✓ Pixel Dungeon: sound effect plays on item pickup
# ✓ Material Life: cellular automaton animation runs (frames updating)
# ✓ 2048: tile grid renders, swipe gesture moves tiles
# ✓ All apps: window title matches app name
# ✓ All apps: close button exits cleanly
# ✓ All apps: window maximize resizes content
```

---

### Phase 3 — Text, Resources, Broader App Support (4–6 weeks)
**Goal:** Apps with real UI layouts, text rendering, and resource files work.

#### Test Apps
| App | What it tests |
|---|---|
| **Simple Calculator** | TextView, EditText, Button layout |
| **KeePassDX** | Complex layout, Typeface, RecyclerView |
| **NewPipe** | Network (OkHttp stub), ListView, fragments |
| **VLC for Android** | MediaCodec, SurfaceView, hardware decode |

#### Tasks

**P3-T1: FreeType + HarfBuzz text rendering**
- `libnova_android/nova_text.c` — FreeType font loading, HarfBuzz shaping
- `android.graphics.Typeface` — load from APK assets or system fonts
- `android.graphics.Paint` text metrics: `measureText`, `getTextBounds`
- `Canvas.drawText()` → shaped glyphs → Skia glyph atlas

**P3-T2: Layout inflation — full XML parser**
- `android.view.LayoutInflater` — parse compiled XML resource (AXML format)
- Instantiate view classes: `TextView`, `EditText`, `Button`, `ImageView`,
  `LinearLayout`, `RelativeLayout`, `FrameLayout`, `ConstraintLayout` (basic)
- Apply attributes: `layout_width`, `layout_height`, `gravity`, `padding`,
  `margin`, `background`, `textSize`, `textColor`

**P3-T3: Resource system**
- `android.content.res.Resources` — resolve `@string/`, `@color/`, `@dimen/`,
  `@layout/`, `@drawable/` by reading compiled `resources.arsc` from APK
- `android.content.res.AssetManager` — open raw assets from APK zip

**P3-T4: RecyclerView + Adapter framework**
- `android.support.v7.widget.RecyclerView` (AndroidX) — view recycling,
  `LinearLayoutManager`
- `android.widget.ListView` — classic list view with adapter

**P3-T5: Fragment framework**
- `android.app.Fragment` and `android.app.FragmentManager` (AOSP — use as-is)
- `androidx.fragment.app.Fragment` (AndroidX — ship full AndroidX fragment jar)

**P3-T6: Network stubs**
- `android.net.ConnectivityManager` — returns "WiFi connected"
- `java.net.HttpURLConnection` — delegate to host `libcurl` or Java's own
  network stack (ART includes this)
- OkHttp — no stub needed; OkHttp ships in the APK and uses Java sockets

**P3-T7: MediaCodec — hardware video decode**
- `android.media.MediaCodec` → V4L2 on Linux for hardware decode,
  or FFmpeg software fallback
- `android.media.MediaExtractor` → FFmpeg demuxer

#### Gate Test — Phase 3
```bash
bash vendor/nova/scripts/smoke-phase3.sh
# PASS criteria:
# ✓ Simple Calculator: renders buttons, accepts digit input, computes result
# ✓ KeePassDX: database list screen renders with text and icons
# ✓ NewPipe: home screen renders (network optional — offline mode accepted)
# ✓ VLC: opens and plays a local video file with audio
```

---

### Phase 4 — ARM64 Native Libraries via FEX-Emu (6–10 weeks)
**Goal:** ARM64 `.so` files inside APKs execute on x86_64 Linux.

This phase is required for any app that ships native C/C++ code compiled
for ARM64 (games, Chromium-based browsers, anything using NDK).

#### What Is FEX-Emu

FEX-Emu is an open-source ARM64→x86_64 userspace emulator. It runs ARM64
ELF binaries inside a Linux process on x86_64. It handles:
- ARM64 → x86_64 instruction translation (JIT)
- Bionic syscall ABI → Linux syscall translation
- ARM64 TLS layout → x86_64 TLS layout

#### Integration Strategy

When Nova extracts an APK's `lib/arm64-v8a/*.so`, it runs those libraries
through FEX-Emu's library mode (`FEXInterpreter`) rather than loading them
directly. The linker namespace config is extended:

```
namespace.nova_arm64.path = /path/to/fex/rootfs
namespace.nova_arm64.type = fex-interpreted
```

#### Test Apps
| App | What it tests |
|---|---|
| **glmark2.apk** | ARM64 native GLES benchmark (first ARM64 gate) |
| **Godot game** | ARM64 + GLES3 |
| **Genshin Impact** (lite) | ARM64 + Vulkan, heavy native |

#### Gate Test — Phase 4
```bash
bash vendor/nova/scripts/smoke-phase4.sh
# PASS criteria:
# ✓ glmark2.apk: benchmark runs, FPS score reported, no Bionic LIBC version error
# ✓ A Godot-based ARM64 game renders and accepts input
```

---

### Phase 5 — Vulkan via gfxstream (6–10 weeks)
**Goal:** Apps using Vulkan render using the host GPU (NVIDIA 3090 or AMD iGPU).

#### What Is gfxstream

gfxstream is Google's own Vulkan/GLES translation layer used in Android
Studio's emulator and ChromeOS's ARCVM. The guest encoder serialises Vulkan
calls; the host decoder submits them to the real Vulkan driver.

In Nova's case, guest and host are the **same process** — the transport is
a direct function call, making it faster than even real Android hardware.

#### Integration

`libnova_vulkan.so` = gfxstream guest encoder linked against host Vulkan:

```
App calls vkCreateInstance
    → libnova_vulkan.so (gfxstream guest)
    → direct call (no virtio-gpu, same process)
    → gfxstream host decoder
    → host Vulkan loader (libvulkan.so system)
    → NVIDIA 3090 Vulkan 1.3 driver
```

#### Test Apps
| App | What it tests |
|---|---|
| **Genshin Impact** | Full Vulkan game, ARM64 + gfxstream |
| **VLC** | Vulkan video output path |
| **MX Player Pro** | Hardware decode + Vulkan compositor |

#### Gate Test — Phase 5
```bash
bash vendor/nova/scripts/smoke-phase5.sh
# PASS criteria:
# ✓ vkCreateInstance succeeds via gfxstream
# ✓ vkEnumeratePhysicalDevices returns NVIDIA 3090
# ✓ A Vulkan triangle renders without Mesa CPU fallback
# ✓ Genshin Impact reaches the login screen
```

---

### Phase 6 — AAA Games + Media Players (ongoing)
**Goal:** Production-quality experience for demanding apps.

This phase has no single gate test — it is an ongoing compatibility effort
driven by the coverage audit tool and user-reported issues.

#### Priority App List
| App | Category | Blockers expected |
|---|---|---|
| Genshin Impact | AAA game | ARM64 + Vulkan + anti-tamper |
| PUBG Mobile | AAA game | Anti-cheat (likely impossible) |
| MX Player Pro | Media player | MediaCodec V4L2 |
| VLC for Android | Media player | MediaCodec, subtitles |
| YouTube | Video streaming | Widevine DRM (out of scope) |
| Spotify | Music | ARM64 native |
| Chrome | Browser | Chromium renderer process |

**Anti-cheat note:** PUBG Mobile uses BattlEye — a kernel-level anti-cheat.
Nova cannot and will not attempt to bypass it. This is explicitly out of scope.

---

## 5. Validation Process

### 5.1 Per-Phase Gate Tests

Each phase's smoke script is the gating criterion. No phase is marked
complete until its smoke script exits 0. Scripts live in
`vendor/nova/scripts/` and are part of the repository.

Smoke script contract:
```bash
# Every smoke script must:
# 1. Build nova if not built
# 2. Launch the test APK
# 3. Assert specific log lines appear within timeout
# 4. Exit 0 on PASS, 1 on FAIL with a clear failure message
```

### 5.2 AOSP Test Suites (Run After Every AOSP Version Bump)

```bash
# Core runtime — must always pass
atest --host art_gtests
atest --host bionic_unit_tests

# Framework — must pass for all classes Nova inherits unchanged
atest --host framework-core-tests

# Nova-specific — owned by vendor/nova/tests/
atest --host nova_unit_tests
atest --host nova_smoke_tests
```

### 5.3 Coverage Audit (Run Before Every Session)

```bash
python3 vendor/nova/scripts/audit-coverage.py apks/phase-N/ --out out/gaps.md
```

The audit tool (from prior NovaART work, already complete) scans APK files
and compares their class/method references against `nova-framework/src/`.
Output is ranked by: APK call frequency × danger level (object > primitive > void).

Fix policy:
- `!` (object return): **must fix before moving to next task**
- `⚠` (primitive return): fix if used by 2+ test APKs
- ` ` (void return): fix if critical path, otherwise log TODO

### 5.4 Upgrade Validation (AOSP Version Bumps)

```bash
# 1. Sync new AOSP tag
repo init -b android-16.0.0_r1 && repo sync -j$(nproc)

# 2. Rebuild
m -j$(nproc) nova

# 3. Run all AOSP + Nova tests
bash vendor/nova/scripts/validate.sh

# 4. If tests fail, the failure points are the exact API delta between
#    Android versions. Fix only vendor/nova/ — never touch AOSP.
```

### 5.5 progress.md — Engineering Log

`vendor/nova/progress.md` is a hand-maintained engineering log.
Every task completion must be recorded with:

```markdown
## YYYY-MM-DD HH:MM TZ

Milestone: <one sentence>

Completed:
- <file changed>: <what was done>

Verified:
- <command run>: <observed result>

Current state:
- <what works now>

Next:
- <very next step>
```

---

## 6. What Nova Owns vs What AOSP Owns

| Component | Owner | Lines of code estimate |
|---|---|---|
| ART (`libart.so`) | AOSP — not touched | 0 |
| Bionic (libc, libm, pthread) | AOSP — not touched | 0 |
| frameworks/base (99% of Java API) | AOSP — not touched | 0 |
| `libnova_android.so` | Nova | ~1,500 |
| `libnova_vulkan.so` | Nova (gfxstream wrapper) | ~500 |
| `libnova_audio.so` | Nova | ~800 |
| `libnova_egl.so` | Nova | ~400 |
| `nova-framework.jar` (Java delta) | Nova | ~3,000 |
| `nova` binary (main, wayland, art bootstrap) | Nova | ~2,000 |
| Build system (products, Android.bp files) | Nova | ~300 |
| Scripts (audit, smoke, validate) | Nova | ~600 |
| **Total Nova code** | | **~9,100 lines** |

Google maintains the remaining millions of lines. Nova only owns the
hardware-boundary junction and the ~12 Java classes with Linux-specific behaviour.

---

## 7. Reuse from Prior NovaART Prototype

The following files from `deps/NovaART/` are directly applicable and
should be the starting reference for Nova implementation:

| Prototype file | Reuse in Nova | Notes |
|---|---|---|
| `src/art.c` | `nova/src/art.c` | ART bootstrap + APK manifest parsing |
| `src/wayland.c` | `libnova_android/nova_wayland.c` | Wayland + EGL + decoration — adapt to new build |
| `src/egl.c` | `libnova_egl/nova_egl.c` | Host EGL bridge |
| `src/canvas_render.c` | `libnova_android/nova_canvas_render.c` | wl_shm buffer management |
| `src/softgfx.c` | `libnova_android/nova_softgfx.c` | Pixel buffer ops |
| `src/jni/android_graphics_BitmapFactory.c` | `libnova_android/nova_bitmap_factory.c` | libpng decode |
| `src/jni/android_opengl_GLES20.c` | `libnova_android/nova_gles20.c` | GLES20 bridge |
| `src/jni/android_opengl_GLUtils.c` | `libnova_android/nova_glutils.c` | Texture upload |
| `src/java/nova-shims/nova/internal/Launcher.java` | `nova-framework/nova/internal/Launcher.java` | APK launch + lifecycle |
| `src/java/nova-shims/android/view/TextureView.java` | `nova-framework/android/view/TextureView.java` | TextureView + SurfaceTexture |
| `src/java/aosp/android/opengl/GLSurfaceView.java` | Keep as-is | GLSurfaceView works |
| `scripts/audit-coverage.py` | `vendor/nova/scripts/audit-coverage.py` | Already complete |

Everything else should be implemented fresh using the AOSP build system
rather than ported from the prototype's meson/hand-built structure.

---

## 8. Out of Scope (Explicitly Deferred)

These items must NOT be worked on in any phase without an explicit plan
update:

- WebView / CEF browser engine
- Widevine DRM
- PUBG anti-cheat bypass
- Google Play Services
- Camera (V4L2 is Phase 3+ research, not committed)
- Bluetooth / NFC / telephony
- Multi-user / sandboxing
- ARM64 AOT compilation (FEX-Emu JIT is sufficient for Phase 4)

---

## 9. Hardware Reference

| Hardware | Role in Nova |
|---|---|
| AMD iGPU | Primary EGL/GLES display (Phase 1–3) |
| NVIDIA RTX 3090 | Primary Vulkan GPU (Phase 5+), 24 GB VRAM |
| Linux kernel | Must be ≥ 5.15 for PipeWire + Wayland stability |
| PipeWire | Audio backend (Phase 2+), must be ≥ 0.3.48 |
| Wayland compositor | Any wlroots-based (sway, river, KDE, GNOME + XWG) |

For Vulkan (Phase 5), the 3090 is the primary target. NVIDIA's Vulkan 1.3
driver on Linux is production quality. gfxstream is validated against it
by Google in ARCVM / ChromeOS.

---

## 10. Milestone Summary

| Phase | Duration | Gate App | Key Deliverable |
|---|---|---|---|
| 0 — Foundation | 2–3 weeks | (empty window) | AOSP builds, ART boots, Wayland window |
| 1 — GLES | 2–3 weeks | gles3jni | OpenGL apps render at 60 FPS |
| 2 — Canvas+Audio | 3–4 weeks | Pixel Dungeon, Material Life | 2D apps + touch + sound |
| 3 — Text+Resources | 4–6 weeks | Calculator, KeePassDX | UI layout + text rendering |
| 4 — ARM64 | 6–10 weeks | glmark2 ARM64 | FEX-Emu + Bionic native libs |
| 5 — Vulkan | 6–10 weeks | Vulkan triangle, Genshin | gfxstream + 3090 GPU |
| 6 — AAA | ongoing | Genshin, MX Player | Production quality |
