# NovaART Progress

## Architecture Decision (agreed upon)

### Strategy: Real AOSP Sources + Targeted Nova Overrides

Instead of generating thousands of stub classes from APK DEX analysis, the architecture uses:

1. **`src/java/aosp/`** — Real AOSP source files for complex framework classes (Handler, Binder, Bundle, etc.)
2. **`src/java/nova-shims/`** — ~20-30 hand-written overrides for classes that need Nova-specific behavior
3. **`src/generated/aidl/nova/`** — Generated AIDL stubs for IPC interface definitions
4. **`deps/aosp-full/prebuilts/sdk/35/module-lib/android.jar`** — Compile-time type reference only (not included in output)

### Binder Architecture

- **AOSP `Binder.java`** is used as-is — its `transact()` already has an in-process fast path (calls `onTransact()` directly without `/dev/binder` when target is in same JVM)
- **Native JNI methods** in Binder are no-ops (return 0/false/null)
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

`scripts/build-framework.sh --skip-aidl` compiles successfully as of the latest commit.

Output: `out/framework/nova-framework-classes.jar` and `nova-framework-dex.jar`

### Nova Overrides in `nova-shims/` (Key Files)

| File | Purpose |
|---|---|
| `android/os/ServiceManager.java` | In-process service registry (ConcurrentHashMap) |
| `android/os/Binder.java` | Binder with in-process transact, no native JNI |
| `android/os/IBinder.java` | IBinder interface |
| `android/os/Bundle.java` | HashMap-backed Bundle (no Parcelling machinery) |
| `android/os/Parcel.java` | ArrayList-backed Parcel (in-process only) |
| `android/os/Handler.java` | Handler with in-process MessageQueue dispatch |
| `android/os/Looper.java` | Looper using Java threads |
| `android/os/Message.java` | Message with object pool |
| `android/os/MessageQueue.java` | LinkedList-backed queue |
| `android/os/SystemClock.java` | System.nanoTime()-based clock |
| `android/os/Trace.java` | No-op trace stubs |
| `android/content/Context.java` | Abstract Context base with all system service constants |
| `android/content/ContextWrapper.java` | Delegates all Context methods to mBase |
| `android/content/Intent.java` | HashMap-backed extras |
| `android/content/ComponentName.java` | Simple package+class holder |
| `android/content/pm/ApplicationInfo.java` | Data class (no Parcelling) |
| `android/content/pm/ActivityInfo.java` | Data class (no Parcelling) |
| `android/app/Application.java` | Minimal Application extending ContextWrapper |
| `android/app/Activity.java` | Activity skeleton extending ContextWrapper |
| `android/view/Surface.java` | Renders via CanvasRender.submitFrame() |
| `android/view/Display.java` | Fixed 1080×1920 display |
| `android/view/View.java` | View stub with all standard properties |
| `android/view/Window.java` | Window with LayoutParams |
| `android/view/WindowManager.java` | Interface + LayoutParams with all flags |
| `android/graphics/Canvas.java` | Nova canvas implementation |
| `android/graphics/Bitmap.java` | Bitmap stub |
| `android/graphics/Rect.java` | Full Rect implementation |
| `android/graphics/Point.java` | Point with Parcelable |
| `android/graphics/SurfaceTexture.java` | With novaBitmap/novaCanvas Nova extensions |
| `android/util/DisplayMetrics.java` | Fixed XHIGH density |
| `com/google/android/gles_jni/GLImpl.java` | GL10+GL11 stub implementation |

### Annotation Stubs in `nova-shims/android/annotation/`

All AOSP-internal annotations are stubbed as no-ops:
`@NonNull`, `@Nullable`, `@IntDef`, `@FlaggedApi`, `@SystemApi`, `@TestApi`, `@CallSuper`, `@CallbackExecutor`, `@UiContext`, `@SuppressLint`, `@RequiresPermission`, `@IntRange`

---

## Phase 2: APK Execution — TODO

Next steps after successful framework build:
1. Wire `NovaContext` implementation of all abstract Context methods
2. Register core services in ServiceManager at bootstrap
3. Load APK via DexClassLoader and instantiate Application + Activity
4. Hook TextureView/SurfaceView rendering to NovaCanvasRender
5. Dispatch input events from native layer to View hierarchy

### Known Working APKs (targeted):
- Material Life (game using TextureView for rendering)
- Others in `apks/` directory

---

## Key Source Locations

| Path | Description |
|---|---|
| `scripts/build-framework.sh` | Main build script (compiles sources, packages jar, runs D8) |
| `scripts/gen-shims.py` | Scans `apks/` dir to generate missing stubs (run rarely) |
| `src/java/aosp/` | Staged AOSP source files |
| `src/java/nova-shims/` | Nova override implementations |
| `src/generated/aidl/nova/` | Generated AIDL Java stubs |
| `out/framework/` | Build output (jar + dex jar) |
| `docs/plan-v3.md` | Full architectural plan |
