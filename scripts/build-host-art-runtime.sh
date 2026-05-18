#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
AOSP_ROOT="$ROOT/deps/aosp-full"

usage() {
  cat <<'EOF'
Usage: build-host-art-runtime.sh

Builds the host/glibc ART runtime artifacts NovaART needs from the current
master-art checkout:
- out/host/linux-x86/lib64/libart.so
- out/host/linux-x86/bin/dex2oat64
- out/host/linux-x86/bin/dalvikvm64

This uses the regenerated build-art-host wrapper and targets only the host
outputs required by NovaART.
EOF
}

if (($# > 0)); then
  case "$1" in
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
fi

cd "$AOSP_ROOT"
source build/envsetup.sh >/dev/null
banchan com.android.art x86_64 >/dev/null

export SOONG_ALLOW_MISSING_DEPENDENCIES=true
export TARGET_BUILD_UNBUNDLED=true
export ART_TARGET_LINUX=true

# Regenerate the host wrapper after local prebuilts changes.
build/soong/soong_ui.bash --make-mode -j1 build-art-host

rtk ninja -f out/combined-module_x86_64-build-art-host.ninja -j"$(nproc)" \
  out/host/linux-x86/lib64/libart.so \
  out/host/linux-x86/bin/dex2oat64 \
  out/host/linux-x86/bin/dalvikvm64
