# AOSP master-art Investigation

## Overview

Checkout at `deps/aosp-full/`. 354GB total. `master-art` manifest (reduced — no
`frameworks/base/`, no device HALs, no vendor blobs).

## What We Have (key dirs)

| Dir | Size | Notes |
|---|---|---|
| `art/` | 115MB | Full ART source: runtime, compiler, dex2oat, JIT, libdexfile, etc. |
| `prebuilts/clang/host/linux-x86/` | 14GB | 5 Clang versions (default: `clang-r547379` = Clang 20.0.0) |
| `prebuilts/jdk/jdk21/` | present | OpenJDK 21.0.4 Android_PDK |
| `prebuilts/go/` | 698MB | Go 1.23.4 (for Soong) |
| `prebuilts/build-tools/` | present | ckati, ninja, make, etc. |
| `prebuilts/rust/` | present | Rust toolchain |
| `prebuilts/runtime/` | 534MB | Runtime prebuilts for ART module build |
| `build/soong/` | 16MB | Soong build system |
| `build/make/` | present | envsetup.sh, banchan, etc. |
| `libcore/` | 118MB | Core Java libraries (ojluni, luni, dalvik) |
| `libnativehelper/` | 644K | JNI helpers: JniInvocation, JNIHelp |
| `external/` | 2.2GB | 100+ deps (libcxx, boringssl, zlib, icu, skia, perfetto...) |
| `out/` | 919MB | Stale build from previous attempt (aosp_64bitonly_x86_64) |

## What We DON'T Have

- `frameworks/base/` — intentionally absent in master-art manifest.
  NovaART provides its own JNI stubs instead.
- `frameworks/base/core/java/` — no framework Java classes.
  For bootclasspath we'd need a separate full-Android checkout or
  prebuilt `android.jar`. NOT needed for Phase 1 (GLES rendering
  goes through GLES JNI stubs directly, not framework classes).

## ART Build System

### How ART Builds (Soong)

- `ART` modules use custom types (`art_cc_library`, `art_cc_binary`)
  defined in `art/build/art.go`.
- These wrap Soong's `cc.LibraryFactory()` / `cc.BinaryFactory()`.
- Host builds are enabled by default (`ART_BUILD_HOST=true`).
- `ART_TARGET_LINUX` env var: if set, device target builds for
  glibc/Linux instead of Bionic/Android.
- Default toolchain: Clang 20.0.0 (`clang-r547379`).
- Soong reads `Android.bp` files; the root `Android.bp` links to
  `build/soong/root.bp`.

### Key ART Modules

| Module | File | Type | Notes |
|---|---|---|---|
| `libart` | `art/runtime/Android.bp:870` | `art_cc_library` (shared) | Includes JIT via `libart-compiler` + `libart-runtime` |
| `libartd` | `art/runtime/Android.bp:943` | debug variant | Extra checks, slower |
| `dex2oat` | `art/dex2oat/Android.bp` | `art_cc_binary` | `host_supported: true` |
| `dalvikvm` | `art/dalvikvm/Android.bp` | `art_cc_binary` | Java launcher |
| `art_cc_library` | `art/build/art.go:393` | factory | Wraps `cc.LibraryFactory()` |

All host-related ART modules declare `host_supported: true` + `target: { host: {...} }`.

### Build Entry Points

**Method 1 — banchan (official ART module build):**
```sh
cd deps/aosp-full
source build/envsetup.sh
banchan com.android.art x86_64
m apps_only dist
```
- Builds full ART APEX (device code). Overkill for NovaART.
- Takes hours, produces APEX file.

**Method 2 — Direct Soong (recommended for NovaART):**
```sh
cd deps/aosp-full
source build/envsetup.sh
# Set unbundled mode + allow missing deps:
export SOONG_ALLOW_MISSING_DEPENDENCIES=true
export TARGET_BUILD_UNBUNDLED=true
build/soong/soong_ui.bash --make-mode -j$(nproc) libart dex2oat dalvikvm
```
- Builds only the specified host modules.
- `libart` → `out/soong/host/linux-x86_64/lib64/libart.so`
- `dex2oat` → `out/soong/host/linux-x86_64/bin/dex2oat`
- `dalvikvm` → `out/soong/host/linux-x86_64/bin/dalvikvm`

**Method 3 — build-art-host target (from ART buildbot):**
```sh
build/soong/soong_ui.bash --make-mode -j$(nproc) \
  SOONG_ALLOW_MISSING_DEPENDENCIES=true \
  TARGET_BUILD_UNBUNDLED=true \
  build-art-host-gtests
```

### Build Artifact Layout

```
out/soong/host/linux-x86_64/
├── bin/
│   ├── dex2oat
│   ├── dalvikvm
│   ├── dex2oatd
│   ├── oatdump
│   └── profman
└── lib64/
    ├── libart.so
    ├── libartd.so
    ├── libdexfile.so
    ├── libnativebridge.so
    └── libnativehelper.so  (NOT libnativehelper — JniInvocation is
                              header-only/source in libnativehelper/)
```

### Build Dependencies

ART's host build needs these external libraries (all present):
- `external/libcxx` — LLVM libc++ (ART's C++ runtime on host)
- `external/libcxxabi` — libc++ ABI
- `external/boringssl` — OpenSSL replacement
- `external/zlib` — compression
- `external/lz4` — LZ4 compression
- `external/dlmalloc` — allocator (for ART's gc)
- `external/icu` — Unicode support (libcore deps)
- `external/libffi` — foreign function interface
- `external/vixl` — ARM64 simulator

### Potential Build Issues

1. **Stale out/soong config**: Previous build was `aosp_64bitonly_x86_64`.
   The stale ninja glob files can cause "no rule to make target" errors.
   **Clean before any new build:**
   ```sh
   rm -rf out/soong/
   ```

2. **Missing Soong modules**: Direct `m libart dex2oat` may fail on missing
   Java dependencies or module not found. The `banchan` approach is safer
   because it properly configures the unbundled module build.
   `SOONG_ALLOW_MISSING_DEPENDENCIES=true` can hide real problems —
   prefer `banchan` setup instead.

3. **sdk_version issues**: Java modules in ART need `sdk_version: "core_platform"`.
   `banchan` handles this correctly; raw `m` may not.

4. **Linux host glibc compatibility**: ART host build targets glibc, which
   is what Ubuntu 25.10 provides. No issues expected.

5. **Host artifact paths may vary**: The exact output paths depend on Soong
   configuration. Always verify with `find out/ -name libart.so -type f`
   after build instead of assuming fixed paths.

6. **Boot image missing**: Even if libart.so builds, ART won't start without
   the boot image (boot.art/.oat). The banchan build produces this; the
   direct `m libart dex2oat` approach may NOT produce a boot image.
   **The banchan approach is the correct method for Phase 1.**

7. **Transitive library deps**: libart.so depends on libdexfile.so,
   libprofile.so, libartbase.so, libvixl.so, etc. All must be in
   LD_LIBRARY_PATH at runtime. The banchan build output has them all
   in `com.android.art/lib64/`.

## ART Runtime Architecture

### JNI Invocation (how NovaART will load ART)

`libnativehelper` provides `JniInvocation.c`:
- `JniInvocationCreate()` — dlopen `libart.so` / `libartd.so`
- Function pointers: `JNI_GetDefaultJavaVMInitArgs`, `JNI_CreateJavaVM`, `JNI_GetCreatedJavaVMs`

NovaART's `art.c` already implements this pattern (works with or without libart.so).

### ART Runtime Structure (key dirs)

| Dir | Size | Contents |
|---|---|---|
| `art/runtime/jni/` | 668K | JNI entry points (JNI function table) |
| `art/runtime/native/` | 568K | `java_lang_Class.cc`, `java_lang_Object.cc`, `dalvik_system_DexFile.cc`, `dalvik_system_VMRuntime.cc` |
| `art/runtime/gc/` | 2.2M | GC: concurrent mark-sweep, concurrent copying |
| `art/runtime/arch/` | 1.4M | Architecture: x86, x86_64, arm, arm64 |
| `art/runtime/interpreter/` | 1.4M | Bytecode interpreter |
| `art/runtime/jit/` | 452K | JIT compiler |
| `art/runtime/oat/` | 652K | OAT file loading |
| `art/compiler/` | various | Optimizing compiler (used by JIT + dex2oat) |

### Boot Image Requirements (CRITICAL — was WRONG in earlier version)

**ART cannot create a JavaVM without a boot image** containing core Java
classes (java.lang.Object, java.lang.String, java.util.*, etc.). This was
previously misunderstood — the bootclasspath is not optional.

On the host, ART expects the boot image at:
```
$ANDROID_ROOT/com.android.art/
├── framework/
│   ├── boot.art
│   ├── boot.oat
│   ├── core.art
│   └── core.oat
├── javalib/
│   ├── core-oj-hostdex.jar
│   ├── core-libart-hostdex.jar
│   └── core-icu4j-hostdex.jar
├── lib64/libart.so
├── bin/dalvikvm
└── etc/
```

Production method: build via `banchan com.android.art x86_64` → `m apps_only dist`
which produces the complete host ART runtime including boot image.

Even for gles3jni Phase 1, the bootclasspath is required. The app's Java
bytecode (`Activity.onCreate()` etc.) depends on java.lang.* classes from
the boot image. gles3jni is NOT a framework-free native app — it has a Java
Activity layer that ART must load.

NOTE: The JNI stubs in NovaART's `src/jni/` are for android.* framework
native methods (android.graphics.* etc.). The core Java classes (java.lang.*)
are NOT stubbed — they come from the boot image. This is correct:
- Bootclasspath → libcore/ → boot.oat (produced by AOSP build)
- Framework native stubs → NovaART's JNI modules
- App code → dex2oat from APK

## NovaART Integration Plan

### Step-by-step Build Sequence

**Phase A — Build ART + boot image (via banchan — the only reliable way):**
```
Step 1: Clean stale out/soong (recommended to avoid conflicts)
  rm -rf deps/aosp-full/out/soong/

Step 2: Source env + configure banchan
  cd deps/aosp-full
  source build/envsetup.sh
  banchan com.android.art x86_64

Step 3: Build (this produces host ART + boot image + APEX)
  m apps_only dist -j$(nproc)

  Output artifacts (approximate paths — verify after build):
    Host libart:      out/soong/host/linux-x86_64/com.android.art/lib64/libart.so
    Host dex2oat:     out/soong/host/linux-x86_64/com.android.art/bin/dex2oat
    Host boot image:  out/soong/host/linux-x86_64/com.android.art/framework/
    Host javalibs:    out/soong/host/linux-x86_64/com.android.art/javalib/
```

**Phase B — Stage for NovaART:**
```
Step 4: Copy ART shared libraries
  mkdir -p deps/NovaART/output/lib
  cp -r out/soong/host/linux-x86_64/com.android.art/lib64/libart.so \
        deps/NovaART/output/lib/
  cp out/soong/host/linux-x86_64/com.android.art/bin/dex2oat \
        deps/NovaART/output/bin/

Step 5: Stage boot image + env
  mkdir -p deps/NovaART/output/android-data
  cp -r out/soong/host/linux-x86_64/com.android.art/ \
        deps/NovaART/output/android-data/

Step 6: Verify symbols
  nm -D deps/NovaART/output/lib/libart.so | grep JNI_CreateJavaVM
  # Expected: JNI_CreateJavaVM, JNI_GetDefaultJavaVMInitArgs

Step 7: Verify dynamic deps
  LD_LIBRARY_PATH=./output/lib ldd deps/NovaART/output/lib/libart.so
  # All deps should resolve (use LD_LIBRARY_PATH for runtime dir)
```

**Phase C — Run NovaART with full ART env:**
```
Step 8: Build NovaART
  cd deps/NovaART
  ./build-host.sh

Step 9: Run with ART env
  LD_LIBRARY_PATH=./output/lib \
  ANDROID_ROOT=./output/android-data \
  ANDROID_ART_ROOT=./output/android-data/com.android.art \
  ANDROID_DATA=./output/android-data/data \
  ./output/bin/novaart apks/gles3jni.apk
```

### Timeline Implications

- ART banchan build: ~60-120 min first time (full Soong bootstrap + C++ compile)
- Subsequent incremental builds: ~5-15 min
- Disk usage during build: ~10-15GB in out/soong/
- NovaART skeleton builds in <30s with meson

## Appendix: Useful Commands

```sh
# Check default toolchain version
grep ClangDefaultVersion deps/aosp-full/build/soong/cc/config/global.go
  # → "clang-r547379" (Clang 20.0.0)

# Find host-enabled modules in art/
grep -rl "host_supported: true" deps/aosp-full/art/ --include '*.bp' | wc -l
  # → 68 host-supported modules

# Check libart module definition
grep -A 25 'name: "libart"' deps/aosp-full/art/runtime/Android.bp

# Verify JDK
deps/aosp-full/prebuilts/jdk/jdk21/linux-x86/bin/java -version
  # → OpenJDK 21.0.4 Android_PDK

# Check existing build config
cat deps/aosp-full/out/soong/soong.aosp_64bitonly_x86_64.variables | python3 -m json.tool | head -50

# Force a soong bootstrap (if needed)
source build/envsetup.sh; cd art; build/soong/soong_ui.bash --make-mode --soong-only

# Build specific ART host target (from ART buildbot script)
build/soong/soong_ui.bash --make-mode -j$(nproc) \
  SOONG_ALLOW_MISSING_DEPENDENCIES=true TARGET_BUILD_UNBUNDLED=true \
  build-art-host-gtests
```

## References

- `deps/aosp-full/art/build/art.go` — ART Soong plugin (443 lines)
- `deps/aosp-full/art/build/Android.common_build.mk` — Build flag definitions
- `deps/aosp-full/art/build/README.md` — ART module build docs (banchan approach)
- `deps/aosp-full/art/runtime/Android.bp` — libart module definition
- `deps/aosp-full/art/dex2oat/Android.bp` — dex2oat definition
- `deps/aosp-full/art/tools/buildbot-build.sh` — CI build script (host build reference)
- `deps/aosp-full/libnativehelper/JniInvocation.c` — JNI invocation API source
