# Nova — v7 Implementation Plan

## Overview

Nova is a Linux-native Android application runtime. It runs unmodified Android APKs
directly on a Linux desktop — with Wayland rendering, PipeWire audio, and host GPU
acceleration — without containers, virtual machines, or Android system images.

Nova is built as a first-class AOSP product. It lives in `vendor/nova/`, uses AOSP's
Soong build system, and provides a small set of wrapper libraries plus a Java
framework delta over stock AOSP ART + framework.

---

## Repository Structure

### AOSP Layout

Nova is a single git repository at `vendor/nova/`. AOSP is never forked.

```
vendor/nova/
├── Android.bp                    # Root module definitions
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
├── libnova_native_bridge/
│   ├── Android.bp
│   └── nova_native_bridge.c      # ARM64→x86_64 translation loader hooks
├── nova-framework/
│   ├── Android.bp
│   ├── nova-framework.jar         # Built via java_library, bootclasspath
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
│           ├── animation/AnimatorSet.java
│           ├── os/Handler.java
│           ├── os/Looper.java
│           ├── os/MessageQueue.java
│           ├── media/SoundPool.java
│           ├── media/MediaPlayer.java
│           └── ... (~70–100 more as needed)
└── tests/
    ├── nova_unit_tests/
    └── integration_tests/
```

### Wrapper libraries (4 overrides + 1 native bridge plugin)

Each Nova library overrides the corresponding AOSP module via Soong's `overrides:` stanza:

```python
cc_library_shared {
    name: "libnova_android",
    overrides: ["libandroid"],
    host_supported: true,
}
```

**Library set (5 libraries, 4 are overrides):**

| Nova library | Overrides | Purpose |
|---|---|---|
| `libnova_android` | `libandroid` | ANativeWindow → Wayland, AAssetManager → APK zip |
| `libnova_egl` | `libEGL` | Host EGL + wayland-egl |
| `libnova_audio` | `libOpenSLES` | OpenSL ES and AAudio → PipeWire |
| `libnova_vulkan` | `libvulkan` | gfxstream guest encoder (single-process) |
| `libnova_native_bridge` | *(plugin, not an override)* | Loaded by ART's native bridge for ARM64→x86_64 translation |

`libGLESv2.so` and `libGLESv3.so` are **not** overridden — host Mesa provides
these directly. Apps linking `libGLESv2.so` get host Mesa GLES. Apps linking
`libandroid.so` get the Nova Wayland backend. This split is correct because
Wayland/EGL is the integration surface, not GLES itself.

### Linker namespace

`ld.config.nova.txt` redirects platform libraries via additional namespace:

```
[nova_app]
additional.namespaces = nova_platform

namespace.default.links = nova_platform
namespace.default.link.nova_platform.shared_libs = libvulkan.so:libandroid.so:libOpenSLES.so:libEGL.so

namespace.nova_platform.isolated = true
namespace.nova_platform.search.paths = /out/host/linux-x86/lib64/nova
```

**Note:** The linker namespace config is only fully effective when running
within AOSP's Android linker/runtime. On a glibc-host runtime (Phases 0–3),
the same library redirection is achieved via `LD_LIBRARY_PATH` and
`LD_PRELOAD`, following the prototype's existing approach. The `ld.config.*`
file becomes authoritative in Phase 4+ multi-process mode when each app
process uses the Android linker. This is an unproven spike that must be
validated in Phase 4.

### Window ownership model (architectural decision)

**Each app process owns its own Wayland connection.** This is a deliberate choice
that avoids the invalid "wl_surface over SCM_RIGHTS" pattern from v5.

- The daemon (Phase 4+) brokers Android system services (PackageManager,
  ActivityManager, WindowManager state) via `libbinder_rpc` over Unix domain sockets
- Each app process opens a separate `wl_display` connection, creates its own
  `wl_surface` and `xdg_toplevel`
- The daemon has **no Wayland display connection** of its own — it is a pure
  service bus

This means:
- App-side EGL, GLES, Vulkan, TextureView, WebView all work naturally (same
  process owns the surface and the rendering context)
- No cross-process FD passing of Wayland objects needed
- Each app is a separate toplevel window controlled by the Wayland compositor
- The daemon's `IWindowManagerService` tracks **logical window state** (activity
  stack, task order, focus) but does **not** manage physical window geometry
  (Wayland does not expose global position to clients; the compositor decides
  placement)
- Each app process has a `ClientCompositorAdapter` (local component) that owns
  the Wayland connection, handles `xdg_toplevel.configure` (resize), manages
  `wl_surface` creation, and provides `IWindowSession` semantics locally

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

## Phase 0 — Foundation (~1 month)

**Goal:** AOSP build system produces a `nova` binary that boots ART and opens an
empty Wayland window.

### Tasks

#### P0-T1: Create vendor/nova/ skeleton

Create the `vendor/nova/` directory tree with:
- `vendorsetup.sh` — registers `nova-eng` and `nova-userdebug`
- `products/nova.mk` — defines `PRODUCT_PACKAGES`
- `Android.bp` root — empty top-level blueprint

Verify: `lunch nova-eng` succeeds.

#### P0-T2: Port `nova` binary to Soong

Write `vendor/nova/Android.bp` — a `cc_binary_host { name: "nova" }` that:
- Parses APK path from argv
- Connects to Wayland display
- Calls `JNI_CreateJavaVM` from host `libart.so`
- Enters Wayland dispatch loop

Sources ported from prototype (`deps/NovaART/src/`):
- `src/art.c` (716 LOC) → `nova/src/art.c` — ART bootstrap, APK manifest parsing
  via `aapt2 dump badging`
- `src/wayland.c` (421 LOC) → `libnova_android/nova_wayland.c` — Wayland globals,
  xdg-toplevel, registry listener, pointer/keyboard input, wl_surface creation
- `src/egl.c` (96 LOC) → `libnova_egl/nova_egl.c` — wl_egl_window, EGL
  display/config/surface
- `src/main.c` (115 LOC) → `nova/src/main.c` — entry point, event loop

Add host `libwayland-client`, `libwayland-egl`, `libEGL`, `libGLESv2` as shared
lib dependencies.

Verify: `m -j$(nproc) nova` builds successfully.

#### P0-T3: Wire ART bootstrap

In `nova/src/art.c`:
- `dlopen("libart.so")` at runtime from staged AOSP build output
- Set `ANDROID_ROOT`, `ANDROID_ART_ROOT`, `ANDROID_I18N_ROOT`,
  `ANDROID_TZDATA_ROOT`, `ANDROID_DATA` environment variables
- Build bootclasspath from staged APEX jars:
  `core-oj.jar`, `core-libart.jar`, `okhttp.jar`, `bouncycastle.jar`,
  `apache-xml.jar`, `core-icu4j.jar`, `conscrypt.jar`
- Call `JNI_CreateJavaVM`
- Return valid `JavaVM*` and `JNIEnv*`

Verify: ART initialises without crash, JNIEnv is valid.

#### P0-T4: Create Wayland window

In `libnova_android/nova_wayland.c`:
- Connect to Wayland display (per-process connection)
- Bind `wl_compositor`, `xdg_wm_base`, `wl_shm`, `wl_seat`
- Create `xdg_toplevel` surface
- Set window title from APK `android:label`
- Request server-side decorations via `xdg-decoration-unstable-v1`
- Handle `configure`, `close`, resize
- Dispatch pointer + keyboard events

Verify: Empty Wayland window appears with correct APK title.

#### P0-T5: APK manifest parsing

The prototype uses `aapt2 dump badging` subprocess to extract package name and
launchable activity. Port this approach:
- Invoke `aapt2` from staged AOSP build output
- Parse `package: name='...'` and `launchable-activity: name='...'`
- Extract `android:label` from aapt2 output
- Fallback: binary AXML parser if aapt2 is unavailable

#### P0-T6: Linker namespace config

Write `vendor/nova/linker/ld.config.nova.txt` with the namespace layout above.
Pass `-Xlinker-config` path via ART runtime argument in `JNI_CreateJavaVM` call.

Verify: `dlopen("libvulkan.so")` in a test JNI method loads `libnova_vulkan.so`.

#### Gate Test — Phase 0

```bash
# Manual verification steps (scripts to be written in each phase):
# 1. m -j$(nproc) nova succeeds
# 2. ./out/host/linux-x86/bin/nova test.apk produces a window
# 3. ART initialises without abort
# 4. Window close button exits cleanly
# 5. dlopen("libvulkan.so") loads libnova_vulkan.so
```

---

## Phase 1 — GLES Rendering Port (~1 month)

**Goal:** Port the prototype's working GLES stack to Soong.

The prototype has working GLES rendering (gles3jni at 60 FPS). Phase 1 is a
porting effort from Meson to Soong.

### Tasks

#### P1-T1: Port JNI bridge C files to Soong

Move from `deps/NovaART/src/jni/` to `vendor/nova/libnova_*/`:

| Prototype C file | Destination | Role |
|---|---|---|
| `com_google_android_gles_jni_EGLImpl.c` | `libnova_egl/egl_jni.c` | EGL10 native methods |
| `com_google_android_gles_jni_GLImpl.c` | `libnova_egl/gl_jni.c` | GL10 native methods |
| `android_opengl_GLES20.c` | `libnova_android/gles20_jni.c` | GLES20 native methods |
| `android_opengl_GLUtils.c` | `libnova_android/glutils_jni.c` | GL util native methods |
| `android_runtime.c` | `libnova_android/jni_registration.c` | gRegJNI table (16 modules) |

Add `Android.bp` entries with `shared_libs: ["libart", "libnativehelper"]`.

#### P1-T2: Port the Java framework overlay

Move Java sources from `deps/NovaART/src/java/nova-shims/` and
`deps/NovaART/src/java/aosp/` into `vendor/nova/nova-framework/src/`.

Current inventory:
- **153 Java shim files** in `src/java/nova-shims/` (under 3 package trees:
  `android/`, `com/`, `nova/`)
- **24 AOSP Java files** in `src/java/aosp/`
- Total: **177 Java files**

Build as a `java_library` in Soong with `installable: true` and add it to the
bootclasspath (via `PRODUCT_BOOT_JARS`), **not** as `android_app` or
`dex_import`, because these classes must be visible to all loaded APKs at
boot time.

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

**Canvas capability notes (current prototype state):**
- Working: `drawRect`, `drawColor`, `save`, `restore`, `getWidth`, `getHeight`
- No-op stubs: `drawText`, `drawBitmap`, `drawCircle`, `drawLine`
- Paint: color, stroke width, style settings work; `setXfermode` is no-op
- Porter-Duff compositing: not implemented, pure replace mode

"Canvas renders at 60 FPS" in the gate test is accurate for apps using only
rectangles and color fills (like 2048's tile grid). Text-based apps require
Phase 3 work.

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
| `core_jni_helpers.c` | `libnova_android/jni_helpers.c` |

**Note:** 18 C files in `src/jni/` but only 16 JNI modules registered in
`gRegJNI[]`. The remaining 2 are: `android_runtime.c` (the registration table
itself) and `core_jni_helpers.c` (utility functions used by all JNI modules).

#### Gate Test — Phase 1

```bash
# Using existing prototype smoke test:
bash vendor/nova/scripts/smoke-run-gles3jni.sh   # port to vendor/nova/
# PASS:
# - gles3jni.apk: window appears, triangle renders and rotates
# - GL thread stable at 60 FPS for 10 seconds
# - No EGL errors on host Mesa
```

---

## Phase 1.5 — x86_64 Native Library ABI (~1 month)

**Goal:** Establish exactly which Android x86_64 native `.so` files can be
loaded on the host, build a targeted compatibility shim for the symbols target
apps actually need, and document the supported ABI surface.

### Rationale

This phase replaces v5's unrealistic "build Bionic for host + LD_PRELOAD"
approach, which is not feasible:
- `deps/aosp-full` does not contain `bionic/` sources — `m libc_bionic` won't
  compile from current dependencies
- Mixing two libc implementations in one process via `LD_PRELOAD` is not
  architecturally credible (symbol resolution ambiguity, TLS conflicts,
  signal handling differences)
- The `__aeabi_*` symbols in v5's compatibility table are ARM-specific and
  irrelevant to x86_64 Android `.so` loading

### Approach

The prototype currently works because `libgles3jni.so` is host-native
(x86_64 compiled for glibc). Real off-the-shelf APKs ship `.so` files compiled
for Bionic. The gap is real, and the honest answer is a **bounded scope cut**
with a targeted shim:

1. **Inventory exactly which NDK APIs target apps use** — extract `.so` files
   from each target APK, run `readelf -d` / `nm -D` to list needed symbols
2. **Build a minimal symbol shim** (`libnova_native_shim.so`) providing only
   the Bionic-specific symbols those apps need — not full Bionic compatibility,
   just the specific set of symbols the target apps dereference
3. **Load via Android's linker** — build `linker64` from AOSP for host, use it
   as the `dlopen` loader for Android `.so` files (the Android linker knows
   how to resolve Bionic symbols against the NDK's `libc.so`)

### Known limitation

General "any Android x86_64 `.so` loads unmodified" is **not a goal** of
Phase 1.5. The goal is: **these specific NDK APIs work for these specific apps.**
Full Bionic ABI compatibility (libc, libm, libdl, libc++) is a separate,
significantly harder problem that can be revisited in a later phase if the
target app set demands it.

### Tasks

#### P1.5-T1: Audit target app native library usage

For each target APK (Material Life, Pixel Dungeon, KeePassDX, NewPipe,
Simple Calculator, 2048):
- Extract the APK with `unzip -d /tmp/app`
- Identify native libs via `find lib/x86_64/ -name '*.so'`
- Run `readelf -d <lib>.so` for `NEEDED` entries
- Run `nm -D <lib>.so` for imported symbols
- Document the union of required Bionic/NDK symbols across all target apps

Expected result: a concrete list like:
```
libc.so:  open, close, read, write, malloc, free, mmap, munmap, pthread_create, ...
libm.so:  sin, cos, sqrt, floor, ceil, fabs, ...
libc++_shared.so:  _Znwm, _ZdlPv, _ZNKSt3__16vector..., ... (C++ ABI symbols)
liblog.so: __android_log_print, __android_log_vprint, ...
```

#### P1.5-T2: Build linker64 + NDK runtime for host

AOSP can build `linker64` for the host target. The Android linker knows how
to resolve `DT_NEEDED` entries against Bionic-style shared libraries. The
result is a host-compatible `linker64` binary that can serve as the program
interpreter for Android `.so` files.

```bash
m linker64-linker
```

Also stage the NDK shared libraries that target apps need:
```
libc.so, libm.so, libdl.so, liblog.so, libc++_shared.so
```
These are available from the Android NDK (not from `deps/aosp-full`).
Download the NDK for API 34+ and extract the x86_64 `.so` files.

#### P1.5-T3: Build minimal symbol shim

Write `libnova_native_bridge/nova_native_shim.c`:
- Provide stubs for any Bionic-specific symbols from the audit that are
  not present in glibc or the NDK runtime
- Interpose via `dlsym(RTLD_NEXT, ...)` for symbols that need different
  behavior on host (e.g., `__android_log_print` → `fprintf(stderr, ...)`)
- Size target: < 500 LOC

#### P1.5-T4: Wire into System.loadLibrary()

Modify `nova-framework/java/lang/Runtime.java` or the JNI `native_load()`
to route `System.loadLibrary("native")` through:
1. Check target ABI: if `x86_64`, try glibc-host `dlopen` first
2. If fails (symbol error), retry using Android linker with the symbol shim
3. If both fail, log unsupported native library and continue (Java-only)

#### P1.5-T5: Validate with audited APKs

Test each target APK. Document per-APK results:
- Which native libs loaded successfully
- Which symbols needed shimming
- Which APKs are Java-only and don't need native loading at all

#### Gate Test — Phase 1.5

```bash
# For each target APK with native libs:
./out/host/linux-x86/bin/nova target-app.apk
# PASS: App does not crash on native lib load
# PASS: For Java-only APKs: no regression
# Documented: per-app native loading status matrix
```

---

## Phase 2 — Animation + Audio + TextureView (~2 months)

**Goal:** Unblock animated apps (Material Life) and add audio.

### Tasks

#### P2-T1: Upgrade ValueAnimator to Choreographer-driven vsync

The prototype already has a functional `ValueAnimator.java` (178 LOC) with:
- `ofFloat()`, `ofInt()`, `ofObject()` factory methods
- `addUpdateListener()` / `removeUpdateListener()`
- Sleep-based animation loop at ~60 FPS
- `start()` / `cancel()` / `isRunning()`
- `getAnimatedValue()`, `getAnimatedFraction()`
- Duration, repeat count/mode, start delay

**What's missing:**
- Choreographer-driven frame callbacks (currently uses `Thread.sleep(16)` which
  desyncs from actual vsync) — `Choreographer.java` already exists in the repo
  with `FrameCallback` and `postFrameCallback()`, but it is not wired to
  Wayland frame events
- `AnimatorListenerAdapter` — Android apps expect `onAnimationEnd`,
  `onAnimationStart`
- `ObjectAnimator` — extends ValueAnimator with property reflection
- `Choreographer.postFrameCallback()` vsync via `wl_surface.frame`

Tasks:
- Wire `Choreographer.java`'s `postFrameCallback()` to Wayland frame events
  (`wl_surface.frame` callback → `FrameCallback.doFrame()`)
- Rewrite `ValueAnimator.start()` to use `Choreographer.postFrameCallback()`
  instead of thread sleep
- Write `ObjectAnimator.java` extending ValueAnimator with property name/setter
  reflection
- Write `AnimatorListenerAdapter.java`

#### P2-T2: Fix TextureView render path

Material Life's render loop is detected but not drawing frames. Cause: the app
drives rendering via ValueAnimator, not Surface lifecycle callbacks.

- Ensure `Surface(SurfaceTexture)` constructor works when render thread starts
- Wire `lockCanvas()` / `unlockCanvasAndPost()` through `CanvasRender.submitFrame()`
- Ensure `onSurfaceTextureAvailable` callback fires and triggers render setup

#### P2-T3: Wire PipeWire audio

Write `libnova_audio/nova_audio.c`:
- `nova_audio_init()` — init PipeWire context + main loop
- `nova_sound_load(path)` — decode PCM via libsndfile, return sound ID
- `nova_sound_play(id, left_vol, right_vol, loop, rate)` — create `pw_stream`,
  write PCM data
- `nova_sound_stop(id)`, `nova_sound_unload(id)`
- `nova_media_player_start(path)` — loop stream for background music

Wire OpenSL ES shim (`libnova_audio/nova_opensles.c`):
- `slCreateEngine` → init PipeWire context
- `SLresult (*Realize)(...)` → return success
- `SLEngineItf (*CreateAudioPlayer)(...)` → route to sound pool

Wire AAudio shim (`libnova_audio/nova_aaudio.c`):
- `AAudio_createStreamBuilder` → return builder with PipeWire params
- `AAudioStreamBuilder_openStream` → create `pw_stream`
- `AAudioStream_requestStart` → start PipeWire stream
- `AAudioStream_write` → write PCM to PipeWire buffer

Add `libaaudio.so` to the override set if apps directly link it.

#### P2-T4: Complete SoundPool + MediaPlayer Java stubs

- `nova-framework/android/media/SoundPool.java`
  - `load(Context, int, int)` → JNI `nova_sound_load`
  - `play(int, float, float, int, int, float)` → JNI `nova_sound_play`
  - `stop(int)`, `pause(int)`, `resume(int)`, `release()`, `autoPause()`,
    `autoResume()`
  - `setVolume(int, float, float)`, `setRate(int, float)`
- `nova-framework/android/media/MediaPlayer.java`
  - `setDataSource(String)` / `setDataSource(Context, Uri)`
  - `prepare()` / `start()` / `stop()` / `pause()` / `seekTo(int)`
  - `setLooping(boolean)`, `isPlaying()`, `getDuration()`, `getCurrentPosition()`
  - `setOnCompletionListener`, `setOnPreparedListener`, `setOnErrorListener`

#### P2-T5: Fix repeated-run native-lib staging

Prototype fails on repeated runs with `FileAlreadyExistsException`. Fix: remove
existing native libs before re-extracting.

#### Gate Test — Phase 2

```
# Material Life: cellular automaton animation runs (frames updating)
# Pixel Dungeon: dungeon view renders, hero visible, touch → hero moves
# Pixel Dungeon: sound plays on item pickup
# 2048: tile grid renders, swipe gesture moves tiles
# Window close exits cleanly
```

---

## Phase 3 — Text + Resources + Layout Inflation (~2 months)

**Goal:** Apps with real UI layouts, text rendering, and resource files work.

### Tasks

#### P3-T1: FreeType + HarfBuzz text rendering

Write `libnova_android/nova_text.c`:
- `nova_text_init()` — init FreeType library
- `nova_typeface_load(path)` — load font from APK assets or system fonts
- `nova_text_measure(text, typeface, size)` — measure text width via HarfBuzz
- `nova_text_draw(canvas, text, x, y, paint)` — shape text, render glyphs to
  softgfx canvas (glyph mask → pixel blend)
- `nova-framework/android/graphics/Typeface.java` — `create(String, int)`,
  `DEFAULT`, `MONOSPACE`, `SANS_SERIF`, `SERIF`
- `nova-framework/android/graphics/Paint.java` — `measureText(String)`,
  `getTextBounds(...)`, `setTextSize(float)`, `setTypeface(Typeface)`
- Wire `Canvas.drawText()` JNI stub (currently no-op) to nova_text_draw

#### P3-T2: Implement drawBitmap, drawCircle, drawLine

Implement the 3 remaining Canvas no-op stubs in softgfx:
- `drawBitmap` — blit source bitmap region to canvas at (left, top) with paint
  color filter
- `drawCircle` — Bresenham/midpoint circle algorithm with fill+stroke
- `drawLine` — Bresenham line draw with paint color and stroke width

#### P3-T3: XML drawable support

Android apps use XML drawables extensively (shapes, selectors, layer-lists).
This is a significant gap not addressed in the original plan.

Write `nova-framework/android/graphics/drawable/`:
- `GradientDrawable.java` — parse `shape` XML: corners, gradient, stroke, solid
- `StateListDrawable.java` — selector with state matching (pressed, focused,
  enabled)
- `LayerDrawable.java` — composite from child drawable list
- `RippleDrawable.java` — basic ripple (circular reveal on touch)

Parse XML drawables via `resources.arsc` → XmlPullParser → drawable inflation.

#### P3-T4: Layout inflation with AXML parsing

Write `nova-framework/android/view/LayoutInflater.java`:
- `inflate(int resource, ViewGroup root)` — parse compiled AXML
- `inflate(XmlPullParser, ViewGroup root, boolean attachToRoot)` — recursive
- Instantiate: `TextView`, `Button`, `ImageView`, `LinearLayout`, `FrameLayout`,
  `RelativeLayout`, `EditText`, `CheckBox`, `RadioButton`, `Spinner`, `ScrollView`
- Apply attributes: `layout_width`, `layout_height`, `gravity`, `padding`,
  `margin`, `background`, `textSize`, `textColor`, `ellipsize`, `maxLines`,
  `inputType`, `src`, `scaleType`

Wire into `Activity.setContentView(int)`.

Write resource parser (`nova-framework/android/content/res/ResourceManager.java`):
- Parse `resources.arsc` from APK (binary resource table)
- Resolve `@string/name`, `@color/name`, `@dimen/name`, `@layout/name`,
  `@drawable/name`, `@mipmap/name`
- Support configuration qualifiers: `-land`, `-v21`, `-xhdpi`, `-night`

#### P3-T5: Complete AssetManager

An `AssetManager.java` already exists in the prototype (partial). Complete it:
- Open assets from APK zip via `ZipFile` API
- `open(String path)` → `InputStream`
- `openFd(String path)` → extract to temp, return `AssetFileDescriptor`
- `list(String path)` → `String[]`
- `openNonAssetFd(int cookie, String path)` → `AssetFileDescriptor`
- Wire into `Context.getAssets()` and `Resources.getAssets()`

#### P3-T6: Expand framework stub coverage

Add real implementations (not stubs) for widget classes:
- `android/widget/TextView.java` — text layout with word wrapping, padding,
  gravity, ellipsize, linkify
- `android/widget/Button.java` — clickable TextView with background state drawable
- `android/widget/ImageView.java` — Bitmap draw from resource, scaleType
- `android/widget/EditText.java` — cursor, xdg_input_method integration
- `android/widget/LinearLayout.java` — measure/layout vertically/horizontally
- `android/widget/FrameLayout.java` — stack children with gravity
- `android/widget/RelativeLayout.java` — rule-based layout pass
- `android/widget/ScrollView.java` — vertical scrolling with fling
- `android/view/ViewGroup.java` — full measure/layout/dispatch
- `android/view/View.java` — requestLayout, invalidate, dispatchDraw,
  onTouchEvent, onKeyDown

#### P3-T7: Intent resolution + in-process Binder stubs

Write `nova-framework/android/content/Intent.java` with `setAction`, `setData`,
`setClass`, extras.

Write single-activity dispatch:
- `startActivity(Intent)` → launch known activity
- Single-task, no back stack yet

Write Java Binder service stubs (in-process, no kernel module):
- `IWindowManager`: `openSession()` → local `IWindowSession`
- `IWindowSession`: `addToDisplay()` → Wayland surface creation,
  `relayout()` → SurfaceControl, `remove()` → destroy
- `IDisplayManager`: `getDisplayInfo()` → derive from actual Wayland output
  (not hardcoded 1920×1080), `registerCallback()` → fire on resize
- `IPackageManager`: `getApplicationInfo()`, `getActivityInfo()`,
  `checkPermission()` → GRANTED, `resolveIntent()` → activity

Register at startup: `ServiceManager.addService("window", ...)`

#### P3-T8: WebView provider architecture

WebView is required for OAuth login in almost all modern apps.

Implement as a custom WebView provider (not thin Java shim):
- Write `libnova_android/nova_webview.c`:
  - Initialize WPE WebKit renderer
  - Create Wayland subsurface in the app's existing `wl_surface` hierarchy
  - Route URL loading from WebView Java API → WPE
  - Route touch/click events from Wayland → WPE
  - Route WPE render frames → dmabuf → app surface via `zwp_linux_dmabuf_v1`
- Write `nova-framework/android/webkit/WebView.java`:
  - `loadUrl(String url)`, `setWebViewClient()`, `setWebChromeClient()`
  - `evaluateJavascript(String, ValueCallback)`
  - `getSettings()` → `WebSettings` (JavaScript enabled, user agent)
  - `onPageFinished`, `onReceivedError`, `shouldOverrideUrlLoading`
  - `onReceivedTitle`, `onProgressChanged`
- **Not implemented** in Phase 3: cookies, localStorage, HTTP auth, file
  chooser, popup windows — documented as known gaps for Phase 7

This is a **prototype-phase WebView** that should be revisited when the window
ownership model stabilizes (already settled as per-app connections, so the
subsurface approach is stable).

#### Gate Test — Phase 3

```
# Simple Calculator: buttons render with text, digit input, result computed
# KeePassDX: database list screen renders with text and icons
# NewPipe: home screen renders, no crash on WebView (offline mode accepted)
# drawText, drawBitmap, drawCircle, drawLine JNI stubs are fully implemented
```

---

## Phase 4 — Multi-Process Daemon + libbinder_rpc (~2 months)

**Goal:** Transition from single-process to daemon architecture.

### Rationale

The single-process approach (Phases 0–3) works for individual APKs but doesn't
scale to multiple APKs, background services, or inter-app Intents.

This plan uses **per-app Wayland connections**. Each app process opens its own
`wl_display` and creates its own `wl_surface` + `xdg_toplevel`. The daemon is a
pure IPC bus for Android system services. It **does not** own any Wayland
display or surfaces.

### Architecture

```
┌─────────────────────────────────┐    Unix socket    ┌──────────────────────────────┐
│ App Process A                    │◄────────────────►│  nova-daemon                 │
│                                  │  libbinder_rpc   │                              │
│  ART + Java framework            │                  │  RpcServer                   │
│  ClientCompositorAdapter         │                  │  ├─IActivityManagerService   │
│    ├─wl_display (own)            │                  │  ├─IPackageManagerService    │
│    ├─wl_surface, xdg_toplevel   │                  │  ├─IServiceManager           │
│    ├─IWindowSession (local)     │                  │  └─IWindowManagerService     │
│    ├─Choreographer vsync        │                  │     (logical state only)     │
│    └─input dispatch             │                  │                              │
│  NovaBinderTransport (JNI)      │                  │  No Wayland display          │
│    └─IBinder → RPC bridge       │                  │  No rendering                │
└─────────────────────────────────┘                  └──────────────────────────────┘

┌─────────────────────────────────┐
│ App Process B                    │
│  (same structure, independent   │
│   wl_display, wl_surface)       │
└─────────────────────────────────┘
```

### Key components

#### ClientCompositorAdapter (app-local, C + Java)

Each app process has a `ClientCompositorAdapter` that owns all Wayland
interaction. This is the component that replaces the prototype's ad-hoc
Wayland event handling with a structured interface:

- **Wayland connection**: opens `wl_display`, binds globals
- **Surface management**: creates `wl_surface`, `xdg_toplevel`, handles
  `configure` (→ resize event → `Activity.onConfigurationChanged`),
  handles `close` (→ `Activity.finish()`)
- **VSync**: `wl_surface.frame` callback → `Choreographer.doFrame()`
- **IWindowSession**: local implementation of `addToDisplay()`,
  `relayout()`, `remove()` using the app's own Wayland surface
  - `addToDisplay()` → create `wl_surface` + `xdg_toplevel`
  - `relayout()` → update `wl_egl_window` dimensions, handle resize
  - `remove()` → destroy `wl_surface`
- **Input dispatch**: `wl_seat` → pointer/keyboard events → Android
  `MotionEvent`/`KeyEvent` → view tree dispatch
- **Display metrics**: derived from Wayland output events (logical size,
  physical size, scale factor), NOT hardcoded
- **Naming**: `vendor/nova/libnova_android/client_compositor.c` +
  `nova-framework/src/nova/internal/ClientCompositorAdapter.java`

The `ClientCompositorAdapter` does **not** need to talk to the daemon for
any of the above — all Wayland interaction is local to the app process.

#### WindowPolicyService (daemon-side, logical state only)

The daemon's `IWindowManagerService` manages **logical** window state
(what a traditional Android WindowManager does at the framework level):

- **Activity stack**: which activities exist, their order, focus state
- **Task management**: grouping activities into tasks, task ordering
- **Lifecycle**: `startActivity()` → resolve intent → pick existing or
  new task → notify `IActivityManager`
- **Focus**: which activity currently has input focus (the Wayland
  compositor sends `wl_surface.enter`/`leave` events to each app; apps
  forward focus changes to the daemon)

It does **not** manage:
- Physical window geometry (Wayland compositor does that)
- Surface creation/destruction (app's `ClientCompositorAdapter` does that)
- Rendering (app does that)

#### NovaBinderTransport — Java-to-C++ Binder bridge

A critical missing piece from v5: how does Java framework code (running in
the app process) call services registered in the C++ daemon?

Write `nova-framework/src/nova/internal/NovaBinderTransport.java`:

```java
// Connects Java IBinder to libbinder_rpc C++ session via JNI
public class NovaBinderTransport {
    static { System.loadLibrary("nova_binder_transport"); }

    // Connect to daemon's RPC session
    private static native long nativeConnect(String socketPath);

    // Transact: serialize Parcel → send via RPC → deserialize reply
    private static native boolean nativeTransact(long sessionHandle,
        int code, long dataParcel, long replyParcel, int flags);
}
```

And `libnova_binder_transport/transport_jni.cpp`:
- Wraps `RpcSession` from `libbinder_rpc`
- `nativeConnect()`: calls `RpcSession::make()`, then
  `setupUnixDomainClient(socketPath)`, stores session pointer in native handle
- `nativeTransact()`: serializes Java `Parcel` to binary, sends via
  `session->transact()`, deserializes response into Java `Parcel`

This allows Java AIDL-style service proxies to transparently call daemon
services through the RPC bridge.

#### Local vs remote service split

| Service | Phase 0–3 (single-process) | Phase 4+ (multi-process) |
|---|---|---|
| IPackageManager | In-process Java stub | Daemon C++ via RPC |
| IActivityManager | In-process Java stub | Daemon C++ via RPC |
| IServiceManager | In-process Java | Daemon C++ root object |
| IWindowManager | Local (ClientCompositorAdapter) | Split: logical state in daemon, physical in ClientCompositorAdapter |

### Tasks

#### P4-T1: Standalone libbinder_rpc integration test

Write `tests/nova_binder_test.cpp`:
- Create `RpcServer::make()` bound to Unix domain socket
- Call `setupUnixDomainServer("/tmp/nova-test.sock")`
- Start a thread that calls `server->join()`
- Create `RpcSession::make()`
- Call `setupUnixDomainClient("/tmp/nova-test.sock")`
- Call `session->getRootObject()` — verify non-null
- Create a simple `ITestService` that echoes a string
- Verify round-trip IPC
- Test callback from daemon to client (server calls a method on the client)

Build with Soong `cc_test_host { ... }`.

#### P4-T2: Write NovaBinderTransport JNI bridge

Write `libnova_binder_transport/transport_jni.cpp`:
- `nativeConnect(socketPath)` → create `RpcSession`, connect to daemon
- `nativeTransact(sessionHandle, code, dataParcelHandle, replyParcelHandle, flags)`
  → serialize data Parcel → `session->transact()` → deserialize reply Parcel
- `nativeDisconnect(sessionHandle)` → destroy RpcSession

Write `NovaBinderTransport.java` matching the bridge interface above.
Register in the framework's `ServiceManager` as the transport layer:
when an app calls `ServiceManager.getService("package")`, it returns a
proxy backed by the RPC transport.

#### P4-T3: Daemon process

Write `vendor/nova/nova-daemon/src/main.cpp` (C++ for libbinder_rpc):
- Parse command line: `nova-daemon [--socket-path PATH]`
- Create `RpcServer` on Unix domain socket at
  `$XDG_RUNTIME_DIR/nova-daemon.sock`
- Register root service object (`INovaService`)
- Register system service stubs:
  - `IActivityManagerService` — app lifecycle, activity stack
  - `IPackageManagerService` — package resolution, installed APKs
  - `IWindowManagerService` — logical window state only (activity stack,
    task order, focus — NOT geometry, NOT Wayland)
  - `IServiceManager` — the service registry
- Accept client connections, create per-session threads
- No Wayland display — daemon is pure IPC bus
- Services are implemented as C++ AIDL-style interfaces using libbinder_rpc's
  templated service pattern

#### P4-T4: Client process with ClientCompositorAdapter

Write `vendor/nova/src/nova_client.c` (the `nova` binary becomes a client
launcher that also embeds the ClientCompositorAdapter):
- Parse `nova --package com.example.app` or `nova /path/to/app.apk`
- Connect to daemon socket via `NovaBinderTransport.nativeConnect()`
- Call `IActivityManagerService.startActivity(package, activity)` via the
  Binder transport bridge
- Set up ART + framework in-process (same as Phase 0–3)
- Initialize `ClientCompositorAdapter`:
  - Open own `wl_display` connection
  - Bind globals (`wl_compositor`, `xdg_wm_base`, `wl_seat`, etc.)
  - Create `wl_surface` + `xdg_toplevel`
  - Enter Wayland dispatch loop
- Wire `IWindowSession` operations to `ClientCompositorAdapter`:
  - `addToDisplay()` → creates `wl_surface`
  - `relayout()` → resizes `wl_egl_window`
  - `remove()` → destroys `wl_surface`

The `nova` binary now has two modes:
- `nova --standalone /path/to.apk` — single-process (Phase 0–3 compatible)
- `nova /path/to.apk` (default) — connect to daemon

#### P4-T5: Split bootclasspath

Daemon bootclasspath: `IServiceManager`, system service interface classes,
Binder transport types (lighter — just what the daemon needs to expose
service stubs).
Client bootclasspath: full app framework (Activity, View, Canvas, etc.),
plus `NovaBinderTransport` and service proxy classes.

#### P4-T6: Service death + reconnection

The daemon must handle client death and restart:
- `RpcSession` death recipient notifications from libbinder_rpc
- Reap zombie app processes
- Clean up per-app `IWindowManagerService` state
- Client detects daemon restart → reconnect and re-register windows

#### Gate Test — Phase 4

```
# libbinder_rpc integration test passes (echo, callback)
# NovaBinderTransport JNI bridge: Java → C++ RPC round-trip works
# nova-daemon starts, listens on Unix socket
# nova --package com.simplemobiletools.calculator launches app
#   - App's ClientCompositorAdapter opens its own Wayland window
#   - App queries PackageManager from daemon (not local stub)
#   - Window title, resize, close all work through ClientCompositorAdapter
```

---

## Phase 5 — ARM64 Native Libraries (~3 months, after Phase 1.5)

**Goal:** APKs with `lib/arm64-v8a/` native libraries execute on x86_64 host.

### Prerequisite

Phase 1.5 must be complete — general x86_64 Android `.so` loading must work
before ARM64 translation adds complexity on top.

### Approach

Use Google's `libndk_translation.so` (from ChromeOS guybrush R134+) or Intel's
`libhoudini.so` (from WSA). Both are ARM64→x86_64 binary translators proven in
Android-x86, ChromeOS ARCVM, and Redroid.

**Note:** Unlike v5, this plan does NOT claim the native bridge can be
registered via `binfmt_misc` or system properties alone. The native bridge
mechanism in `libart.so` (`ro.dalvik.vm.native.bridge`) is the correct entry
point — ART calls `NativeBridgeLoadLibrary()` when a native lib's CPU ABI
doesn't match the host.

### Tasks

#### P5-T1: Acquire translation library

Extract `libndk_translation.so` from ChromeOS guybrush R134+ recovery image:
- Download from `cros.tech` → `guybrush` → R134+
- Extract `vendor.raw.img` → `usr/lib64/libndk_translation.so`

Alternative: Extract `libhoudini.so` from WSA MSIX bundle (Microsoft Store
`9P3395VX91MR`).

#### P5-T2: Write native bridge loader

Write `libnova_native_bridge/nova_native_bridge.c`:
- Implements ART's native bridge interface:
  - `NativeBridgeIsSupported(abi)` → true for `arm64-v8a`
  - `NativeBridgeLoadLibrary(path)` → dlopen translation lib, route to ARM
    `.so`
  - `NativeBridgeGetTrampoline(...)` → return translator trampoline
- Set the `ro.dalvik.vm.native.bridge` property via SystemProperties

This is a **controlled integration** — only APKs with `lib/arm64-v8a/` trigger
the bridge. x86_64 APKs use the Phase 1.5 native ABI path and skip translation.

#### P5-T3: Validate with ARM64 test APK

Test with glmark2 ARM64 build:
- Must load `libglmark2-android.so` (ARM64)
- Benchmark must complete without signal crashes
- FPS score must be plausible (host CPU translating ARM→x86 is slower than
  native, but should complete)

#### Gate Test — Phase 5

```
# libndk_translation.so loads via ART native bridge
# glmark2-arm64.apk runs and produces FPS output
# No ILP32/Aarch64 decode crashes
```

---

## Phase 6 — Vulkan via gfxstream (~3 months)

**Goal:** Vulkan rendering via host GPU using gfxstream single-process IPC.

gfxstream supports direct-function-call mode (no virtio-gpu) where guest encoder
and host decoder run in the same process. This is verified by consulting
gfxstream source code at `hardware/google/gfxstream/` and its documentation.

### Tasks

#### P6-T1: Build gfxstream backend

```bash
cd hardware/google/gfxstream
m libgfxstream_backend
# Result: out/host/linux-x86/lib64/libgfxstream_backend.so
```

If gfxstream is not present in the AOSP checkout, add it as a git submodule or
vendor copy under `vendor/nova/deps/gfxstream/`.

#### P6-T2: Wire as libvulkan.so replacement

Write `libnova_vulkan/nova_vulkan.c`:
- Implement `vkCreateInstance` → gfxstream guest encoder init
- Implement `vkEnumeratePhysicalDevices` → return host GPU (hardware-agnostic
  detection via EGL or DRM)
- Implement `vkCreateDevice` → gfxstream host decoder GPU context
- Implement `vkGetDeviceQueue`, `vkAllocateMemory`, `vkCreateBuffer`, etc.
- Route all Vulkan commands through gfxstream encoder → host decoder chain

The linker namespace redirects `libvulkan.so` → `libnova_vulkan.so`.

#### P6-T3: Vulkan WSI for Wayland

Write `libnova_vulkan/nova_vulkan_wsi.c`:
- Implement `VK_KHR_wayland_surface` — `vkCreateWaylandSurfaceKHR` creates
  `wl_surface` (in the app's own connection) backed by dmabuf
- Use `VK_EXT_image_drm_format_modifier` for buffer negotiation
- Use `zwp_linux_dmabuf_v1` Wayland protocol for buffer sharing

Gate test is a minimal Vulkan triangle, not hardcoded GPU model names.

#### Gate Test — Phase 6

```
# vkCreateInstance succeeds via gfxstream
# vkEnumeratePhysicalDevices returns at least one GPU
# vkGetPhysicalDeviceProperties returns Vulkan 1.x
# Vulkan triangle renders through Wayland (no Mesa software fallback)
```

---

## Phase 7 — Ecosystem + Missing Architecture (Ongoing)

**Goal:** Production-quality experience.

### Tasks

#### P7-T1: Multi-window support

Each `nova --package` process gets its own `xdg_toplevel`. Window management
via daemon's `IWindowManager` state tracking (not physical surface management).

#### P7-T2: App installation flow

- `nova install /path/to/app.apk` — copies APK, runs dex2oat, registers in
  PackageManager
- `nova list` — lists installed packages
- `nova uninstall <package>` — removes APK + app data
- Persistent storage: `$XDG_DATA_HOME/nova/` with per-app data directories

#### P7-T3: ADB debugging

- `adb connect` via TCP loopback to `nova-daemon`
- `adb install`, `adb logcat`, `adb shell`

#### P7-T4: MediaCodec → VA-API

- `android.media.MediaCodec` JNI → VA-API for hardware decode
- `android.media.MediaExtractor` → FFmpeg demuxer
- H.264, H.265, VP9 support

#### P7-T5: WebView gap filling

- Cookies, localStorage, session storage
- HTTP auth dialogs
- File chooser (web → native file picker)
- Popup window support

#### P7-T6: Clipboard + drag-and-drop

- Wayland `wl_data_device` for clipboard
- Primary selection support
- Drag-and-drop from Wayland to Android

#### P7-T7: IME + keyboard layout

- Wayland `zwp_input_method_v1` or `text-input` protocol
- xkbcommon keymap → Android keycode translation
- The prototype currently ignores the Wayland keymap and does a simplistic
  keycode mapping — this must be replaced with `xkb_state_*` for correct
  layout-agnostic key delivery

#### P7-T8: Display metrics

- Derive display info from Wayland output events (logical size, physical size,
  scale factor)
- Android dp/px conversion using compositor scale
- Handle resize (`xdg_toplevel.configure`) → activity config change
- Fractional scaling via `wp_fractional_scale_v1`

#### P7-T9: Accessibility

Even if deferred, document the scope:
- `android.accessibilityservice.AccessibilityService` — not implementing
- `View.onInitializeAccessibilityNodeInfo()` — return empty node info
- Screen reader (TalkBack) — out of scope for v1
- Focus navigation via Tab key — should work naturally if View focus system
  is complete

#### P7-T10: Multi-app scalability

Current architecture is per-app-process. For 10+ apps:
- RSS per app process: ART + framework ≈ 150–250MB per process
- 10 apps → 1.5–2.5GB RSS
- No sharing between processes (no zygote)
- Potential optimization: fork from prewarmed ART zygote process to share
  pages (copy-on-write), dramatically reducing per-app RSS

Add zygote to architectural backlog.

#### P7-T11: Porter-Duff compositing

The softgfx Canvas currently overwrites (replace mode). Android apps expect
Porter-Duff blending modes (`SRC_OVER`, `SRC_IN`, `SCREEN`, etc.):

Extend softgfx with:
- `nova_canvas_set_composite_mode(int mode)` — set Porter-Duff mode
- Per-pixel alpha blending in `fill_span()`
- Support `Paint.setXfermode(Xfermode)` in Java shim

---

## Known Architectural Gaps (Documented, Not Yet Scheduled)

| Gap | Impact | Notes |
|---|---|---|
| Zygote / prewarmed ART process | High per-app startup time, high RSS | Phase 4+ optimization |
| XML drawable shapes + selectors | Apps with custom UI crash or draw wrong | Phase 3-T3 |
| Nine-patch / 9-png images | Stretchable button backgrounds broken | Phase 3 gap |
| Vector drawable + AnimatedVector | Material Design icons missing | Phase 7 |
| Porter-Duff compositing | Screenshots/overlay apps render wrong | Phase 7-T11 |
| Multi-display / external monitor | Apps see wrong display dimensions | Phase 7-T8 |
| Back gesture / system UI navigation | App has no system gesture zone | Phase 7 |
| DownloadManager | APK download from F-Droid | Phase 7 |
| Notifications | No Android notification display | Phase 7 |

---

## Summary

| Phase | Duration | Gate App | Key Deliverable |
|---|---|---|---|
| 0 — Foundation | ~1 month | (empty window) | AOSP builds, ART boots, Wayland window |
| 1 — GLES Port | ~1 month | gles3jni | Working Nova (port prototype to Soong) |
| 1.5 — Native ABI | ~1 month | NDK test APK | x86_64 Android .so loading on host |
| 2 — Audio+Animation | ~2 months | Material Life, Pixel Dungeon | Choreographer VSync, PipeWire, TextureView |
| 3 — Text+Resources | ~2 months | Calculator, KeePassDX | Text, layout inflation, WebView, drawables |
| 4 — Multi-Process | ~2 months | Calculator via daemon | libbinder_rpc daemon, per-app Wayland |
| 5 — ARM64 Native | ~3 months | glmark2 ARM64 | libndk_translation via ART native bridge |
| 6 — Vulkan | ~3 months | Vulkan triangle | gfxstream single-process Vulkan |
| 7 — Ecosystem | ongoing | F-Droid | Zygote, clipboard, IME, MediaCodec, Porter-Duff |

### Code Reuse Summary (verified from prototype)

| Source | Lines | Port To |
|---|---|---|
| `src/art.c` | 716 | `nova/src/art.c` |
| `src/wayland.c` | 421 | `libnova_android/nova_wayland.c` |
| `src/egl.c` | 96 | `libnova_egl/nova_egl.c` |
| `src/canvas_render.c` | 172 | `libnova_android/nova_canvas_render.c` |
| `src/softgfx.c` | 317 | `libnova_android/nova_softgfx.c` |
| `src/main.c` | 115 | `nova/src/main.c` |
| `src/jni/*.c` (16 JNI modules + 2 aux) | ~1,500 | `libnova_*/` JNI modules |
| `src/java/nova-shims/` (153 files) | ~3,000 | `nova-framework/src/` |
| `src/java/aosp/` (24 files) | ~3,000 | `nova-framework/src/` |
| **Total reusable** | **~5 categories, ~9,300 LOC** | |
| **New code for full plan** | **~12,000 LOC** | (text, resources, WebView, audio, daemon, Vulkan) |
| **Grand total** | **~21,000 LOC** | nova runtime |

### Revision history

| Version | Changes |
|---|---|
| v5 | Initial plan (Apr 2026) — broken Wayland model, plausible Bionic shim, factual errors |
| v6 | Corrected Wayland (per-app connections), Phase 1.5 added, counts/caps fixed (May 2026) — but Phase 1.5 approach and Phase 4 split still flawed |
| **v7** | **Phase 1.5 rewritten** (targeted symbol audit + Android linker, not Bionic build), **Phase 4 split defined** (ClientCompositorAdapter, NovaBinderTransport, WindowPolicyService), Choreographer/AssetManager pre-existence noted, override count fixed |
