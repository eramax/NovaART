# NovaART Implementation Plan

> **Goal:** Build a Wayland-native Android app runtime (NovaART) that embeds ART, implements AOSP framework JNI stubs, and renders apps on Wayland surfaces without GTK4/XWayland.

**Strategy:** Build and test on Ubuntu host (glibc/Wayland) first, then port to QOS (Alpine/musl) after MVP is proven.

**Tech Stack:** C11, libwayland-client, libwayland-egl, libEGL, libGLESv2, Skia, ART (libart.so), dex2oat, AOSP frameworks/base (Java + JNI), meson/ninja build

**Reference:** ATL source at `deps/android_translation_layer/` for ART init pattern. AOSP source at `deps/aosp-full/` (master-art manifest, 177 projects).

---

### Task 1: Bootstrap ART Experiment on Host

**Goal:** Get ART running standalone on Ubuntu — create a JVM, call JNI methods, prove the runtime works.

**Files:**
- Create: `deps/NovaART/experiment/art_test.c` — minimal C program loading libart.so
- Create: `deps/NovaART/experiment/HelloWorld.java` — simple Java class
- Create: `deps/NovaART/experiment/Makefile` — build helpers

- [ ] **Step 1: Find libart.so from AOSP build (or Alpine package)**

Check AOSP build output at `deps/aosp-full/out/host/linux-x86_64/lib64/libart.so`.
Or install Alpine's `art_standalone` for initial testing:
```sh
# On Alpine: apk add art art-dev art-standalone
# On Ubuntu: use AOSP build output
```

- [ ] **Step 2: Write art_test.c**

```c
// Load libart.so via dlopen, call JNI_CreateJavaVM
// Same pattern as ATL's main.c:83-143
```

- [ ] **Step 3: Run experiment on host**

Expected: "JVM created successfully", "Hello from ART!"

- [ ] **Step 4: Test with Android framework classes (when available)**

**Deliverable:** ART boots on Ubuntu host, JNI works.

---

### Task 2: Build ART from AOSP Source

**Goal:** Build ART (libart.so + dex2oat) from AOSP `deps/aosp-full/` with JIT enabled, dex2oat working.

- [ ] **Step 1: Verify AOSP checkout complete**

Check `deps/aosp-full/` has needed modules:
```
art/ build/soong/ external/icu/ external/compiler-rt/
external/libunwind/ libnativehelper/ system/core/ prebuilts/go/
prebuilts/clang/host/linux-x86/  prebuilts/rust/
```

- [ ] **Step 2: Build libart.so + dex2oat**

```sh
cd deps/aosp-full
source build/envsetup.sh
lunch silvermont-eng
make libart dex2oat -j$(nproc)
```

- [ ] **Step 3: Verify symbols**

```sh
nm -D out/host/linux-x86_64/lib64/libart.so | grep JNI_CreateJavaVM
```

- [ ] **Step 4: Package ART artifacts**

```sh
mkdir -p deps/NovaART/output/lib deps/NovaART/output/bin
cp out/host/linux-x86_64/lib64/libart.so deps/NovaART/output/lib/
cp out/host/linux-x86_64/bin/dex2oat deps/NovaART/output/bin/
```

**Deliverable:** `libart.so` + `dex2oat` built from AOSP for native glibc host.

---

### Task 3: Compile AOSP Framework Java to OAT

**Goal:** Compile AOSP `frameworks/base/core/java/` to DEX → OAT so ART can load framework classes.

**Approach:** Use full `make framework` from AOSP rather than manual subset (avoids ClassNotFoundException rabbit holes).

- [ ] **Step 1: Build full framework via AOSP**

```sh
cd deps/aosp-full
make framework -j$(nproc)
# Produces out/target/common/obj/JAVA_LIBRARIES/framework_intermediates/classes.jar
```

- [ ] **Step 2: Convert JAR → DEX → OAT**

```sh
d8 --release --output output/dex/ classes.jar
dex2oat \
  --dex-file=output/dex/classes.dex \
  --oat-file=output/framework/boot.oat \
  --instruction-set=x86_64 \
  --compiler-filter=speed
```

- [ ] **Step 3: Verify OAT file**

```sh
oatdump --oat-file=output/framework/boot.oat
```

- [ ] **Step 4: Test loading OAT in ART**

**Deliverable:** Framework boot.oat loads without errors.

---

### Task 3b: Looper + Choreographer

**Goal:** Implement minimal `android.os.Looper` message pump integrated with Wayland dispatch, and `Choreographer` driven by `wl_callback` vsync.

**Why:** Every animated app (gles3jni, GD) requires these. Without them, apps spin-poll or stall.

**Files:**
- Create: `deps/NovaART/src/looper.c`
- Create: `deps/NovaART/src/android_os_Looper.java` (stub class)

- [ ] **Step 1: Implement Looper**

```c
// Main loop:
while (running) {
    // 1. Dispatch Wayland events (non-blocking)
    wl_display_dispatch_pending(state->display);
    wl_display_flush(state->display);
    // 2. Dispatch Looper messages
    // 3. Wait (poll with timeout) for next event
    poll(wayland_fd, ..., timeout_ms);
}
```

- [ ] **Step 2: Implement Choreographer → wl_callback**

```c
// wl_surface_frame() callback provides vsync timestamp
// Choreographer posts frame callbacks to Looper queue
// Apps call: Choreographer.getInstance().postFrameCallback(...)
```

- [ ] **Step 3: Integrate with main event loop**

Remove blocking `wl_display_dispatch()` — use `wl_display_prepare_read()` + `poll()` + `wl_display_read_events()` pattern.

- [ ] **Step 4: Create Choreographer JNI stubs**

Key methods: `getInstance()`, `postFrameCallback()`, `getFrameTime()`, `scheduleVsync()`

**Deliverable:** Main thread dispatches Wayland events + Looper messages. Choreographer frame callbacks fire at compositor refresh rate.

---

### Task 4: Wayland Window + Event Loop

**Goal:** Wayland client skeleton with xdg-toplevel window, EGL init, and event loop.

**Files:**
- `src/wayland.c` — registry, globals, xdg-toplevel
- `src/egl.c` — wl_egl surface + EGL context
- `src/main.c` — entry point
- `src/meson.build` — build definition

- [ ] **Step 1: Wayland registry — bind compositor, xdg-shell, seat, shm**

- [ ] **Step 2: xdg-toplevel creation + configure/close callbacks**

- [ ] **Step 3: EGL init on wl_egl surface**

- [ ] **Step 4: Non-blocking event loop (poll + dispatch)**

- [ ] **Step 5: Build and test**

```sh
cd deps/NovaART && ./build-host.sh
./output/bin/novaart
```

Expected: Empty Wayland window appears.

**Deliverable:** Window opens, EGL context created, event loop runs.

---

### Task 5: JNI Stub Registration Framework

**Goal:** Skeleton `libnovaart_jni.a` that registers all JNI stubs with no-crash trampolines.

**Files:**
- `src/jni/android_runtime.c` — gRegJNI[] central table
- `src/jni/core_jni_helpers.*` — registration helpers
- `scripts/generate_stubs.sh` — stub generator

- [ ] **Step 1: Write generate_stubs.sh**

Scan AOSP `AndroidRuntime.cpp` `gRegJNI[]` + `core/jni/*.cpp` + `libs/hwui/jni/*.cpp`. For each:
1. Extract function name and Java class
2. Parse JNI signatures for return type classification
3. Generate skeleton .c file with safe defaults

- [ ] **Step 2: Generate all 170+ registration stubs**

- [ ] **Step 3: Build libnovaart_jni.a** (link these stubs into the runtime)

- [ ] **Step 4: Test — load framework class with all stubs registered**

Expected: Framework class loads without `UnsatisfiedLinkError`.

**Deliverable:** All JNI native methods have no-crash trampolines. Framework classes load.

---

### Task 6: GLES Rendering via wl_egl

**Goal:** GLES JNI stubs render to wl_egl surface. gles3jni displays the rotating triangle.

- [ ] **Step 1: Implement EGL init in main.c**

Create EGL context before entering event loop.

- [ ] **Step 2: Write GLES20 JNI stubs**

`glClear`, `glClearColor`, `glDrawArrays`, `glCreateShader`, `glCompileShader`, `glAttachShader`, `glLinkProgram`, `glUseProgram`, etc.

These are direct passthroughs to desktop GL (same signatures).

- [ ] **Step 3: Write EGL JNI stubs**

`eglCreateContext`, `eglMakeCurrent`, `eglSwapBuffers`, `eglChooseConfig` → route through our EGL context.

- [ ] **Step 4: Build and test with gles3jni**

```sh
LD_LIBRARY_PATH=./output/lib ./output/bin/novaart gles3jni.apk
```

Expected: Wayland window with rotating triangle.

- [ ] **PHASE 1 GATE — gles3jni renders on host.**

**Deliverable:** GLES app renders via Wayland. Phase 1 complete.

---

### Task 7: Wayland Input → Android Events

**Goal:** Map `wl_seat` events (pointer, keyboard) to Android `MotionEvent`/`KeyEvent`.

- [ ] **Step 1: Register wl_seat listener** (pointer + keyboard)

- [ ] **Step 2: Pointer events → MotionEvent**

BTN_LEFT → ACTION_DOWN/UP. Motion → ACTION_MOVE.

- [ ] **Step 3: Keyboard events → KeyEvent**

Wayland keysym → Android keycode mapping.

- [ ] **Step 4: Deliver to Activity via JNI**

`activity.dispatchTouchEvent()` / `activity.dispatchKeyEvent()`

**Deliverable:** Click/tap on window delivers events to Android app.

---

### Task 8: Activity Lifecycle + APK Loading

**Goal:** Parse APK, dex2oat to OAT, create Activity, run lifecycle.

- [ ] **Step 1: Write APK loader** — extract AndroidManifest.xml from APK zip

- [ ] **Step 2: Parse binary AndroidManifest.xml** (AXML format)

- [ ] **Step 3: dex2oat the APK** (via posix_spawn, not system())

- [ ] **Step 4: Create Activity via JNI** — call `onCreate`, `onStart`, `onResume`

- [ ] **Step 5: Test with gles3jni** — full pipeline: APK → dex2oat → Activity → render

**Deliverable:** `./novaart app.apk` launches app through full pipeline.

---

### Task 9: Canvas Rendering via Skia + wl_shm

**Goal:** `android.graphics.Canvas` → Skia raster → wl_shm buffer → compositor.

- [ ] **Step 1: Build Skia standalone** (GN build from AOSP's external/skia)

- [ ] **Step 2: wl_shm buffer pool management**

- [ ] **Step 3: Canvas JNI stubs → Skia API calls**

- [ ] **Step 4: Paint/Bitmap/Path JNI stubs → Skia**

- [ ] **Step 5: Implement SurfaceView → wl_subsurface**

- [ ] **Step 6: Test with GD, 2048**

**Deliverable:** Canvas apps render via Skia + wl_shm.

---

### Task 10: MVP Integration

**Goal:** Single binary, full pipeline working on host.

- [ ] **Step 1: Write full build.sh** — orchestrate all build steps

- [ ] **Step 2: Full MVP test**

```sh
./build.sh
LD_LIBRARY_PATH=./output/lib ./output/bin/novaart gles3jni.apk
LD_LIBRARY_PATH=./output/lib ./output/bin/novaart gd.apk
```

- [ ] **Step 3: Measure metrics** — binary size, RAM, startup time, FPS

**Deliverable:** Single binary launches GLES and Canvas apps on host.

---

### Task 11: QOS Component Integration (Future)

After MVP on host, port to QOS/Alpine:

- [ ] Build ART in glibc container for musl target
- [ ] Compile runtime with musl-libc
- [ ] Create QOS component `components/novaart-runtime/`
- [ ] Add to desktop profile
- [ ] Build QOS ISO, test in QEMU

## Task Ordering and Dependencies

```
Task 1 (ART bootstrap) → Task 2 (build ART) → Task 3 (framework OAT)
                                                     ↓
Task 4 (Wayland window) ──────────────────────→ Task 3b (Looper/Choreo)
                                                     ↓
Task 5 (JNI stubs) ───────────────────────────→ Task 6 (GLES) ← Task 7 (input)
                                                     ↓
                                            [Phase 1 Gate]
                                                     ↓
Task 8 (APK + Activity) ─────────────────────→ Task 9 (Canvas)
                                                     ↓
                                              Task 10 (MVP)
```

## Timeline (Revised)

| Phase | Estimate |
|---|---|
| Phase 1: gles3jni MVP (Tasks 1-6 + 3b) | 3-5 weeks |
| Phase 2: Canvas support (Tasks 7-9) | 2-3 weeks |
| Phase 3: Broader apps (Task 10) | 3-5 weeks |
| Phase 4: Full API 28+ + QOS port (Task 11) | 4-8 weeks |

## Key Differences from Original Plan

1. **Host-first**: Ubuntu/glibc instead of Alpine/musl. No musl compat layer needed.
2. **No Task 0**: Eliminated (was musl/glibc compat). ART builds natively on glibc.
3. **Task 3b added**: Looper + Choreographer before GLES rendering (review recommendation).
4. **Full framework build**: Use AOSP `make framework`, not manual subset.
5. **JNI stub classification**: By return type, with sentinel structs for jlong handles.
6. **posix_spawn for dex2oat**: Instead of system().
7. **Phase 1 gate**: Explicit checkpoint after Task 6 marks "gles3jni works".
8. **QOS integration**: Deferred to Phase 4, after MVP proven on host.
