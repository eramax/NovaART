#!/bin/sh
# Build NovaART runtime for host (Ubuntu/glibc/Wayland)
set -e

cd "$(dirname "$0")"

BUILD_DIR="${BUILD_DIR:-build/host}"
PREFIX="${PREFIX:-$PWD/output}"

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
