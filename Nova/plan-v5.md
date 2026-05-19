# Nova — v5 Implementation Plan

## Overview

Nova is a Linux-native Android application runtime. It runs unmodified Android APKs
directly on a Linux desktop — with Wayland rendering, PipeWire audio, and host GPU
acceleration — without containers, virtual machines, or Android system images.

Nova is built as a first-class AOSP product. It lives in `vendor/nova/`, uses AOSP's
Soong build system, and overrides only 4 hardware-boundary native libraries plus a
Java framework delta. Everything else (ART, Bionic, 99% of `frameworks/base`) is
stock AOSP.

---

## Repository Structure

### AOSP Layout

Nova is a single git repository at `vendor/nova/`. AOSP is never forked.

`vendor/nova/`:
```
vendor/nova/
├── Android.bp                    # Module definitions
├── vendorsetup.sh                # Registers lunch target
├── products/
│   └── nova.mk                   # Product definition
├── linker/
│   └── ld.config.nova.txt        # Bionic linker namespace config
├── libnova_android/
│   ├── Android.bp
│   ├── nova_native_window.c      # ANativeWindow → wl_surface
│   ├── nova_input.c              # AInputQueue → wl_seat
│   ├── nova_asset_manager.c      # AAssetManager (reads from APK)
│   └── nova_wayland.c            # Wayland display + globals + xdg-toplevel
├── libnova_egl/
│   ├── Android.bp
│   └── nova_egl.c                # EGL → host EGL + wayland-egl
├── libnova_audio/
│   ├── Android.bp
│   ├── nova_audio.c              # OpenSL ES → PipeWire
│   └── nova_aaudio.c             # AAudio → PipeWire
├── libnova_vulkan/
│   ├── Android.bp
│   └── nova_vulkan.c             # gfxstream guest encoder init
├── nova-framework/
│   ├── Android.bp
│   └── src/
│       ├── nova/internal/
│       │   ├── Launcher.java         # APK boot, manifest, lifecycle
│       │   ├── CanvasRender.java     # JNI bridge for frame submission
│       │   ├── RenderCoordinator.java
│       │   └── ViewDispatcher.java   # Touch/key dispatch
│       └── android/
│           ├── app/Activity.java
│           ├── app/Application.java
│           ├── view/View.java
│           ├── view/ViewGroup.java
│           ├── view/SurfaceView.java
│           ├── view/TextureView.java
│           ├── view/WindowManager.java
│           ├── graphics/Canvas.java
│           ├── graphics/Bitmap.java
│           ├── graphics/Paint.java
│           ├── animation/ValueAnimator.java
│           ├── animation/ObjectAnimator.java
│           ├── os/Handler.java
│           ├── os/Looper.java
│           ├── os/MessageQueue.java
│           ├── media/SoundPool.java
│           ├── media/MediaPlayer.java
│           └── ... (~50–100 more as needed)
├── scripts/
│   ├── audit-coverage.py
│   ├── smoke-phase0.sh
│   ├── smoke-phase1.sh
│   ├── smoke-phase2.sh
│   └── smoke-phase3.sh
└── tests/
    ├── nova_unit_tests/
    └── nova_smoke_tests/
```

### Wrapper libraries (the 4 overrides)

Each Nova library overrides the corresponding AOSP module via Soong's `overrides:` stanza:

```python
cc_library_shared {
    name: "libnova_android",
    overrides: ["libandroid"],
    host_supported: true,
    ...
}
```

### Linker namespace

`ld.config.nova.txt` redirects 4 platform libraries:

```
libvulkan.so   → libnova_vulkan.so   (gfxstream)
libandroid.so  → libnova_android.so  (ANativeWindow/Wayland)
libOpenSLES.so → libnova_audio.so    (PipeWire)
libEGL.so      → libnova_egl.so      (host EGL)
```

### Dependencies (on disk)

| Dependency | Location | Branch | Purpose |
|---|---|---|---|
| AOSP ART build tree | `deps/aosp-full` | `master-art` | Builds `libart.so`, `dex2oat`, boot image |
| AOSP framework source | `deps/aosp-framework-src` | `android16-qpr2-release` | Source input for framework overlay |

Both already synced and built. ART host build artifacts are at
`deps/aosp-full/out/host/linux-x86/` (18GB). Framework sources are at
`deps/aosp-framework-src/frameworks/base/`.

---

## Single Build Command

```bash
source build/envsetup.sh
lunch nova-eng
m -j$(nproc) nova
```

Result: `out/host/linux-x86/bin/nova`

Run any APK:
```bash
./out/host/linux-x86/bin/nova /path/to/app.apk
```

---

## Phase 0 — Foundation (Weeks 1–3)

**Goal:** AOSP build system produces a `nova` binary that boots ART and opens a
Wayland window.

### Tasks

#### P0-T1: Create vendor/nova/ skeleton

Create the `vendor/nova/` directory tree with:
- `vendorsetup.sh` — registers `nova-eng` and `nova-userdebug`
- `products/nova.mk` — defines `PRODUCT_PACKAGES` including `nova`, `libnova_android`,
  `libnova_egl`, `libnova_audio`, `nova-framework`
- `Android.bp` root — empty top-level blueprint

Verify: `lunch nova-eng` succeeds.

#### P0-T2: Port `nova` binary to Soong

Write `vendor/nova/Android.bp` — a `cc_binary_host { name: "nova" }` that:
- Parses APK path from argv
- Connects to Wayland display
- Calls `JNI_CreateJavaVM` from host `libart.so`
- Enters Wayland dispatch loop

Sources directly ported from prototype (`deps/NovaART/src/`):
- `src/art.c` → `nova/src/art.c` — ART bootstrap, APK manifest parsing
- `src/wayland.c` → `libnova_android/nova_wayland.c` — Wayland globals, xdg-toplevel,
  registry listener, pointer/keyboard input
- `src/egl.c` → `libnova_egl/nova_egl.c` — wl_egl_window, EGL display/config/surface
- `src/main.c` → `nova/src/main.c` — entry point, event loop

Add host `libwayland-client`, `libwayland-egl`, `libEGL`, `libGLESv2` as shared lib
dependencies.

Verify: `m -j$(nproc) nova` builds successfully.

#### P0-T3: Wire ART bootstrap

In `nova/src/art.c`:
- `dlopen("libart.so")` at runtime
- Set `ANDROID_ROOT`, `ANDROID_ART_ROOT`, `ANDROID_I18N_ROOT`, `ANDROID_TZDATA_ROOT`,
  `ANDROID_DATA` environment variables
- Build bootclasspath from staged APEX jars:
  `core-oj.jar`, `core-libart.jar`, `okhttp.jar`, `bouncycastle.jar`,
  `apache-xml.jar`, `core-icu4j.jar`, `conscrypt.jar`
- Call `JNI_CreateJavaVM`
- Return valid `JavaVM*` and `JNIEnv*`

Verify: ART initialises without crash, JNIEnv is valid.

#### P0-T4: Create Wayland window

In `libnova_android/nova_wayland.c`:
- Connect to Wayland display
- Bind `wl_compositor`, `xdg_wm_base`, `wl_shm`, `wl_seat`
- Create `xdg_toplevel` surface
- Set window title from APK `android:label`
- Request server-side decorations via `xdg-decoration-unstable-v1`
- Handle `configure`, `close`, resize
- Dispatch pointer + keyboard events

Verify: Empty Wayland window appears with correct APK title.

#### P0-T5: APK manifest parsing

Parse binary `AndroidManifest.xml` (AXML format) to extract:
- `package` name
- Main activity class (action=MAIN, category=LAUNCHER)
- `android:label` for window title

Reuse prototype's aapt2-based detection in `src/art.c`.

#### P0-T6: Linker namespace config

Write `vendor/nova/linker/ld.config.nova.txt`:
```
[nova_app]
additional.namespaces = nova_platform

namespace.default.links = nova_platform
namespace.default.link.nova_platform.shared_libs = \
    libvulkan.so:libandroid.so:libOpenSLES.so:libEGL.so

namespace.nova_platform.isolated = true
namespace.nova_platform.search.paths = /out/host/linux-x86/lib64/nova
```

Wire `-Xlinker-config` option into JNI_CreateJavaVM call.

Verify: `dlopen("libvulkan.so")` in a test JNI method loads `libnova_vulkan.so`.

#### Gate Test — Phase 0

```bash
bash vendor/nova/scripts/smoke-phase0.sh
# PASS:
# ✓ m -j$(nproc) nova succeeds
# ✓ ART initialises (JNIEnv valid, no abort)
# ✓ Empty Wayland window appears with correct APK title
# ✓ Window close button exits cleanly
# ✓ dlopen("libvulkan.so") loads libnova_vulkan.so
```

---

## Phase 1 — GLES Rendering Port (Weeks 3–5)

**Goal:** Port the prototype's working GLES stack into the new Soong build.

The prototype already has fully functional GLES rendering (gles3jni at 60 FPS).
Phase 1 is primarily a **porting effort** — moving working code from Meson to Soong.

### Tasks

#### P1-T1: Port JNI bridge C files to Soong

Move from `deps/NovaART/src/jni/` to `vendor/nova/libnova_*/`:

| Prototype file | Destination | Role |
|---|---|---|
| `com_google_android_gles_jni_EGLImpl.c` | `libnova_egl/egl_jni.c` | EGL10 native methods |
| `com_google_android_gles_jni_GLImpl.c` | `libnova_egl/gl_jni.c` | GL10 native methods |
| `android_opengl_GLES20.c` | `libnova_android/gles20_jni.c` | GLES20 native methods |
| `android_opengl_GLUtils.c` | `libnova_android/glutils_jni.c` | GL util native methods |
| `android_runtime.c` | `libnova_android/jni_registration.c` | Central gRegJNI table |

Add corresponding `Android.bp` entries with `shared_libs: ["libart", "libnativehelper"]`.

#### P1-T2: Port the Java framework overlay

Move Java sources from `deps/NovaART/src/java/nova-shims/` and
`deps/NovaART/src/java/aosp/` into `vendor/nova/nova-framework/src/`.

This includes:
- All 125 existing shim classes
- AOSP `GLSurfaceView.java`, `EGLImpl.java`, EGL interfaces
- AIDL-generated stubs (7 interfaces)

Build as `android_app` or `dex_import` in Soong. Load on bootclasspath.

#### P1-T3: Port Canvas + softgfx C code

| Prototype file | Destination |
|---|---|
| `softgfx.c` / `softgfx.h` | `libnova_android/nova_softgfx.c` |
| `canvas_render.c` / `canvas_render.h` | `libnova_android/nova_canvas_render.c` |
| `android_graphics_Canvas.c` | `libnova_android/canvas_jni.c` |
| `android_graphics_Bitmap.c` | `libnova_android/bitmap_jni.c` |
| `android_graphics_Paint.c` | `libnova_android/paint_jni.c` |
| `android_graphics_BitmapFactory.c` | `libnova_android/bitmapfactory_jni.c` |
| `nova_canvas_render.c` | `libnova_android/canvas_render_jni.c` |

#### P1-T4: Port input JNI

| Prototype file | Destination |
|---|---|
| `android_view_KeyEvent.c` | `libnova_android/keyevent_jni.c` |
| `android_view_MotionEvent.c` | `libnova_android/motionevent_jni.c` |

#### P1-T5: Port remaining JNI modules

| Prototype file | Destination |
|---|---|
| `android_os_SystemProperties.c` | `libnova_android/sysprop_jni.c` |
| `android_os_SystemClock.c` | `libnova_android/clock_jni.c` |
| `android_os_Binder.c` | `libnova_android/binder_jni.c` |
| `android_os_Process.c` | `libnova_android/process_jni.c` |
| `com_android_internal_graphics_NativeUtils.c` | `libnova_android/nativeutils_jni.c` |

#### Gate Test — Phase 1

```bash
bash vendor/nova/scripts/smoke-phase1.sh
# PASS:
# ✓ gles3jni.apk: window appears, triangle renders and rotates
# ✓ GL thread runs stable at 60 FPS for 10 seconds
# ✓ No EGL errors
# ✓ Window title: "GLES3 JNI" (from APK manifest)
# ✓ 2048.apk: Canvas renders at 60 FPS
# ✓ Touch/swipe works on 2048
```

---

## Phase 2 — Animation + Audio + TextureView (Weeks 5–9)

**Goal:** Unblock animated apps (Material Life) and add audio. Fix the gaps that
the prototype demonstrated.

### Tasks

#### P2-T1: Wire ValueAnimator + ObjectAnimator properly

The prototype has stub ValueAnimator/ObjectAnimator that are no-ops. This blocks
Material Life and any app using animation-driven rendering.

Replace with real implementations:
- `nova-framework/android/animation/ValueAnimator.java`
  - `start()` registers a `Choreographer.postFrameCallback()` listener
  - `cancel()` removes the callback
  - `setDuration(long)` stores duration
  - `addUpdateListener(AnimatorUpdateListener)` stores listener
  - Frame callback: interpolate value based on elapsed time, call listener, post next frame
- `nova-framework/android/animation/ObjectAnimator.java`
  - Extends ValueAnimator
  - Uses reflection to call setter on target object with animated value
  - `ofFloat(target, "alpha", 0f, 1f)` → calls `target.setAlpha(float)`
- `nova-framework/android/animation/AnimatorSet.java`
  - Manages multiple animators with playTogether/playSequentially

#### P2-T2: Fix TextureView render path

Material Life's render loop is detected but not drawing frames. Root cause:
the app drives rendering via animation (ValueAnimator), not via Surface lifecycle
callbacks.

- Ensure `Surface(SurfaceTexture)` constructor is called when render thread starts
- Wire `lockCanvas()` / `unlockCanvasAndPost()` through `CanvasRender.submitFrame()`
- Ensure `onSurfaceTextureAvailable` callback fires and triggers the app's render setup

#### P2-T3: Wire PipeWire audio

Write `libnova_audio/nova_audio.c`:
- `nova_audio_init()` — initialise PipeWire context + main loop
- `nova_sound_load(path)` — decode PCM via libsndfile, return sound ID
- `nova_sound_play(id, left_vol, right_vol, loop, rate)` — create `pw_stream`, write PCM data
- `nova_sound_stop(id)`, `nova_sound_unload(id)`
- `nova_media_player_start(path)` — loop stream for background music

Wire OpenSL ES shim (`libnova_audio/nova_opensles.c`):
- `slCreateEngine` → init PipeWire context
- `SLresult (*Realize)(...)` → return success
- `SLEngineItf (*CreateAudioPlayer)(...)` → route to sound pool

Wire AAudio shim (`libnova_audio/nova_aaudio.c`):
- `AAudio_createStreamBuilder` → return builder struct with PipeWire params
- `AAudioStreamBuilder_openStream` → create `pw_stream`
- `AAudioStream_requestStart` → start PipeWire stream
- `AAudioStream_write` → write PCM to PipeWire buffer

#### P2-T4: Complete SoundPool + MediaPlayer Java stubs

- `nova-framework/android/media/SoundPool.java`
  - `load(Context, int, int)` → calls JNI `nova_sound_load`
  - `play(int, float, float, int, int, float)` → calls JNI `nova_sound_play`
  - `stop(int)`, `pause(int)`, `resume(int)`, `release()`, `autoPause()`, `autoResume()`
  - `setVolume(int, float, float)`, `setRate(int, float)`
- `nova-framework/android/media/MediaPlayer.java`
  - `setDataSource(String)` / `setDataSource(Context, Uri)`
  - `prepare()` / `start()` / `stop()` / `pause()` / `seekTo(int)`
  - `setLooping(boolean)`, `isPlaying()`, `getDuration()`, `getCurrentPosition()`
  - `setOnCompletionListener`, `setOnPreparedListener`, `setOnErrorListener`

#### P2-T5: Fix repeated-run native-lib staging

The prototype fails on repeated runs with `FileAlreadyExistsException` when
native libs are already extracted. Fix: remove existing native libs before
re-extracting (or check existence first).

#### P2-T6: Remove temporary debug logging

Remove verbose debug logging from `Launcher.java`, `GLSurfaceView.java`, and
`CanvasRender.java` that was added during prototype debugging.

### Gate Test — Phase 2

```bash
bash vendor/nova/scripts/smoke-phase2.sh
# PASS:
# ✓ Material Life: cellular automaton animation runs (frames updating)
# ✓ Pixel Dungeon: dungeon view renders, hero visible
# ✓ Pixel Dungeon: touch on tile → hero moves
# ✓ Pixel Dungeon: sound plays on item pickup
# ✓ 2048: tile grid renders, swipe gesture moves tiles
# ✓ All apps: window title matches app name
# ✓ All apps: close button exits cleanly
```

---

## Phase 3 — Text + Resources + Broader App Support (Weeks 9–16)

**Goal:** Apps with real UI layouts, text rendering, and resource files work.

### Tasks

#### P3-T1: FreeType + HarfBuzz text rendering

Write `libnova_android/nova_text.c`:
- `nova_text_init()` — init FreeType library
- `nova_typeface_load(path)` — load font from APK assets or system fonts
- `nova_text_measure(text, typeface, size)` — measure text width via HarfBuzz shaping
- `nova_text_draw(canvas, text, x, y, paint)` — shape text, render glyphs to canvas
- `nova-framework/android/graphics/Typeface.java` — `create(String, int)`, `DEFAULT`, `MONOSPACE`, `SANS_SERIF`, `SERIF`
- `nova-framework/android/graphics/Paint.java` — `measureText(String)`, `getTextBounds(...)`, `setTextSize(float)`, `setTypeface(Typeface)`, `setFakeBoldText(boolean)`
- Wire `Canvas.drawText()` to shaped glyph path through softgfx

#### P3-T2: Layout inflation with full AXML parsing

Write `nova-framework/android/view/LayoutInflater.java`:
- `inflate(int resource, ViewGroup root)` — parse compiled AXML resource
- `inflate(XmlPullParser, ViewGroup root, boolean attachToRoot)` — recursive view creation
- Instantiate views: `TextView`, `Button`, `ImageView`, `LinearLayout`, `FrameLayout`,
  `RelativeLayout`, `EditText`, `CheckBox`, `RadioButton`, `Spinner`
- Apply attributes: `layout_width`, `layout_height`, `gravity`, `padding`, `margin`,
  `background`, `textSize`, `textColor`, `textStyle`, `ellipsize`, `maxLines`, `inputType`

Wire into `Activity.setContentView(int)` to load layout resource by ID.

Write resource parser (`nova-framework/android/content/res/ResourceManager.java`):
- Parse `resources.arsc` from APK (resource table binary format)
- Resolve `@string/name`, `@color/name`, `@dimen/name`, `@layout/name`, `@drawable/name`
- Support configuration qualifiers: `-land`, `-v21`, `-xhdpi`, `-night`

#### P3-T3: AssetManager for APK assets

Write `nova-framework/android/content/res/AssetManager.java`:
- Open assets from APK zip via `ZipFile` API
- `open(String path)` → `InputStream`
- `openFd(String path)` → `AssetFileDescriptor` (extract to temp file)
- `list(String path)` → `String[]`
- `openNonAssetFd(int cookie, String path)` → `AssetFileDescriptor`

#### P3-T4: Expand framework stub coverage

Add real implementations (not stubs) for classes needed by target apps:
- `android/widget/TextView.java` — minimum: draw text, handle padding
- `android/widget/Button.java` — extends TextView, click handling
- `android/widget/ImageView.java` — draw Bitmap from resource
- `android/widget/EditText.java` — cursor, text input from keyboard
- `android/widget/LinearLayout.java` — measure/layout children vertically/horizontally
- `android/widget/FrameLayout.java` — stack children
- `android/widget/RelativeLayout.java` — rule-based layout
- `android/widget/ScrollView.java` — vertical scrolling
- `android/view/ViewGroup.java` — complete measure/layout dispatch
- `android/view/View.java` — complete requestLayout, invalidate, dispatchDraw

Coverage target: support the Phase 3 test apps (Simple Calculator, KeePassDX, NewPipe).

#### P3-T5: WebView via WPE WebKit

Write `libnova_android/nova_webview.c`:
- Initialize WPE WebKit renderer
- Create Wayland subsurface in the app's surface hierarchy
- Route URL loading from WebView Java API → WPE
- Route touch/click events from Wayland → WPE
- Route WPE render frames → dmabuf → app surface

Write `nova-framework/android/webkit/WebView.java`:
- `loadUrl(String url)` — call JNI `nova_webview_load_url`
- `setWebViewClient(WebViewClient)` — store callback
- `setWebChromeClient(WebChromeClient)` — store callback
- `evaluateJavascript(String, ValueCallback)` — call JNI `nova_webview_eval_js`
- `onPageFinished`, `onReceivedError`, `shouldOverrideUrlLoading`
- `getSettings()` → `WebSettings` with `setJavaScriptEnabled`, `setUserAgentString`

**Why Phase 3:** WebView is required for OAuth login in almost all modern apps.
Without it, NewPipe, KeePassDX, and any app with authentication will crash at
login screen. Cannot be deferred.

#### P3-T6: Intent resolution (basic)

Write `nova-framework/android/content/Intent.java`:
- `setAction(String)`, `setData(Uri)`, `setClass(Context, Class)`, `setClassName(...)`
- `getStringExtra(String)`, `getIntExtra(String, int)`, `getBooleanExtra(...)`, `hasExtra(String)`
- `putExtra(String, String)`, `putExtra(String, int)`, etc.

Write single-activity dispatch:
- `startActivity(Intent)` → if intent targets a known activity, launch it
- Single-task: only one activity window at a time (no back stack yet)

#### P3-T7: In-process Binder service stubs

Write Java service stubs that implement the AIDL interfaces:
- `IWindowManager`: `openSession()` → local `IWindowSession`
- `IWindowSession`: `addToDisplay()` → Wayland surface, `relayout()` → SurfaceControl, `remove()` → destroy
- `IDisplayManager`: `getDisplayInfo()` → 1920×1080@60, `registerCallback()` → no-op
- `IPackageManager`: `getApplicationInfo()`, `getActivityInfo()`, `checkPermission()` → GRANTED
  `resolveIntent()` → activity, `getInstalledPackages()` → known apps

Register at startup: `ServiceManager.addService("window", new NovaWindowManagerService())`

These are in-process stubs using Java Binder (no kernel module). AOSP's Binder
base class supports in-process `transact()` directly.

### Gate Test — Phase 3

```bash
bash vendor/nova/scripts/smoke-phase3.sh
# PASS:
# ✓ Simple Calculator: buttons render with text, digit input, result computed
# ✓ KeePassDX: database list screen renders with text and icons
# ✓ NewPipe: home screen renders (offline mode accepted, no crash on WebView)
# ✓ WebView: loads URL and renders content in subsurface
```

---

## Phase 4 — Multi-Process Daemon + libbinder_rpc (Weeks 16–24)

**Goal:** Transition from single-process to daemon architecture. This is required
for any app that needs real system services (multiple APKs, background services,
Intents between apps).

### Rationale

The Phase 0–3 single-process approach works for individual APKs but doesn't scale:
- No background services (SoundPool stops when app exits)
- No Intent routing between apps
- No multi-window support
- Stub service surface grows unbounded

The daemon architecture uses AOSP's `libbinder_rpc` which supports Binder IPC
over Unix domain sockets since Android 14. No kernel modules needed.

### Tasks

#### P4-T1: Standalone libbinder_rpc integration test

Before building the full daemon, validate that libbinder_rpc works on host:

Write `tests/nova_binder_test.cpp`:
- Create `RpcServer::make()` bound to Unix domain socket
- Call `setupUnixDomainServer("/tmp/nova-test.sock")`
- Start a thread that calls `server->join()`
- Create `RpcSession::make()` 
- Call `setupUnixDomainClient("/tmp/nova-test.sock")`
- Call `session->getRootObject()` — verify non-null
- Create a simple test service (`ITestService`) that echoes a string
- Verify round-trip IPC works

Build with Soong `cc_test_host { ... }`.

#### P4-T2: Daemon process

Write `vendor/nova/nova-daemon/src/main.c`:
- Parse command line: `nova-daemon [--daemonize] [--socket-path PATH]`
- Create `RpcServer` on Unix domain socket at `$XDG_RUNTIME_DIR/nova-daemon.sock`
- Register root service object (`INovaService`)
- Register stub system services:
  - `IActivityManagerService` — manages app lifecycle, activity stack
  - `IPackageManagerService` — resolves packages, manages installed APKs
  - `IWindowManagerService` — manages Wayland surfaces across processes
  - `IServiceManager` — the service registry itself
- Wayland display connection moved to daemon (single wl_display for all apps)
- Accept client connections, create per-session threads

#### P4-T3: Client process

Write `vendor/nova/src/nova_client.c` (new binary `nova` becomes a client launcher):
- Parse `nova --package com.example.app` or `nova /path/to/app.apk`
- Connect to daemon socket via `RpcSession::setupUnixDomainClient()`
- Call `IActivityManagerService.startActivity(package, activity)` through Binder RPC
- Receive Wayland surface FD via Unix socket ancillary data (`SCM_RIGHTS`)
- Set up ART + framework in child process
- Route Wayland events from daemon to client

#### P4-T4: Split bootclasspath

Daemon gets: `ServiceManager`, system service stubs, Binder infrastructure.
Client gets: app framework (Activity, View, Canvas, etc.), shorter bootclasspath.

#### P4-T5: File descriptor passing

Wayland `wl_surface` FDs must be passed from daemon to client processes.
This is done via Unix socket ancillary data (`cmsg(3)` with `SCM_RIGHTS`).
`libbinder_rpc` supports `FileDescriptorTransportMode::UNIX` for this.

### Gate Test — Phase 4

```bash
bash vendor/nova/scripts/smoke-phase4.sh
# PASS:
# ✓ libbinder_rpc integration test passes
# ✓ nova-daemon starts and listens on Unix socket
# ✓ nova --package com.simplemobiletools.calculator launches app in child process
# ✓ App renders via Wayland surface received from daemon
# ✓ App can query PackageManager from daemon (returns installed package info)
```

---

## Phase 5 — ARM64 Native Libraries (Weeks 24–32)

**Goal:** APKs with `lib/arm64-v8a/` native libraries execute on x86_64 host.

### Approach

Use Google's `libndk_translation.so` (from ChromeOS guybrush firmware R134+) or
Intel's `libhoudini.so` (from WSA). Both are ARM64→x86_64 binary translators
proven in Android-x86, ChromeOS ARCVM, and Redroid.

### Tasks

#### P5-T1: Acquire translation library

Extract `libndk_translation.so` from ChromeOS guybrush R134+ recovery image:
- Download from `cros.tech` → `guybrush` → R134+
- Extract `vendor.raw.img` → `usr/lib64/libndk_translation.so`
- Extract corresponding system image for ARM sysroot

Alternative: Extract `libhoudini.so` from WSA MSIX bundle:
- Download `9P3395VX91MR` from Microsoft Store
- Extract `libhoudini.so`, `libhoudini64.so`

#### P5-T2: Register native bridge

Set properties in system properties:
```
ro.dalvik.vm.native.bridge=libndk_translation.so
ro.enable.native.bridge.exec=1
ro.enable.native.bridge.exec64=1
ro.product.cpu.abilist=x86_64,x86,arm64-v8a,armeabi-v7a,armeabi
ro.product.cpu.abilist64=x86_64,arm64-v8a
```

Register `binfmt_misc` entries for ARM64 ELF:
```
echo ':arm64_dyn:M::\x7fELF\x02\x01\x01\x00\x00\x00\x00\x00\x00\x00\x00\x00\x02\x00\xb7\x00:\xff\xff\xff\xff\xff\xff\xff\x00\xff\xff\xff\xff\xff\xff\xff\xff\xff\xfe\xff\xff\xff:/usr/libexec/houdini64:P' > /proc/sys/fs/binfmt_misc/register
```

#### P5-T3: Linker config for ARM libraries

Write `ld.config.arm.txt`:
```
[nova_arm64_app]
namespace.default.search.paths = /system/lib64/arm64
namespace.default.links = nova_art

namespace.nova_art.search.paths = /apex/com.android.art/lib64
```

#### P5-T4: Validate with test APKs

Test with glmark2 ARM64 and a simple ARM64 NDK app:
- `libglmark2-android.so` (ARM64 build) must load and execute
- Benchmark must complete without `LIBC` version errors

### Gate Test — Phase 5

```bash
bash vendor/nova/scripts/smoke-phase5.sh
# PASS:
# ✓ libndk_translation.so loads successfully
# ✓ glmark2-arm64.apk runs, FPS score reported
# ✓ No LIBC or symbol version errors
# ✓ CPU ABI list includes arm64-v8a
```

---

## Phase 6 — Vulkan via gfxstream (Weeks 32–40)

**Goal:** Vulkan rendering via host GPU using gfxstream single-process IPC.

gfxstream supports direct-function-call mode (no virtio-gpu) where guest encoder
and host decoder are in the same process. This is documented and verified.

### Tasks

#### P6-T1: Build gfxstream backend

```bash
cd hardware/google/gfxstream
m libgfxstream_backend
# Result: out/host/linux-x86/lib64/libgfxstream_backend.so
```

#### P6-T2: Wire as libvulkan.so replacement

Write `libnova_vulkan/nova_vulkan.c`:
- Implement `vkCreateInstance` → gfxstream guest encoder initialization
- Implement `vkEnumeratePhysicalDevices` → return host GPU (NVIDIA 3090 / AMD)
- Implement `vkCreateDevice` → gfxstream host decoder GPU context
- Implement `vkGetDeviceQueue`, `vkAllocateMemory`, `vkCreateBuffer`, etc.
- Route all Vulkan commands through gfxstream encoder → host decoder chain

The linker namespace redirects `libvulkan.so` → `libnova_vulkan.so`.

#### P6-T3: Implement Vulkan WSI for Wayland

Write `libnova_vulkan/nova_vulkan_wsi.c`:
- Implement `VK_KHR_wayland_surface` extension
- `vkCreateWaylandSurfaceKHR` — create `wl_surface` backed by dmabuf
- Implement `VK_EXT_swapchain_dma_buf` for zero-copy
- Use `zwp_linux_dmabuf_v1` Wayland protocol for buffer sharing

#### P6-T4: Integrate dmabuf-backed AHardwareBuffer

For apps using `AHardwareBuffer` (Android 11+ native API):
- Allocate dmabuf FDs via GBM library
- Wrap in `AHardwareBuffer_Desc` struct
- Return to app for GPU rendering

### Gate Test — Phase 6

```bash
bash vendor/nova/scripts/smoke-phase6.sh
# PASS:
# ✓ vkCreateInstance succeeds via gfxstream
# ✓ vkEnumeratePhysicalDevices returns host GPU (NVIDIA 3090 / AMD iGPU)
# ✓ vkGetPhysicalDeviceProperties returns Vulkan 1.3
# ✓ Vulkan triangle renders without Mesa CPU fallback
```

---

## Phase 7 — Ecosystem + Polish (Ongoing)

**Goal:** Production-quality experience for end users.

### Tasks

#### P7-T1: F-Droid as launcher app
- Install F-Droid APK via nova
- Browse catalog (exercises RecyclerView, Fragments, network)
- Download APK (exercises DownloadManager)
- Install APK (exercises PackageInstaller)

#### P7-T2: ADB debugging
- Support `adb connect` via TCP loopback
- Allow `adb install`, `adb logcat`, `adb shell` for debugging

#### P7-T3: MediaCodec → VA-API
- `android.media.MediaCodec` JNI → VA-API for hardware decode
- `android.media.MediaExtractor` → FFmpeg demuxer
- H.264, H.265 (HEVC), VP9 support

#### P7-T4: Multi-window
- Multiple `nova --package` processes each get their own xdg_toplevel
- Window management via daemon's IWindowManager

#### P7-T5: App installation flow
- `nova install /path/to/app.apk` — copies APK, runs dex2oat, registers in PackageManager
- `nova list` — shows installed packages
- `nova uninstall <package>` — removes APK and app data
- Persistent storage: `$XDG_DATA_HOME/nova/` with per-app data directories

---

## Summary

| Phase | Duration | Gate Test App | Key Deliverable |
|---|---|---|---|
| 0 — Foundation | 1–3 weeks | (empty window) | AOSP builds, ART boots, Wayland window |
| 1 — GLES Port | 3–5 weeks | gles3jni, 2048 | Working Nova binary (port from prototype) |
| 2 — Audio+Animation | 5–9 weeks | Material Life, Pixel Dungeon | ValueAnimator, PipeWire audio, TextureView |
| 3 — Text+Resources | 9–16 weeks | Calculator, KeePassDX, NewPipe | Text rendering, layout inflation, WebView |
| 4 — Multi-Process | 16–24 weeks | Calculator via daemon | libbinder_rpc daemon, system services |
| 5 — ARM64 Native | 24–32 weeks | glmark2 ARM64 | libndk_translation, ARM64 ELF execution |
| 6 — Vulkan | 32–40 weeks | Vulkan triangle | gfxstream single-process Vulkan |
| 7 — Ecosystem | ongoing | F-Droid, VLC | ADB, app install, MediaCodec, multi-window |

### Code Reuse Summary

| Source | Lines | Port To |
|---|---|---|
| `deps/NovaART/src/art.c` | 716 | `nova/src/art.c` |
| `deps/NovaART/src/wayland.c` | 421 | `libnova_android/nova_wayland.c` |
| `deps/NovaART/src/egl.c` | 96 | `libnova_egl/nova_egl.c` |
| `deps/NovaART/src/canvas_render.c` | 172 | `libnova_android/nova_canvas_render.c` |
| `deps/NovaART/src/softgfx.c` | 317 | `libnova_android/nova_softgfx.c` |
| `deps/NovaART/src/jni/*.c` (18 files) | ~1,500 | `libnova_*/` JNI modules |
| `deps/NovaART/src/java/nova-shims/` (125 files) | ~3,000 | `nova-framework/src/` |
| `deps/NovaART/src/java/aosp/` (24 files) | ~3,000 | `nova-framework/src/` |
| **Total reusable** | **~6,500** | |
| **New code needed** | **~9,000** | |
| **Grand total** | **~15,500** | nova runtime |
