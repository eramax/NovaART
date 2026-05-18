# NovaART v3 Implementation Plan Review

## Executive Summary

After thorough review of the plan against the actual NovaART codebase and dependency structure, this is a **well-structured, detailed implementation plan** that largely aligns with the existing architecture. The codebase has substantial Phase 1 groundwork in place (ART bootstrap, Wayland bridge, EGL foundation, AIDL stubs, JNI registration framework).

**Overall Assessment: APPROVED with targeted recommendations based on actual codebase state.**

---

## Codebase Reality Check (Updated with Dependency Analysis)

### AOSP Dependencies - Actual State

**deps/aosp-full/** (master-art build tree):
- `art/`, `libcore/`, `dalvik/` present - complete ART sources
- `frameworks/` directory exists but contains ONLY `libs/`, `proto_logging/` - NO `frameworks/base/`
- Build output in `out/`:
  - `host/linux-x86/lib64/libart.so` (69MB) - host/glibc ART runtime
  - `host/linux-x86/bin/aidl`, `d8`, `dalvikvm64` - host tools
  - `target/product/module_x86_64/apex/com.android.art/javalib/` - core-oj.jar, core-libart.jar, okhttp.jar, etc.
  - `host/linux-x86/apex/com.android.i18n/javalib/core-icu4j.jar`
  - `host/linux-x86/apex/com.android.conscrypt/javalib/conscrypt.jar`

**deps/aosp-framework-src/** (framework source tree, separate repo client):
- Full `frameworks/base/` synced (~5GB)
- `external/icu/`, `external/freetype/`, `external/harfbuzz_ng/`, `external/libpng/`, `external/skia/`
- This is the source input, NOT the build tree

**Key insight**: The plan correctly separates these concerns (lines 175-191). The `aosp-full` was intentionally kept WITHOUT `frameworks/base` to avoid the `UNBUNDLED_BUILD_SDKS_FROM_SOURCE=true` transition.

### Source Files Actually Used

The `build-framework.sh` script compiles a minimal cherry-pick:
- `src/java/aosp/android/opengl` - minimal OpenGL stubs
- `src/java/aosp/com/google/android/gles_jni` - GLImpl.java (2200+ lines of JNI native methods)
- `src/java/aosp/javax/microedition/khronos` - EGL/GL interfaces
- `src/java/nova-shims/` - Shim classes for hidden APIs

This is NOT a full framework compile - it's a targeted overlay approach.

### Phase 1 Gaps (Accurate Assessment)
1. **Wayland surface management** (`wayland.c:19`) - registry listener not implemented
2. **EGL surface binding** (`egl.c:15`) - no `wl_egl_window` integration, currently surfaceless
3. **MessageQueue JNI** - no `android_os_MessageQueue.c`, critical for Looper
4. **Surface JNI** - no `android_view_Surface.c`
5. **IWindowSession service stub** - interface generated but no Java implementation
6. **Choreographer** - completely missing

---

## Strengths (Validated Against Codebase)

### 1. Architecture Clarity ✅
- **In-process Binder insight** (lines 9-11) matches actual AIDL stub approach
- The `IWindowSession.aidl` correctly defines minimal stub methods
- `nova/internal/Launcher.java` implements the bootstrap flow

### 2. Phased Scope Definition ✅
- Phase 1-3 boundaries align with actual test APKs in `apks/` directory
- Native library boundary (lines 43-68) correctly identifies Bionic vs glibc limitation

### 3. Implementation Order Alignment ✅
The plan's 12-step order aligns with codebase reality:
- Steps A-D mostly complete (except MessageQueue JNI)
- Steps E-G partially complete

---

## Concerns / Misalignments

### 1. Framework Source Worktree Correctly Separated
**Assessment:** The plan correctly separates `aosp-full` (build tree) from `aosp-framework-src` (source input). This is intentional to avoid the unbundled build transition.

### 2. Wayland Implementation Gaps
**Location:** `src/wayland.c:19`, `src/egl.c:15`

Critical missing pieces:
- No registry listener implementation to bind compositor, shm, seat
- No xdg-shell integration for window management
- No frame callback for vsync

The current `egl.c` creates a "surfaceless EGL context" - this won't work for `gles3jni` which expects a real window surface.

### 3. JNI Coverage Gap
**Location:** Plan line 293-307 (Phase 1 JNI list)

These JNI methods are NOT implemented:
- `Surface.nativeCreate()` - no `android_view_Surface.c`
- `MessageQueue.nativePollOnce()` - no `android_os_MessageQueue.c`

The plan mentions 8 C files (lines 326-334), but only `art.c`, `wayland.c`, `egl.c`, `main.c` exist. Missing: `message_queue.c`, `window.c`, `surface.c`.

### 4. AIDL Stub Implementation Gap
**Location:** Plan lines 284-291

The plan lists stub methods for `IWindowSession` but there's no Java implementation - only the generated interface exists.

### 5. Smoke Test vs Visual Success Criterion
Current smoke test checks for bootstrap failures, but "rotating triangle visible" requires full Surface+EGL+Wayland integration.

---

## Recommendations

### Immediate Priorities
1. **Wayland registry listener** - bind compositor, shm, seat globals
2. **MessageQueue JNI** - integrate with Wayland epoll loop for Looper
3. **wl_egl_window binding** in `egl.c` - essential for rendering
4. **IWindowSession Java implementation** - service stub for in-process dispatch

### Documentation Updates
5. **Clarify framework source approach** - document overlay source set AND `make framework` fallback
6. **Update file reference table** to reflect actual files vs planned files
7. **Clarify gles3jni strategy** - the extracted native lib won't work (Bionic vs glibc)

---

## Risk Assessment

| Risk | Current State | Recommendation |
|------|---------------|----------------|
| Wayland surface lifecycle | High - unimplemented | Registry listener before EGL |
| Looper integration | Medium-High - no nativePollOnce | MessageQueue test gate |
| EGL without surface | High - surfaceless | wl_egl_window binding required |

---

## Final Verdict

**✅ APPROVED WITH CORRECTIONS**

The plan is sound but Phase 1 requires:
1. Wayland surface lifecycle implementation
2. MessageQueue JNI for Looper
3. wl_egl_window binding for rendering

The framework source separation strategy is correct. Proceed with implementation prioritizing the missing C/JNI components.