# NovaART Progress

This is a hand-maintained engineering log. Entries record completed and verified
milestones only.

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
