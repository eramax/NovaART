#!/bin/sh
# Build NovaART runtime for host (Ubuntu/glibc/Wayland)
set -e

cd "$(dirname "$0")"

BUILD_DIR="${BUILD_DIR:-build/host}"
PREFIX="${PREFIX:-$PWD/output}"

# Toolchain: prefer AOSP prebuilt Clang, fall back to system
AOSP_CLANG="../aosp-full/prebuilts/clang/host/linux-x86/clang-r547379/bin"
if [ -x "$AOSP_CLANG/clang" ]; then
  export CC="$AOSP_CLANG/clang"
  export CXX="$AOSP_CLANG/clang++"
  echo "  Toolchain: AOSP Clang ($AOSP_CLANG)"
elif command -v clang-20 >/dev/null 2>&1; then
  export CC="$(command -v clang-20)"
  export CXX="$(command -v clang++-20)"
  echo "  Toolchain: system Clang 20"
else
  echo "  Toolchain: default (cc/c++)"
fi

echo "=== NovaART Build for Host ==="
echo "  Build dir: $BUILD_DIR"
echo "  Prefix:    $PREFIX"
echo ""

# Step 1: Configure
echo "--- Configuring with meson ---"
if [ -d "$BUILD_DIR" ]; then
  meson setup --reconfigure "$BUILD_DIR" src \
    --prefix="$PREFIX" \
    -Dbuildtype=debugoptimized
else
  meson setup "$BUILD_DIR" src \
    --prefix="$PREFIX" \
    -Dbuildtype=debugoptimized
fi

# Step 2: Build
echo ""
echo "--- Building ---"
meson compile -C "$BUILD_DIR"

# Step 3: Install
echo ""
echo "--- Installing to $PREFIX ---"
meson install -C "$BUILD_DIR"

echo ""
echo "=== Build complete ==="
echo "Binary: $PREFIX/bin/novaart"
echo ""
echo "Run: LD_LIBRARY_PATH=/path/to/art/lib ./output/bin/novaart <apk>"
