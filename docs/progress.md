# NovaART Progress

## Architecture Decision (agreed upon)

### Strategy: Real AOSP Sources + Targeted Nova Overrides

Instead of generating thousands of stub classes from APK DEX analysis, the architecture uses:

1. **`src/java/aosp/`** — Real AOSP source files for complex framework classes (Handler, Binder, Bundle, etc.)
2. **`src/java/nova-shims/`** — ~30 hand-written overrides for classes that need Nova-specific behavior
3. **`src/generated/aidl/nova/`** — Generated AIDL stubs for IPC interface definitions
4. **`deps/aosp-full/prebuilts/sdk/35/module-lib/android.jar`** — Compile-time type reference only (not included in output)

### Binder Architecture

- **`Binder.java`** (nova-shims) — concrete class; `transact()` calls `onTransact()` directly in same JVM
- **Native JNI methods** in Binder are no-ops (return 0/false/null) — `blockUntilThreadAvailable()`, `getNativeBBinderHolder()`, `getNativeFinalizer()`, `destroy()`
- **`ServiceManager`** (nova-shims override) is the real choke point — uses `ConcurrentHashMap` to map service names to local Java objects registered at Nova bootstrap time
- **There is no kernel Binder involvement** — all IPC is in-process

### Source Layers (priority order, later overrides earlier):
```
src/java/aosp/       ← staged real AOSP implementations
src/java/nova-shims/ ← Nova overrides (these win on conflicts)
src/generated/aidl/  ← generated AIDL Java stubs
```

If same class appears in both aosp/ and nova-shims/, the build gets a duplicate-class error (intentional — prevents accidental silent wins).

---

## Build Status

### Phase 1: Framework Build — ✅ WORKING

`scripts/build-framework.sh --skip-aidl` compiles successfully.

Output: `out/framework/nova-framework-classes.jar` and `nova-framework-dex.jar`

### Phase 2: JNI Bootstrap — ✅ WORKING

All 16 JNI stubs register successfully:
- `android/os/SystemProperties` (7 native methods)
- `android/os/SystemClock` (7 native methods including `now()`)
- `android/os/Binder` (4 native methods)
- `android/os/Process` (6 native methods)
- `android/view/KeyEvent` (1 native method)
- `android/view/MotionEvent` (1 native method)
- `android/graphics/Canvas` (11 native methods)
- `android/graphics/Paint` (9 native methods)
- `android/graphics/Bitmap` (9 native methods)
- `android/graphics/BitmapFactory` (1 native method)
- `android/opengl/GLES20` (100+ native methods)
- `android/opengl/GLUtils` (3 native methods)
- `com/android/internal/graphics/NativeUtils` (1 native method)
- `com/google/android/gles_jni/EGLImpl` (30+ native methods)
- `com/google/android/gles_jni/GLImpl` (1 native method: `_nativeClassInit`)
- `nova/canvas/render` (frame submission, vsync, input dispatch)

### Phase 2: APK Launch — PARTIALLY WORKING (gles3jni)

The gles3jni APK successfully:
1. ✅ ART runtime initializes
2. ✅ All 16 JNI stubs register
3. ✅ EGL initializes (1.5)
4. ✅ APK loaded via DexClassLoader
5. ✅ Activity class resolved and instantiated
6. ✅ `onCreate()` completes (GLSurfaceView created and setContentView'd)
7. ✅ `onResume()` completes
8. ✅ Surface lifecycle simulated (960×540)
9. ✅ GLThread starts and becomes RUNNABLE

**Current blocker:** `gl3stubInit()` in `libgles3jni.so` crashes with SIGSEGV (SEGV_ACCERR). The function tries to write GL3 function pointers into a GOT entry that the dynamic linker resolved to the host system's read-only `libGLESv2.so.2` text segment instead of `libgles3jni.so`'s writable data section. This is a symbol collision between the host GL library and the APK's native code — the `glReadBuffer` etc. symbols get interposed by the system library.

**Backtrace:**
```
gl3stubInit+68 → Java_com_android_gles3jni_GLES3JNILib_init+268
→ GLES3JNIView$Renderer.onSurfaceCreated
→ GLSurfaceView$GLThread.guardedRun
→ GLSurfaceView$GLThread.run
```

### Phase 2: Other APKs

| APK | Status | Error |
|---|---|---|
| `2048.apk` | ❌ ClassNotFound | `android.view.Menu` missing |
| `antimine.apk` | ❌ VerifyError | `AppCompatImageView` not instance of `android.view.View` — needs AppCompat shims |
| `simple-calculator.apk` | ❌ No launchable activity | Manifest parsing issue |

---

## Nova Overrides in `nova-shims/` (Complete List)

### Core OS

| File | Purpose |
|---|---|
| `android/os/ServiceManager.java` | In-process service registry (ConcurrentHashMap) |
| `android/os/Binder.java` | Binder with in-process transact + 4 native method stubs |
| `android/os/IBinder.java` | IBinder interface |
| `android/os/Bundle.java` | HashMap-backed Bundle (no Parcelling machinery) |
| `android/os/Parcel.java` | ArrayList-backed Parcel (in-process only) |
| `android/os/Handler.java` | Handler with in-process MessageQueue dispatch |
| `android/os/Looper.java` | Looper using Java threads |
| `android/os/Message.java` | Message with object pool |
| `android/os/MessageQueue.java` | LinkedList-backed queue |
| `android/os/SystemClock.java` | All methods are `native` (JNI stubs provide implementations via `clock_gettime`) |
| `android/os/Trace.java` | No-op trace stubs |
| `android/os/Process.java` | All methods are `native` for PID/UID; `THREAD_PRIORITY_*` constants |
| `android/os/SystemProperties.java` | 7 native methods for get/set system properties |
| `android/os/Parcelable.java` | Parcelable interface with write flags |

### Content

| File | Purpose |
|---|---|
| `android/content/Context.java` | Abstract Context base with all system service constants + `novaSetCurrentPackageName()` |
| `android/content/ContextWrapper.java` | Delegates all Context methods to mBase |
| `android/content/Intent.java` | HashMap-backed extras |
| `android/content/ComponentName.java` | Simple package+class holder |
| `android/content/pm/ApplicationInfo.java` | Data class (no Parcelling) |
| `android/content/pm/ActivityInfo.java` | Data class (no Parcelling) |
| `android/content/pm/PackageInfo.java` | Package metadata data class |
| `android/content/pm/PackageItemInfo.java` | Base class for package item info |
| `android/content/pm/ComponentInfo.java` | Base class extending PackageItemInfo |
| `android/content/pm/PackageManager.java` | Delegates to NovaPackageManager |
| `android/content/pm/NovaPackageManager.java` | Singleton PM tracking current package |

### App

| File | Purpose |
|---|---|
| `android/app/Application.java` | Minimal Application extending ContextWrapper |
| `android/app/Activity.java` | Activity skeleton extending ContextWrapper |
| `android/app/FragmentManager.java` | Abstract with NovaFragmentManager inner impl |
| `android/app/Fragment.java` | Full lifecycle callbacks |
| `android/app/FragmentTransaction.java` | Abstract with NovaFragmentTransaction inner impl |

### View

| File | Purpose |
|---|---|
| `android/view/Surface.java` | Renders via CanvasRender.submitFrame(); Parcelable |
| `android/view/Display.java` | Fixed 1080×1920 display |
| `android/view/View.java` | View stub with all standard properties |
| `android/view/ViewTreeObserver.java` | Listener registration (all no-ops) |
| `android/view/ViewParent.java` | Full interface with nested scroll support |
| `android/view/ViewManager.java` | Interface: add/update/remove view |
| `android/view/ViewGroup.java` | ViewGroup with LayoutParams |
| `android/view/Window.java` | Concrete Window class |
| `android/view/WindowManager.java` | Interface + LayoutParams with all flags |
| `android/view/Gravity.java` | All gravity constants |
| `android/view/ViewPropertyAnimator.java` | Animator API delegating to View setters |
| `android/view/SurfaceHolder.java` | Interface with Callback/Callback2 |
| `android/view/KeyEvent.java` | With DispatcherState inner class |
| `android/view/MotionEvent.java` | With native classify method |
| `android/view/WindowInsets.java` | WindowInsets stub |
| `android/view/DisplayInfo.java` | Data class with metrics helpers |
| `android/view/WindowRelayoutResult.java` | Simple Parcelable |
| `android/view/SurfaceView.java` | SimpleSurfaceHolder implements all SurfaceHolder methods |

### Graphics

| File | Purpose |
|---|---|
| `android/graphics/Canvas.java` | Nova canvas with native draw methods |
| `android/graphics/Bitmap.java` | Bitmap with native create/recycle/getPixels |
| `android/graphics/BitmapFactory.java` | Native decode bytes |
| `android/graphics/Paint.java` | Native paint with style/color/stroke |
| `android/graphics/Rect.java` | Full Rect implementation with Parcelable |
| `android/graphics/RectF.java` | Float Rect |
| `android/graphics/Point.java` | Point with Parcelable |
| `android/graphics/Matrix.java` | 3×3 matrix with basic operations |
| `android/graphics/PixelFormat.java` | Format constants |
| `android/graphics/SurfaceTexture.java` | With novaBitmap/novaCanvas Nova extensions |

### Animation

| File | Purpose |
|---|---|
| `android/view/animation/Animation.java` | Abstract animation base |
| `android/view/animation/Interpolator.java` | Interpolation interface |
| `android/view/animation/Transformation.java` | Alpha + matrix transformation |

### OpenGL / EGL

| File | Purpose |
|---|---|
| `android/opengl/GLES20.java` | 100+ native GL2/3 methods |
| `android/opengl/GLUtils.java` | Native texImage/texSubImage |
| `android/opengl/GLSurfaceView.java` | Full GLSurfaceView with EGL/Thread management |
| `com/google/android/gles_jni/GLImpl.java` | GL10+GL11 stub with `_nativeClassInit` |
| `com/google/android/gles_jni/EGLImpl.java` | (aosp/) Full EGL implementation |

### Internal

| File | Purpose |
|---|---|
| `com/android/internal/graphics/NativeUtils.java` | Single native method stub |
| `nova/internal/Launcher.java` | APK launch orchestrator |
| `nova/internal/ViewDispatcher.java` | Input event dispatch to root view |

### Annotation Stubs in `nova-shims/android/annotation/`

All AOSP-internal annotations are stubbed as no-ops:
`@NonNull`, `@Nullable`, `@IntDef`, `@FlaggedApi`, `@SystemApi`, `@TestApi`, `@CallSuper`, `@CallbackExecutor`, `@UiContext`, `@SuppressLint`, `@RequiresPermission`, `@IntRange`

### AOSP `javax/microedition` Classes

| File | Purpose |
|---|---|
| `javax/microedition/khronos/egl/EGL.java` | Marker interface (was missing, blocked EGLImpl) |
| `javax/microedition/khronos/egl/EGL10.java` | (aosp/) EGL 1.0 interface |
| `javax/microedition/khronos/egl/EGL11.java` | (aosp/) EGL 1.1 interface |
| `javax/microedition/khronos/opengles/GL*.java` | (aosp/) GL interface hierarchy |

---

## Next Steps

### Fix gles3jni native crash (GL3 symbol collision)

The `gl3stubInit()` crash is caused by the dynamic linker resolving GL function pointer
variables (like `glReadBuffer`) in `libgles3jni.so`'s GOT to the host system's `libGLESv2.so.2`
instead of the APK's own writable data section. Possible fixes:

1. **`RTLD_DEEPBIND`** — Load `libgles3jni.so` with `RTLD_DEEPBIND` so it prefers its own symbols
2. **`mprotect`** — Make the RELRO page writable before `gl3stubInit` runs
3. **Rebuild `libgles3jni.so`** — Compile with `-Wl,-z,norelro` or hide the GL symbols

### Add missing framework stubs for other APKs

- `android.view.Menu` / `android.view.MenuItem` (needed by 2048)
- `android.view.LayoutInflater` (needed by XML layout inflation)
- AppCompat / AndroidX shims (needed by antimine and most modern APKs)

---

## Key Source Locations

| Path | Description |
|---|---|
| `scripts/build-framework.sh` | Main build script (compiles sources, packages jar, runs D8) |
| `scripts/gen-shims.py` | Scans `apks/` dir to generate missing stubs (run rarely) |
| `scripts/smoke-run-gles3jni.sh` | Smoke test for gles3jni APK |
| `src/java/aosp/` | Staged AOSP source files |
| `src/java/nova-shims/` | Nova override implementations |
| `src/generated/aidl/nova/` | Generated AIDL Java stubs |
| `src/jni/` | C JNI stub implementations |
| `src/egl.c` | EGL initialization (Wayland-backed) |
| `src/wayland.c` | Wayland display/window management |
| `src/art.c` | ART runtime init + JNI registration |
| `src/main.c` | NovaART entry point |
| `out/framework/` | Build output (jar + dex jar) |
| `docs/plan-v3.md` | Full architectural plan |

---

## Session History (fixes applied)

### Session: 2026-05-18 — Phase 2 JNI Bootstrap

**Problem:** Framework compiled but APK execution crashed at JNI registration.

**Root cause:** The C JNI stubs in `src/jni/` register native methods against Java classes via `FindClass()` + `RegisterNatives()`. The Java stubs needed matching `native` method declarations — many were missing or had Java implementations instead of native declarations.

**Fixes applied (in order):**

1. **`SystemClock.java`** — All 7 methods (`now`, `uptimeMillis`, `elapsedRealtime`, `elapsedRealtimeNanos`, `currentThreadTimeMillis`, `currentThreadTimeMicro`, `currentTimeMicro`) changed from Java implementations to `native` declarations. The JNI C stubs provide the actual implementations.

2. **`Binder.java`** — Added 4 missing native methods: `blockUntilThreadAvailable()`, `getNativeBBinderHolder()`, `getNativeFinalizer()`, `destroy(long)`.

3. **`Process.java`** — Created new file. 6 native methods (`setArgV0`, `setProcessGroup`, `setThreadPriority`, `myPid`, `myUid`, `getUidForName`) plus constants (`THREAD_PRIORITY_*`, `SYSTEM_UID`, etc.).

4. **`NativeUtils.java`** — Created `com.android.internal.graphics.NativeUtils` with single native method `native_configureHdrScreenSdcard()`.

5. **`EGL.java`** — Created `javax.microedition.khronos.egl.EGL` marker interface. `EGL10 extends EGL` was failing because the base interface was missing. This blocked `EGLImpl` class loading.

6. **`GLImpl.java`** — Added `private static native void _nativeClassInit()` required by the GLImpl JNI stub.

7. **`PackageInfo.java`** — Created `android.content.pm.PackageInfo` data class. Required by `NovaPackageManager.setCurrentPackage()`.

8. **`ViewTreeObserver.java`** — Created with listener interfaces (`OnGlobalLayoutListener`, `OnPreDrawListener`, etc.) and no-op registration methods. Required by `View` constructor.

9. **`ViewParent.java`** — Created interface with all methods (nested scrolling, focus, accessibility). Required by `View.getParent()` return type.

10. **`ViewManager.java`** — Created interface (`addView`, `updateViewLayout`, `removeView`). Required by `WindowManager extends ViewManager`.

11. **`Animation.java` / `Interpolator.java` / `Transformation.java`** — Created animation framework stubs needed by View system.

12. **`Matrix.java`** — Created `android.graphics.Matrix` with 3×3 float array backing and basic operations. Required by `Transformation.getMatrix()`.
