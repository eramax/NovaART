# NovaART v2 Implementation Plan

## Architecture

NovaART runs real AOSP framework Java code (android.* classes) on ART, with
**local Binder-shaped service stubs** replacing system_server, and Android
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

**Key insight (from architecture review):** Don't patch hundreds of Binder
call sites. Instead, implement local AIDL service stubs that the AOSP
framework code calls naturally via IBinder/IInterface. AOSP's `Binder`
base class already supports in-process `transact()` dispatch — no kernel
IPC needed. The service stubs register via `ServiceManager.addService()`
at startup.

---

## Stage 0: Prerequisites

### 0.1 Build ART + bootclasspath from AOSP

Important: this must be done from a clean `master-art`-aligned checkout state.
Do not sync `frameworks/base` into the active ART build tree before this step.
If `frameworks/base/Android.bp` is present, AOSP flips unbundled ART builds
from prebuilt SDK mode to source SDK mode (`UNBUNDLED_BUILD_SDKS_FROM_SOURCE=true`),
which expands the dependency graph dramatically and defeats the thin-manifest
assumption.

For the exact command sequence used to reproduce the current `master-art`
sync/build flow, see `scripts/repro-sync-art.sh`.

```sh
cd deps/aosp-full
source build/envsetup.sh
banchan com.android.art x86_64

# First try the narrow build without `dist`. NovaART Stage 0 needs host-side
# ART outputs, not the distributed packaging artifacts.
m com.android.art -j$(nproc)

# If the APEX packaging itself is needed later, try:
# m apps_only -j$(nproc)
# Avoid `dist` unless the tree is known-clean for metadata/licensing targets.
```

Produces:
- `out/soong/host/linux-x86_64/com.android.art/lib64/libart.so`
- `out/soong/host/linux-x86_64/com.android.art/bin/dex2oat`
- `out/soong/host/linux-x86_64/com.android.art/javalib/core-oj-hostdex.jar`
- `out/soong/host/linux-x86_64/com.android.art/javalib/core-libart-hostdex.jar`
- `out/soong/host/linux-x86_64/com.android.art/javalib/okhttp-hostdex.jar`
- `out/soong/host/linux-x86_64/com.android.art/javalib/bouncycastle-hostdex.jar`
- `out/soong/host/linux-x86_64/com.android.art/javalib/apache-xml-hostdex.jar`
- `out/soong/host/linux-x86_64/com.android.art/framework/boot.art`
- `out/soong/host/linux-x86_64/com.android.art/framework/boot.oat`
- `out/soong/host/linux-x86_64/com.android.art/framework/core.art`
- `out/soong/host/linux-x86_64/com.android.art/framework/core.oat`

For NovaART's host/glibc runtime, also run:

```sh
cd /mnt/mydata/projects2/qos/deps/NovaART
bash scripts/build-host-art-runtime.sh
```

This produces the host artifacts NovaART actually stages and runs against:
- `deps/aosp-full/out/host/linux-x86/lib64/libart.so`
- `deps/aosp-full/out/host/linux-x86/bin/dex2oat64`
- `deps/aosp-full/out/host/linux-x86/bin/dalvikvm64`

### 0.2 Create framework-source worktree

A second AOSP working tree should be used for framework source acquisition.
Keep `deps/aosp-full` reserved for the known-good ART build state on
`master-art`.

Use a second repo client for framework sources. The client should be created
with repo's experimental `--worktree` mode:

- `deps/aosp-full` → ART build tree (`master-art`)
- `deps/aosp-framework-src` → framework source tree

For the exact command sequence, see `scripts/repro-framework-source-worktree.sh`.

```sh
cd deps/aosp-full
mkdir -p ../aosp-framework-src
cd ../aosp-framework-src

repo init --worktree --partial-clone --no-use-superproject \
  -b android-latest-release \
  -u https://android.googlesource.com/platform/manifest

repo sync -c -j$(nproc) \
  frameworks/base external/skia external/freetype \
  external/harfbuzz_ng external/libpng external/icu
```

~400MB for frameworks/base alone. Contains the real `android.*` Java source
under `core/java/android/`. Licensed Apache 2.0.

Recommended workflow:
- Stage 0.1: build ART from a clean `master-art` checkout state first
- Stage 0.2: populate `deps/aosp-framework-src` for framework source extraction
- Use `deps/aosp-framework-src` as source input only
- Do not run the ART Stage 0 build in `deps/aosp-framework-src`
- Do not reintroduce `frameworks/base` into `deps/aosp-full`
- Stage 0.3: stage the minimal Phase 1 source batch into NovaART with
  `scripts/stage-phase1-framework-sources.sh`

To sync everything at once: `repo sync -j$(nproc)`

### 0.3 Check NovaART project is clean

```sh
deps/NovaART/
├── src/          # Wayland, EGL, ART bootstrap, event loop
├── src/jni/      # 11 stub modules, registration framework
├── apks/         # Test APKs (gles3jni, 2048, gd, retrobreaker, cm.aptoide, com.apkmirror)
├── build-host.sh # Meson build with auto toolchain detection
└── output/       # Built binary + runtime
```

### 0.4 Smoke-test milestone

Current reproducible smoke check:

```sh
cd /mnt/mydata/projects2/qos/deps/NovaART
bash scripts/smoke-run-gles3jni.sh
```

Current success criterion:
- ART initializes successfully
- JNI registration completes without `ClassNotFoundException` or `NoSuchMethodError`
- the `gles3jni` process stays alive under a bounded timeout

This is the current end-of-phase marker for the bootstrap/runtime bring-up
slice. It proves NovaART is past early ART/classpath/JNI registration failure
and into long-lived app/runtime execution.

Operational helpers:
- use [Makefile](/mnt/mydata/projects2/qos/deps/NovaART/Makefile) as the stable
  operator surface for build/stage/smoke commands
- append completed, verified milestones to
  [progress.md](/mnt/mydata/projects2/qos/deps/NovaART/progress.md)

---

## Phase 1 Detailed Bill of Materials (gles3jni)

### 1. AOSP framework classes to include

**Keep unmodified from AOSP** (~35 files):

OpenGL/EGL Java wrappers (from `frameworks/base/opengl/java/`):
- `android/opengl/GLSurfaceView.java`
- `com/google/android/gles_jni/EGLImpl.java`, `EGLContextImpl.java`, `EGLDisplayImpl.java`,
  `EGLSurfaceImpl.java`, `EGLConfigImpl.java`, `GLImpl.java`
- `javax/microedition/khronos/egl/EGL10.java`, `EGL11.java`, `EGLConfig.java`,
  `EGLContext.java`, `EGLDisplay.java`, `EGLSurface.java`
- `javax/microedition/khronos/opengles/GL.java`, `GL10.java`, `GL10Ext.java`,
  `GL11.java`, `GL11Ext.java`, `GL11ExtensionPack.java`
- `android/opengl/GLDebugHelper.java`, `EGLLogWrapper.java`, `GLException.java`

Core OS/util (from `frameworks/base/core/java/`):
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

**Fork/adapt for NovaART** (~15 "choke point" files):

Activity/bootstrap:
- `android/app/Activity.java`, `ActivityThread.java`, `Instrumentation.java`,
  `LoadedApk.java`, `ContextImpl.java`

Window/view stack:
- `android/view/Window.java`, `WindowManagerImpl.java`, `WindowManagerGlobal.java`,
  `ViewRootImpl.java`, `SurfaceView.java`, `View.java`, `ViewGroup.java`
- `com/android/internal/policy/PhoneWindow.java`

Display:
- `android/view/Choreographer.java` (can skip — synchronous)
- `android/hardware/display/DisplayManagerGlobal.java`
- `android/view/SurfaceControl.java` (forked to avoid BLAST buffer queue — calls window.c directly)

Resources (thin subset):
- `android/content/res/Resources.java`, `AssetManager.java`, `Configuration.java`
- `android/util/DisplayMetrics.java`

### 2. Local AIDL service stubs needed

| AIDL Service | AIDL Source | Minimum Stub Methods |
|---|---|---|
| `IWindowManager` | `core/java/android/view/IWindowManager.aidl` | `openSession()` → local IWindowSession |
| `IWindowSession` | `core/java/android/view/IWindowSession.aidl` | **Hardest stub.** `addToDisplay()` → create Wayland surface, return ADD_OKAY + valid SurfaceControl. `relayout()` → must return valid SurfaceControl + InsetsState or ViewRootImpl crashes. `remove()` → destroy. `finishDrawing()` → no-op |
| `IDisplayManager` | `hardware/display/IDisplayManager.aidl` | `getDisplayInfo()` → 1920x1080@60Hz. `registerCallback()` → no-op |
| `IPackageManager` | `content/pm/IPackageManager.aidl` | `getApplicationInfo()`, `getActivityInfo()`, `checkPermission()` → GRANTED |
| `IActivityTaskManager` | `app/IActivityTaskManager.aidl` | Optional — skip if Nova directly creates Activity |

Each stub extends the AIDL `Stub` class. AOSP's `Binder.execTransact()`
handles in-process dispatch — no kernel module or Unix sockets needed.

### 3. JNI native methods to implement

**MessageQueue** (critical for Looper):
- `nativeInit()`, `nativeDestroy()`, `nativePollOnce()`, `nativeWake()`
  — integrate with Wayland epoll loop

**Surface** (critical for display):
- `nativeCreate()`, `nativeRelease()`, `nativeIsValid()`

**EGL10 JNI** (critical — gles3jni uses EGL10, not EGL14):
- `EGLImpl.eglGetDisplay`, `eglInitialize`, `eglChooseConfig`, `eglCreateContext`,
  `eglCreateWindowSurface`, `eglMakeCurrent`, `eglSwapBuffers`,
  `eglDestroyContext`, `eglDestroySurface`, `eglQueryString`,
  `eglQuerySurface`, `eglGetError`, `eglTerminate`

**Not needed for Phase 1 (forked out):**
- `SurfaceControl` — forked out of `ViewRootImpl` and `SurfaceView`; replaced with direct `window.c` Wayland surface calls
- `DisplayEventReceiver` — use synchronous rendering
- Canvas JNI — Phase 2

### 4. NovaART own code

**Nova-owned Java** (~15 files):
- `NovaActivityThread.java` — app bootstrap, create Activity, dispatch lifecycle
- `NovaInstrumentation.java` — direct lifecycle calls
- `NovaLoadedApk.java` — APK metadata, classloader, native lib path
- `NovaContextImpl.java` — `getSystemService()`, package/resources hooks
- `NovaWindowManagerGlobal.java`, `NovaWindowManagerImpl.java`
- `NovaViewRootImpl.java` — initial attach, measure/layout, window session
- `NovaSurfaceView.java` — simplified fork, no SurfaceControl/BLAST/HWUI
- `NovaPhoneWindow.java`, `NovaDecorView.java` — minimal policy window
- `NovaServiceManager.java`, `NovaWindowManagerService.java`,
  `NovaWindowSession.java`, `NovaDisplayManagerService.java`,
  `NovaPackageManagerService.java`
- `NovaApkLoader.java` — parse manifest, extract native libs

**Nova-owned C** (~8 files):
- `art.c` — JNI_CreateJavaVM + bootclasspath init (exists)
- `wayland.c` — display connect, registry, compositor, xdg-toplevel (stub exists)
- `egl.c` — wl_egl_window, EGLDisplay/EGLContext/EGLSurface (stub exists)
- `window.c` — Nova window objects backing IWindowSession (new)
- `surface.c` — backing for android.view.Surface native methods (new)
- `message_queue.c` — native backend for MessageQueue (new)
- `egl_jni.c` — adapted AOSP `com_google_android_gles_jni_EGLImpl.cpp` (new)
- `jni_registration.c` — register all Nova/AOSP-adapted JNI (new)

### 5. Build pipeline

```
Step 1: Build ART + bootclasspath
  cd deps/aosp-full && source build/envsetup.sh && banchan com.android.art x86_64
  m apps_only dist -j$(nproc)
  → libart.so, dex2oat, core-oj/core-libart/okhttp/bouncycastle jars,
    boot.oat/core.art framework images

Step 2: Compile Nova framework overlay
  a) Generate Java from AIDL files (IWindowManager, IWindowSession, IDisplayManager, IPackageManager)
  b) javac the minimal Phase 1 overlay surface:
     - staged OpenGL/EGL Java wrappers
     - generated slim Binder stubs
     - Nova-owned hidden-API shims under src/java/nova-shims/
     Keep large staged framework files (android.os, pm, view internals) as source
     provenance and future fork material, not mandatory compile input.
  c) d8 → nova-framework-dex.jar
  d) dex2oat → nova-framework.oat (optional)

Step 3: Build NovaART native code
  meson setup build/host && ninja -C build/host
  → output/bin/novaart

Step 4: Stage and run
  mkdir -p output/android-data/com.android.art/
  cp -r aosp-out/.../framework/ output/android-data/com.android.art/
  cp aosp-out/.../lib64/libart.so output/lib/
  cp nova-framework.oat output/framework/
  LD_LIBRARY_PATH=./output/lib \
    ANDROID_ROOT=./output/android-data \
    ./output/bin/novaart apks/gles3jni.apk
```

---

## Phase 1 Gate (gles3jni renders)

**Checkpoint criteria:**
1. ART creates JavaVM successfully with bootclasspath
2. Wayland window opens, EGL context created
3. gles3jni APK loads, Activity starts
4. GLES calls forwarded to host GPU via wl_egl
5. Rotating triangle visible on screen

**By when:** Steps A-G = ~2 weeks

---

## Phase 2 Gate (Canvas apps render)

**Checkpoint criteria:**
1. 2048 / GD / RetroBreaker load and display
2. Canvas drawing works via Skia → wl_shm
3. Touch/click input delivered to app
4. Looper + Choreographer provide smooth frame timing

**By when:** Step H after Phase 1 = ~+2 weeks

---

## Phase 3 Gate (complex apps — stretch goal)

**Checkpoint criteria:**
1. Facebook/Twitter/etc. launch without crash
2. Binder stubs satisfy basic service calls
3. MicroG stubs satisfy GMS checks
4. Network, notifications, location stubs work enough for app UX

**Note:** "Launch without crash" for complex apps is extremely ambitious.
These apps use ContentProvider, JobScheduler, AccountManager, Firebase,
Play Integrity, and dozens of GMS SDK calls. Real support is months, not
weeks. Treat this as a stretch goal; gate on Phase 2 Canvas apps first.

### MicroG / GMS strategy (Phase 3)

The in-process Binder architecture naturally enables MicroG — its AIDL
service implementations register via the same `ServiceManager` our stubs use.
Implementation priority:

**1. Signature spoofing (lowest cost, highest impact)**
`NovaPackageManagerService.checkSignatures()` must return `SIGNATURE_MATCH`
for `com.google.android.gms`. This is a ~10-line addition that unblocks all
apps that only check GMS presence at install/launch.

**2. Inline GMS stubs (`NovaGmsStub.java`)**
Lightweight Java class that satisfies the 5-10 most common GMS API calls
without running MicroG's APK:
- `isGooglePlayServicesAvailable()` → `SUCCESS`
- `getLastLocation()` → null
- `getFCMToken()` → fake token string
- `getAccounts()` → empty array (or from AccountManager)

**3. Real MicroG co-loading (only if needed)**
Only pursue if apps need FCM push or Google Sign-In. Requires:
- Multi-APK loading in `NovaApkLoader.java` (load GmsCore.apk before target APK)
- `NovaAccountManagerService.java` stub implementing `IAccountManager` AIDL
- MicroG's AIDL services auto-register via existing in-process Binder plumbing

**By when:** Step I after Phase 2 = stretch goal (~+months)

---

## Implementation Order

```
Step A:  ART build (banchan com.android.art x86_64) → libart.so + boot.oat
Step B:  Wayland bridge (wayland.c, egl.c) → window + GLES surface
Step C:  ART bootstrap (art.c + JNI_CreateJavaVM) → JavaVM running
Step D:  MessageQueue JNI (nativePollOnce/Wake + epoll) → Looper works
Step E:  AIDL stub codegen + service stubs (IWindowSession, IPackageManager, etc.)
Step F:  Framework compile (AOSP sources + Nova forks + AIDL stubs → nova-framework.jar)
         Current reproducible script: scripts/build-framework.sh
Step G:  Surface JNI + EGL10 JNI → gles3jni rendering → PHASE 1 GATE
Step H:  Skia build + Canvas JNI + wl_shm pool → PHASE 2 GATE
Step I:  Service stub expansion + MicroG stubs → PHASE 3 GATE
```

## Key Files

| File | Purpose |
|---|---|
| `deps/aosp-full/` | AOSP master-art checkout (art, libcore, frameworks/base, external/skia) |
| `deps/NovaART/src/art.c` | JNI_CreateJavaVM bootstrap, bootclasspath setup (exists, fixed) |
| `deps/NovaART/src/wayland.c` | Wayland display, registry, xdg-toplevel, wl_seat input (stub exists) |
| `deps/NovaART/src/egl.c` | wl_egl_window, EGLDisplay/Context/Surface, swap (stub exists) |
| `deps/NovaART/src/message_queue.c` | Native backend for MessageQueue (nativePollOnce/Wake + epoll) |
| `deps/NovaART/src/surface.c` | Native backend for android.view.Surface |
| `deps/NovaART/src/egl_jni.c` | JNI for AOSP EGL10 wrappers (adapted from com_google_android_gles_jni) |
| `deps/NovaART/src/window.c` | Nova window objects backing IWindowSession |
| `deps/NovaART/src/jni_registration.c` | Register all Nova/AOSP-adapted JNI methods |
| `deps/NovaART/src/java/nova/` | Nova-owned Java: ActivityThread, WindowManager*, service stubs, ApkLoader |
| `deps/NovaART/src/java/nova-shims/` | Minimal hidden-API/parcelable shims required to compile the Phase 1 overlay cleanly |
| `deps/NovaART/src/java/aosp-forks/` | Forked AOSP choke points: Activity, ViewRootImpl, SurfaceView, PhoneWindow, etc. |
| `deps/NovaART/scripts/build-framework.sh` | javac + d8 pipeline for the minimal Phase 1 overlay → nova-framework-classes.jar + nova-framework-dex.jar |
| `deps/NovaART/scripts/stage-art.sh` | Copy ART artifacts from AOSP banchan build to output/ |
| `deps/NovaART/apks/` | Test APKs: gles3jni, 2048, gd, retrobreaker, aptoide

## Risks and Mitigations

| Risk | Likelihood | Mitigation |
|---|---|---|
| frameworks/base/ has circular deps | **High** | Use AOSP `make framework` as fallback to produce framework.jar, then extract; manual `javac` ordering only after dependency graph is mapped |
| Minimal overlay drifts back toward full staged-source compile | High | Keep `scripts/build-framework.sh` source list explicit and add shims for hidden types instead of widening the source set by default |
| SurfaceControl/BLAST expected by ViewRootImpl | **High** | Fork `SurfaceView` + `ViewRootImpl` to bypass SurfaceControl/BLAST entirely; call `window.c` directly for Wayland surface creation |
| ART can't load nova-framework.jar | Low | Bootclasspath loading is ART's core function |
| Binder stubs don't cover all call paths | Medium | Monitor logs, add stub methods as crashes appear. `transact()` path is well-defined AIDL boundary |
| Performance of Java reflection Activity launch | Low | Acceptable for MVP, optimize later |
| frameworks/base/ API mismatch with ART version | Medium | Use same `main` branch for both |
| Skia not available (not in master-art) | Medium | Use system Skia package or checkout separately |
| GLES passthrough API differences | Low | GLES20 is stable across desktop/mobile GPUs |
| Phase 3 "Facebook/Twitter launch" timeline | **Overly optimistic** | Treat Phase 3 as stretch goal; real complex-app support is months, not weeks. Gate on "first Canvas app renders" instead |

### SurfaceControl/BLAST avoidance strategy

`ViewRootImpl` in modern AOSP creates a `SurfaceControl` and uses BLAST buffer
queue for frame submission. We bypass this entirely by forking `ViewRootImpl`
to call our `window.c` Wayland surface directly for frame presentation.
`SurfaceView` is similarly forked to avoid `SurfaceControl.createDisplay()`.

### Looper test gate (after Step D, before Step E)

Before proceeding to framework compile, verify: create JVM, instantiate a
`Looper`, post a `Message`, confirm dispatch via `nativePollOnce()`/`nativeWake()`
integration with Wayland epoll loop. This 30-minute test prevents days of
debugging inside the full framework where a hang has no visible output.

### `make framework` fallback

If manual `javac` compilation hits circular dependency walls:
```sh
cd deps/aosp-full && source build/envsetup.sh && lunch aosp_x86_64-eng
m framework  # builds full framework.jar with Soong
```
Extract `out/target/product/generic_x86_64/system/framework/framework.jar`,
then progressively replace packages with Nova fork versions.
