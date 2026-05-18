# NovaART Phase 1 Framework Source Map

This file maps the Phase 1 bill of materials in `plan-v2.md` to exact source
paths in the synced framework-source tree:

- framework source tree:
  `/mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-framework-src`
- ART build tree:
  `/mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full`

Use the framework-source tree as source input only. Do not run the ART Stage 0
build there.

## Source Root

Framework root:

```text
/mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-framework-src/frameworks/base
```

## OpenGL / EGL Java Wrappers

From `frameworks/base/opengl/java/`:

```text
android/opengl/GLSurfaceView.java
android/opengl/GLDebugHelper.java
android/opengl/EGLLogWrapper.java
android/opengl/GLException.java

com/google/android/gles_jni/EGLImpl.java
com/google/android/gles_jni/EGLContextImpl.java
com/google/android/gles_jni/EGLDisplayImpl.java
com/google/android/gles_jni/EGLSurfaceImpl.java
com/google/android/gles_jni/EGLConfigImpl.java
com/google/android/gles_jni/GLImpl.java

javax/microedition/khronos/egl/EGL10.java
javax/microedition/khronos/egl/EGL11.java
javax/microedition/khronos/egl/EGLConfig.java
javax/microedition/khronos/egl/EGLContext.java
javax/microedition/khronos/egl/EGLDisplay.java
javax/microedition/khronos/egl/EGLSurface.java

javax/microedition/khronos/opengles/GL.java
javax/microedition/khronos/opengles/GL10.java
javax/microedition/khronos/opengles/GL10Ext.java
javax/microedition/khronos/opengles/GL11.java
javax/microedition/khronos/opengles/GL11Ext.java
javax/microedition/khronos/opengles/GL11ExtensionPack.java
```

## Core Java / Framework Classes

From `frameworks/base/core/java/` unless noted otherwise:

### Keep unmodified first

```text
core/java/android/os/Bundle.java
core/java/android/os/Handler.java
core/java/android/os/Looper.java
core/java/android/os/Message.java
core/java/android/os/IBinder.java
core/java/android/os/Binder.java
core/java/android/os/IInterface.java
core/java/android/os/RemoteException.java
core/java/android/os/Parcelable.java
core/java/android/os/Parcel.java
core/java/android/os/SystemClock.java
core/java/android/os/Trace.java

core/java/android/util/Log.java
core/java/android/util/AttributeSet.java
core/java/android/util/DisplayMetrics.java

core/java/android/content/Context.java
core/java/android/content/ContextWrapper.java
core/java/android/content/Intent.java
core/java/android/content/ComponentName.java
core/java/android/content/pm/ApplicationInfo.java
core/java/android/content/pm/ActivityInfo.java

core/java/android/app/Application.java

core/java/android/view/SurfaceHolder.java
core/java/android/view/Surface.java
core/java/android/view/Display.java
core/java/android/view/DisplayInfo.java
core/java/android/view/InputChannel.java
core/java/android/view/WindowRelayoutResult.java
core/java/android/view/SurfaceControl.java
core/java/android/view/WindowManager.java
```

### Fork / adapt choke points

```text
core/java/android/app/Activity.java
core/java/android/app/ActivityThread.java
core/java/android/app/Instrumentation.java
core/java/android/app/LoadedApk.java
core/java/android/app/ContextImpl.java

core/java/android/view/WindowManagerImpl.java
core/java/android/view/WindowManagerGlobal.java
core/java/android/view/ViewRootImpl.java
core/java/android/view/SurfaceView.java
core/java/android/view/View.java
core/java/android/view/ViewGroup.java
core/java/android/view/Choreographer.java
core/java/android/view/SurfaceControl.java

core/java/android/content/res/Resources.java
core/java/android/content/res/AssetManager.java
core/java/android/content/res/Configuration.java

core/java/com/android/internal/policy/PhoneWindow.java
```

## Graphics Type Paths

These are not under `core/java`; they live under `graphics/java`:

```text
graphics/java/android/graphics/PixelFormat.java
graphics/java/android/graphics/Rect.java
graphics/java/android/graphics/Point.java
```

## AIDL Sources For Local Binder-Shaped Services

```text
core/java/android/view/IWindowManager.aidl
core/java/android/view/IWindowSession.aidl
core/java/android/hardware/display/IDisplayManager.aidl
core/java/android/content/pm/IPackageManager.aidl
core/java/android/app/IActivityTaskManager.aidl
```

## MessageQueue Branch-Specific Note

On this branch, `android.os.MessageQueue` is variant-split rather than living at
`core/java/android/os/MessageQueue.java`.

Available variants:

```text
core/java/android/os/LegacyMessageQueue/MessageQueue.java
core/java/android/os/CombinedMessageQueue/MessageQueue.java
core/java/android/os/CombinedDeliMessageQueue/MessageQueue.java
core/java/android/os/DeliQueue/MessageQueue.java
```

Recommended NovaART Phase 1 choice:

```text
core/java/android/os/LegacyMessageQueue/MessageQueue.java
```

Reason:
- smallest behavioral surface
- aligns with NovaART's planned custom native `nativeInit/nativePollOnce/nativeWake`
  backend
- avoids pulling in the more concurrent queue implementation until it is needed

## Minimal First Extraction Batch

Start with the smallest batch needed for `gles3jni`:

1. OpenGL/EGL wrapper set from `opengl/java`
2. `android.os`: `Bundle`, `Handler`, `Looper`, `Message`, `LegacyMessageQueue/MessageQueue`,
   `IBinder`, `Binder`, `IInterface`, `RemoteException`, `Parcelable`, `Parcel`,
   `SystemClock`, `Trace`
3. `android.util`: `Log`, `AttributeSet`, `DisplayMetrics`
4. `android.content`: `Context`, `ContextWrapper`, `Intent`, `ComponentName`
5. `android.content.pm`: `ApplicationInfo`, `ActivityInfo`
6. `android.app`: `Application`
7. `android.graphics`: `PixelFormat`, `Rect`, `Point`
8. `android.view`: `SurfaceHolder`, `Surface`, `Display`, `DisplayInfo`,
   `InputChannel`, `WindowRelayoutResult`, `SurfaceControl`, `WindowManager`
9. AIDLs: `IWindowManager`, `IWindowSession`, `IDisplayManager`, `IPackageManager`

Leave these for the first fork/adaptation wave rather than raw copy:

```text
Activity.java
ActivityThread.java
Instrumentation.java
LoadedApk.java
ContextImpl.java
WindowManagerImpl.java
WindowManagerGlobal.java
ViewRootImpl.java
SurfaceView.java
View.java
ViewGroup.java
PhoneWindow.java
SurfaceControl.java
DisplayManagerGlobal.java
Choreographer.java
```

## Suggested NovaART Destination Layout

Suggested staging inside NovaART:

```text
src/java/aosp/...         # direct copies kept close to upstream package layout
src/java/nova/...         # Nova-owned replacements/forks
src/aidl/...              # copied AIDL inputs
src/generated/aidl/...    # generated Stub/Proxy classes
```

Keep copied upstream files separate from Nova-owned forks so future rebases are
traceable.

Current compile model:
- `scripts/build-framework.sh` does not compile every staged `src/java/aosp/...` file.
- Phase 1 currently compiles:
  - staged OpenGL/EGL wrappers
  - generated slim AIDL Java
  - Nova-owned hidden-API shims under `src/java/nova-shims/`
- the broader staged framework files remain as provenance and future fork material.
