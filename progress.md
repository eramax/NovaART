# NovaART Progress

This is a hand-maintained engineering log. Entries record completed and verified
milestones only.

## 2026-05-18 16:36 CEST

Milestone:
- Reached visible on-screen rendering for the scoped Phase 1 `gles3jni`
  host-native path and added Wayland server-side decoration negotiation.

Completed:
- Fixed the EGL bridge so `GLSurfaceView` and the NovaART Wayland window path
  use a consistent display/config pair:
  - [src/jni/com_google_android_gles_jni_EGLImpl.c](/mnt/mydata/projects2/qos/deps/NovaART/src/jni/com_google_android_gles_jni_EGLImpl.c)
  - [src/egl.c](/mnt/mydata/projects2/qos/deps/NovaART/src/egl.c)
- Completed the stub activity/view path enough for `GLSurfaceView` to attach,
  receive `SurfaceHolder` callbacks, and run its GL thread against the visible
  Wayland surface:
  - [src/java/nova-shims/android/app/Activity.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/app/Activity.java)
  - [src/java/nova-shims/android/view/View.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/View.java)
  - [src/java/nova-shims/android/view/SurfaceView.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/SurfaceView.java)
  - [src/java/nova-shims/nova/internal/Launcher.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/nova/internal/Launcher.java)
  - [src/java/aosp/android/opengl/GLSurfaceView.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/aosp/android/opengl/GLSurfaceView.java)
- Added `xdg-decoration-unstable-v1` negotiation and request for
  compositor-provided server-side decorations:
  - [src/nova.h](/mnt/mydata/projects2/qos/deps/NovaART/src/nova.h)
  - [src/meson.build](/mnt/mydata/projects2/qos/deps/NovaART/src/meson.build)
  - [src/wayland.c](/mnt/mydata/projects2/qos/deps/NovaART/src/wayland.c)

Verified:
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-host`
- visual confirmation: `gles3jni` renders on screen and rotates in the NovaART
  Wayland window
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-framework`
- phase1 APK execution checks:
  - `gles3jni.apk`: launch path reaches ART, launcher, and EGL setup, but a
    repeated run currently fails in native-lib staging with:
    `java.nio.file.FileAlreadyExistsException` on
    `output/android-data/dex/native-libs/libgles3jni.so`
  - `glmark2.apk`: ART and launcher path start, but launch fails on the APK's
    Android/Bionic native library:
    `libglmark2-android.so` requires `LIBC` symbol versions that the current
    glibc host runtime cannot satisfy

Current situation:
- Scoped Phase 1 rendering is real and visible for the host-native
  `gles3jni` path.
- Server-side decoration negotiation is implemented, but button visibility still
  depends on compositor support for `xdg-decoration`.
- `glmark2.apk` remains blocked by the general Android-native `.so` ABI gap,
  which is outside the scoped host-native `gles3jni` replacement.

Next:
- Fix repeated-run native-lib staging so `gles3jni.apk` relaunches cleanly.
- Update smoke success criteria to match real render-path evidence instead of
  the old native log expectation.

## 2026-05-18 16:39 CEST

Milestone:
- Closed the Phase 1 gate in the scoped sense defined by the current plan:
  `gles3jni` launches and renders visibly on screen via the NovaART Wayland +
  EGL + `GLSurfaceView` path.

Completed:
- Updated the bounded smoke test to reflect the real success criteria for the
  current renderer path instead of obsolete native sample log markers:
  - [scripts/smoke-run-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/smoke-run-gles3jni.sh)
- Made repeated smoke runs reliable by clearing the staged
  `libgles3jni.so` before relaunch.
- Added direct operator invocation support:
  - [Makefile](/mnt/mydata/projects2/qos/deps/NovaART/Makefile)
    - `make run /abs/path/to.apk`

Verified:
- `bash /mnt/mydata/projects2/qos/deps/NovaART/scripts/smoke-run-gles3jni.sh --timeout 6`
- result:
  - `smoke run passed: render path stayed alive through EGL and GLThread activity for 6s`
- visual confirmation remains true:
  - `gles3jni` window appears and the triangle rotation is visible on screen

Phase status:
- **Phase 1: complete** for the scoped `gles3jni` host-native validation path
- **Not implied complete:** general Android native `.so` execution
  - `glmark2.apk` still fails on the known Bionic-vs-glibc ABI boundary
  - that limitation is already explicitly deferred to the later native-library
    phase in the plan

Next:
- Remove temporary deep debug logging from `Launcher` and `GLSurfaceView`.
- Decide whether to keep `xdg-decoration` only or add a `libdecor` fallback for
  compositors that ignore server-side decorations.

## 2026-05-18 16:16 CEST

Milestone:
- Completed the scoped Phase 1 host-native `gles3jni` replacement path to the
  point where the app survives the bounded smoke run without bootstrap/runtime
  failures.

Completed:
- Fixed host build toolchain selection so mixed C/C++ host builds work
  reliably with the available AOSP clang prebuilts:
  - [build-host.sh](/mnt/mydata/projects2/qos/deps/NovaART/build-host.sh)
- Imported and adapted the official AOSP `gles3jni` native sample sources for
  host compilation:
  - [third_party/gles3jni/gles3jni.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gles3jni.cpp)
  - [third_party/gles3jni/gles3jni.h](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gles3jni.h)
  - [third_party/gles3jni/RendererES2.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/RendererES2.cpp)
  - [third_party/gles3jni/RendererES3.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/RendererES3.cpp)
  - [third_party/gles3jni/gl3stub.c](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gl3stub.c)
  - [third_party/gles3jni/gl3stub.h](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gl3stub.h)
- Added host-side Android log compatibility and linked a host-native
  `libgles3jni.so` into the NovaART build:
  - [src/compat/android/log.h](/mnt/mydata/projects2/qos/deps/NovaART/src/compat/android/log.h)
  - [src/host_android_log.c](/mnt/mydata/projects2/qos/deps/NovaART/src/host_android_log.c)
  - [src/meson.build](/mnt/mydata/projects2/qos/deps/NovaART/src/meson.build)
  - [scripts/test-host-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-host-gles3jni.sh)
- Updated the launcher so the extracted APK `libgles3jni.so` is explicitly
  replaced with the host-built one before classloader-native loading:
  - [src/java/nova-shims/nova/internal/Launcher.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/nova/internal/Launcher.java)

Verified:
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-host`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART test-host-gles3jni`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-framework`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART smoke-gles3jni TIMEOUT=8`
- Observed launcher replacement log:
  - `Staged libgles3jni.so from /mnt/mydata/projects2/qos/deps/NovaART/output/lib/libgles3jni.so`
- Smoke result:
  - `smoke run passed: process stayed alive for 8s without bootstrap failures`

Current situation:
- The scoped Phase 1 tactic is now functioning:
  - ART bootstrap works
  - framework overlay works
  - EGL/JNI bridge works well enough for `gles3jni` launch
  - host-native `libgles3jni.so` is loaded instead of the APK’s Bionic one
- This remains a scoped validation path, not a general solution for arbitrary
  Android-native APK libraries.

Next:
- Move from survival-only smoke to concrete rendering validation:
  - verify actual GL initialization and frame execution inside the host-native
    `gles3jni` renderer
  - add a stronger smoke/assertion that proves renderer entry beyond mere
    process survival

## 2026-05-18 12:18 CEST

Milestone:
- Added a curated dependency patch workflow so local AOSP dependency fixes can
  be reproduced after a fresh sync.

Completed:
- Added checked-in dependency patch storage:
  - [patches/deps/aosp-full/prebuilts-clang-host-linux-x86.patch](/mnt/mydata/projects2/qos/deps/NovaART/patches/deps/aosp-full/prebuilts-clang-host-linux-x86.patch)
- Added dependency preparation script with `apply`, `status`, and `export`
  modes:
  - [scripts/prepare-deps.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/prepare-deps.sh)
- Added operator targets:
  - [Makefile](/mnt/mydata/projects2/qos/deps/NovaART/Makefile)
    - `make prepare-deps`
    - `make export-deps-patches`
    - `make status-deps-patches`
- Explicitly kept the dependency patch set curated instead of sweeping in the
  whole `deps/aosp-full` dirty state. The current `master-art` client reports
  a lot of non-Nova noise under `art/` and `build/make/`, so the stored patch
  set currently includes only the verified host-ART bootstrap fix in
  `prebuilts/clang/host/linux-x86/Android.bp`.

Verified:
- dependency patch source confirmed from:
  - `deps/aosp-full/prebuilts/clang/host/linux-x86/Android.bp`
- curated patch workflow wired into the top-level operator surface

Current situation:
- `make prepare-deps` is now the reproducible entry point for the known-safe
  dependency patch.
- Any future intentional dependency edits should be added to the curated
  whitelist before being exported.

## 2026-05-18 12:05 CEST

Milestone:
- Traced `gles3jni` native-library loading up to the real ABI boundary between
  Android/Bionic native code and the current glibc host runtime.

Completed:
- Implemented and verified the first render-path JNI bridge for the OpenGL/EGL
  side:
  - [src/jni/com_google_android_gles_jni_EGLImpl.c](/mnt/mydata/projects2/qos/deps/NovaART/src/jni/com_google_android_gles_jni_EGLImpl.c)
  - [src/jni/com_google_android_gles_jni_GLImpl.c](/mnt/mydata/projects2/qos/deps/NovaART/src/jni/com_google_android_gles_jni_GLImpl.c)
  - [src/jni/android_runtime.c](/mnt/mydata/projects2/qos/deps/NovaART/src/jni/android_runtime.c)
  - [src/jni/meson.build](/mnt/mydata/projects2/qos/deps/NovaART/src/jni/meson.build)
- Extended the Java launcher and runtime staging path to diagnose and satisfy
  loader-level host dependencies:
  - [src/java/nova-shims/nova/internal/Launcher.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/nova/internal/Launcher.java)
  - [src/art.c](/mnt/mydata/projects2/qos/deps/NovaART/src/art.c)
  - [scripts/smoke-run-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/smoke-run-gles3jni.sh)
- Added focused verification for launcher/native-lib staging and host Android
  shim outputs:
  - [scripts/test-launcher-native-libs.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-launcher-native-libs.sh)
  - [scripts/test-host-android-shims.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-host-android-shims.sh)
  - [Makefile](/mnt/mydata/projects2/qos/deps/NovaART/Makefile)
- Added a minimal host `libandroid.so` shim and its build/install path:
  - [src/host_libandroid.c](/mnt/mydata/projects2/qos/deps/NovaART/src/host_libandroid.c)
  - [src/meson.build](/mnt/mydata/projects2/qos/deps/NovaART/src/meson.build)
- Added compatibility logging/header support for a host-native `gles3jni`
  library path and imported the official AOSP sample native sources for that
  alternative:
  - [src/compat/android/log.h](/mnt/mydata/projects2/qos/deps/NovaART/src/compat/android/log.h)
  - [src/host_android_log.c](/mnt/mydata/projects2/qos/deps/NovaART/src/host_android_log.c)
  - [third_party/gles3jni/gles3jni.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gles3jni.cpp)
  - [third_party/gles3jni/gles3jni.h](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gles3jni.h)
  - [third_party/gles3jni/RendererES2.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/RendererES2.cpp)
  - [third_party/gles3jni/RendererES3.cpp](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/RendererES3.cpp)
  - [third_party/gles3jni/gl3stub.c](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gl3stub.c)
  - [third_party/gles3jni/gl3stub.h](/mnt/mydata/projects2/qos/deps/NovaART/third_party/gles3jni/gl3stub.h)
  - [scripts/test-host-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-host-gles3jni.sh)

Verified:
- `bash scripts/test-launcher-native-libs.sh`
- `bash scripts/test-host-android-shims.sh`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-host`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART build-framework`
- `make -C /mnt/mydata/projects2/qos/deps/NovaART smoke-gles3jni TIMEOUT=8`
- Confirmed render-path progression past these loader failures:
  - `EGLImpl._nativeClassInit()`
  - missing `libGLESv3.so`
  - missing `libandroid.so`
- Confirmed from ELF metadata that the APK-bundled
  `output/android-data/dex/native-libs/libgles3jni.so` is not glibc-native:
  - `readelf --version-info` shows `LIBC` version requirements on
    `libc.so`, `libm.so`, and `libdl.so`
  - the current failure is:
    - ``/lib/x86_64-linux-gnu/libc.so.6: version `LIBC' not found``

Current situation:
- ART bootstrap is working.
- Java framework overlay and launch path are working.
- EGL/JNI bridge work is far enough for the app to reach its GL render thread.
- The remaining blocker is no longer a missing file. It is an ABI mismatch:
  the APK’s native `libgles3jni.so` is built for Android/Bionic, while NovaART
  currently runs inside a glibc host process.

Next:
- Decide explicitly between two directions:
  - scoped Phase 1 tactic: build and stage a host-native replacement
    `libgles3jni.so` from the imported AOSP sample sources
  - general architecture: redesign native-library execution around real
    Android/Bionic compatibility instead of glibc-host loading

## 2026-05-18 11:36 CEST

Milestone:
- Reached real APK launch-path execution through activity lifecycle and
  `GLSurfaceView` surface startup.

Completed:
- Added native APK launch probing with automatic activity discovery via `aapt2`:
  - [src/art.c](/mnt/mydata/projects2/qos/deps/NovaART/src/art.c)
  - [src/main.c](/mnt/mydata/projects2/qos/deps/NovaART/src/main.c)
  - [src/nova.h](/mnt/mydata/projects2/qos/deps/NovaART/src/nova.h)
- Added Java-side launch helper:
  - [src/java/nova-shims/nova/internal/Launcher.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/nova/internal/Launcher.java)
- Added the minimal app/view runtime shim layer needed to load and instantiate
  `GLES3JNIActivity` and construct `GLES3JNIView`:
  - [src/java/nova-shims/android/app/Activity.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/app/Activity.java)
  - [src/java/nova-shims/android/app/Application.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/app/Application.java)
  - [src/java/nova-shims/android/content/Context.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/content/Context.java)
  - [src/java/nova-shims/android/os/Bundle.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Bundle.java)
  - [src/java/nova-shims/android/util/Log.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/util/Log.java)
  - [src/java/nova-shims/android/util/AttributeSet.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/util/AttributeSet.java)
  - [src/java/nova-shims/android/os/Trace.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Trace.java)
  - [src/java/nova-shims/android/view/View.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/View.java)
  - [src/java/nova-shims/android/view/SurfaceHolder.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/SurfaceHolder.java)
  - [src/java/nova-shims/android/view/SurfaceView.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/SurfaceView.java)
  - [src/java/nova-shims/android/view/Surface.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/Surface.java)
  - [src/java/nova-shims/javax/microedition/khronos/egl/EGL.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/javax/microedition/khronos/egl/EGL.java)
- Added simulated `SurfaceView` lifecycle dispatch so `GLSurfaceView` starts its
  render thread from the Nova stub side.
- Tightened the smoke script to fail on render-thread native linkage errors:
  - [scripts/smoke-run-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/smoke-run-gles3jni.sh)

Verified:
- `bash scripts/build-framework.sh`
- `bash build-host.sh`
- `bash scripts/smoke-run-gles3jni.sh --timeout 8`
- Observed successful progression through:
  - activity class load
  - activity instantiation
  - `onCreate()`
  - `onResume()`
  - `GLSurfaceView` content view creation
  - simulated surface lifecycle
  - render thread startup

Next:
- Implement the first EGL10 JNI bridge in `com.google.android.gles_jni.EGLImpl`.
- Current first render-path blocker:
  - `com.google.android.gles_jni.EGLImpl._nativeClassInit()`

## 2026-05-18 11:23 CEST

Milestone:
- Established a reproducible bounded `gles3jni` smoke run that survives ART
  bootstrap and JNI registration.

Completed:
- Added a bounded smoke runner:
  - [scripts/smoke-run-gles3jni.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/smoke-run-gles3jni.sh)
- Added minimal runtime shims required to clear ART bootstrap and JNI
  registration:
  - [src/java/nova-shims/android/os/SystemProperties.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/SystemProperties.java)
  - [src/java/nova-shims/android/os/SystemClock.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/SystemClock.java)
  - [src/java/nova-shims/android/os/IInterface.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/IInterface.java)
  - [src/java/nova-shims/android/os/IBinder.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/IBinder.java)
  - [src/java/nova-shims/android/os/RemoteException.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/RemoteException.java)
  - [src/java/nova-shims/android/os/Parcelable.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Parcelable.java)
  - [src/java/nova-shims/android/os/Parcel.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Parcel.java)
  - [src/java/nova-shims/android/os/Binder.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Binder.java)
  - [src/java/nova-shims/android/os/Process.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/os/Process.java)
  - [src/java/nova-shims/android/view/KeyEvent.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/KeyEvent.java)
  - [src/java/nova-shims/android/view/MotionEvent.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/MotionEvent.java)
  - [src/java/nova-shims/android/graphics/Canvas.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/graphics/Canvas.java)
  - [src/java/nova-shims/android/graphics/Paint.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/graphics/Paint.java)
  - [src/java/nova-shims/android/graphics/Bitmap.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/graphics/Bitmap.java)
  - [src/java/nova-shims/com/android/internal/graphics/NativeUtils.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/com/android/internal/graphics/NativeUtils.java)
- Corrected the Java-side `Bitmap.nativeCreate` signature to match the
  registered native method descriptor `(IIZZJ)J`.

Verified:
- `bash scripts/build-framework.sh`
- `bash scripts/smoke-run-gles3jni.sh --timeout 8`
- Smoke result: process remained alive for 8 seconds without
  `ClassNotFoundException`, `NoSuchMethodError`, JNI registration failure, or
  bootstrap abort.

Next:
- Move from survival-only smoke to a concrete app/runtime milestone such as APK
  entry, activity creation, or first surface/render event.

## 2026-05-18 11:00 CEST

Milestone:
- Reached a runnable host/glibc ART runtime staged into NovaART output.

Completed:
- Fixed host ART build graph by removing the device-side `sdk_version:
  "minimum"` default from:
  - [deps/aosp-full/prebuilts/clang/host/linux-x86/Android.bp](/mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full/prebuilts/clang/host/linux-x86/Android.bp)
- Added host runtime builder:
  - [scripts/build-host-art-runtime.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/build-host-art-runtime.sh)
- Updated runtime staging to prefer real host outputs and stage i18n/tzdata:
  - [scripts/stage-art.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/stage-art.sh)
- Updated ART bootstrap defaults and classpath wiring:
  - [src/art.c](/mnt/mydata/projects2/qos/deps/NovaART/src/art.c)

Verified:
- Host artifacts exist and resolve as glibc binaries:
  - `deps/aosp-full/out/host/linux-x86/lib64/libart.so`
  - `deps/aosp-full/out/host/linux-x86/bin/dex2oat64`
  - `deps/aosp-full/out/host/linux-x86/bin/dalvikvm64`
- `bash scripts/stage-art.sh`
- Staged runtime layout exists under:
  - `output/lib`
  - `output/android-root/apex/com.android.art`
  - `output/android-root/com.android.i18n`
  - `output/android-root/com.android.tzdata`

Next:
- Keep tightening runtime bootstrap until `gles3jni` survives startup cleanly.

## 2026-05-18 10:20 CEST

Milestone:
- Framework overlay build became reproducible and dexable.

Completed:
- Added build and verification scripts:
  - [scripts/build-framework.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/build-framework.sh)
  - [scripts/test-build-framework.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-build-framework.sh)
- Added initial Nova-owned shims needed for framework compilation:
  - [src/java/nova-shims/android/app/AppGlobals.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/app/AppGlobals.java)
  - [src/java/nova-shims/android/opengl/GLErrorWrapper.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/opengl/GLErrorWrapper.java)
  - [src/java/nova-shims/android/opengl/GLLogWrapper.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/opengl/GLLogWrapper.java)
  - [src/java/nova-shims/android/view/DisplayInfo.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/DisplayInfo.java)
  - [src/java/nova-shims/android/view/InputChannel.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/InputChannel.java)
  - [src/java/nova-shims/android/view/SurfaceControl.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/SurfaceControl.java)
  - [src/java/nova-shims/android/view/WindowRelayoutResult.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/view/WindowRelayoutResult.java)
  - [src/java/nova-shims/android/compat/annotation/UnsupportedAppUsage.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/nova-shims/android/compat/annotation/UnsupportedAppUsage.java)
- Patched:
  - [src/java/aosp/android/opengl/GLSurfaceView.java](/mnt/mydata/projects2/qos/deps/NovaART/src/java/aosp/android/opengl/GLSurfaceView.java)

Verified:
- `bash scripts/test-build-framework.sh`
- `bash scripts/build-framework.sh`
- Produced:
  - `out/framework/nova-framework-classes.jar`
  - `out/framework/nova-framework-dex.jar`

Next:
- Combine the framework overlay with a runnable host ART runtime.

## 2026-05-18 09:50 CEST

Milestone:
- Staged the minimal Phase 1 framework/AIDL source surface into NovaART.

Completed:
- Added source staging and verification:
  - [scripts/stage-phase1-framework-sources.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/stage-phase1-framework-sources.sh)
  - [scripts/test-stage-phase1-framework-sources.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-stage-phase1-framework-sources.sh)
- Added source map:
  - [docs/framework-source-map-phase1.md](/mnt/mydata/projects2/qos/deps/NovaART/docs/framework-source-map-phase1.md)
- Added Nova-owned AIDL generator and verification:
  - [scripts/generate-phase1-nova-aidl.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/generate-phase1-nova-aidl.sh)
  - [scripts/test-generate-phase1-nova-aidl.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/test-generate-phase1-nova-aidl.sh)

Verified:
- `bash scripts/test-stage-phase1-framework-sources.sh`
- `bash scripts/test-generate-phase1-nova-aidl.sh`
- Staged source trees exist under:
  - `src/java/aosp`
  - `src/aidl/aosp`
  - `src/generated/aidl/nova`

Next:
- Build the staged framework overlay against ART jars and SDK jars.

## 2026-05-18 09:15 CEST

Milestone:
- Established the clean AOSP layout NovaART needs.

Completed:
- Re-aligned `deps/aosp-full` to the `master-art` ART build path.
- Added a reproducible ART sync/build recipe:
  - [scripts/repro-sync-art.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/repro-sync-art.sh)
- Established a separate framework-source client:
  - `deps/aosp-framework-src`
- Added a reproducible framework-source worktree recipe:
  - [scripts/repro-framework-source-worktree.sh](/mnt/mydata/projects2/qos/deps/NovaART/scripts/repro-framework-source-worktree.sh)

Verified:
- `m com.android.art` succeeds from `deps/aosp-full`
- framework source sync succeeds for:
  - `frameworks/base`
  - `external/skia`
  - `external/freetype`
  - `external/harfbuzz_ng`
  - `external/libpng`
  - `external/icu`

Next:
- Extract only the minimal framework/AIDL surface required for Phase 1.
