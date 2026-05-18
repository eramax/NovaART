#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
AOSP_ROOT="$ROOT/deps/aosp-full"
DEST_ROOT="$ROOT/output"
ALLOW_APEX_ONLY=0

usage() {
  cat <<'EOF'
Usage: stage-art.sh [--aosp-root PATH] [--dest-root PATH] [--apex-only]

Stages the built ART runtime from the current master-art checkout into NovaART's
runtime layout under output/:
- output/android-root/apex/com.android.art/...
- output/android-root/com.android.art -> apex/com.android.art
- output/lib/*.so when host/glibc ART artifacts are available
EOF
}

while (($# > 0)); do
  case "$1" in
    --aosp-root)
      AOSP_ROOT="$2"
      shift 2
      ;;
    --dest-root)
      DEST_ROOT="$2"
      shift 2
      ;;
    --apex-only)
      ALLOW_APEX_ONLY=1
      shift
      ;;
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
done

SRC_APEX="$AOSP_ROOT/out/target/product/module_x86_64/apex/com.android.art"
HOST_APEX_ROOT="$AOSP_ROOT/out/host/linux-x86/apex"
HOST_I18N_ROOT="$AOSP_ROOT/out/host/linux-x86/com.android.i18n"
HOST_TZDATA_ROOT="$AOSP_ROOT/out/host/linux-x86/com.android.tzdata"
DEST_ANDROID_ROOT="$DEST_ROOT/android-root"
DEST_APEX="$DEST_ANDROID_ROOT/apex/com.android.art"
DEST_LIB="$DEST_ROOT/lib"
HOST_LIBART=""

if [ -f "$AOSP_ROOT/out/host/linux-x86/lib64/libart.so" ]; then
  HOST_LIBART="$AOSP_ROOT/out/host/linux-x86/lib64/libart.so"
else
  HOST_LIBART="$(find "$AOSP_ROOT/out" -path '*/host/*' -name 'libart.so' | head -n 1)"
fi

[ -d "$SRC_APEX" ] || {
  echo "missing ART apex output: $SRC_APEX" >&2
  exit 1
}

mkdir -p "$DEST_ANDROID_ROOT/apex" "$DEST_LIB"
rm -rf "$DEST_APEX"
cp -a "$SRC_APEX" "$DEST_APEX"

if [ -d "$HOST_APEX_ROOT/com.android.i18n" ]; then
  rm -rf "$DEST_ANDROID_ROOT/apex/com.android.i18n"
  cp -a "$HOST_APEX_ROOT/com.android.i18n" "$DEST_ANDROID_ROOT/apex/com.android.i18n"
fi

if [ -d "$HOST_APEX_ROOT/com.android.conscrypt" ]; then
  rm -rf "$DEST_ANDROID_ROOT/apex/com.android.conscrypt"
  cp -a "$HOST_APEX_ROOT/com.android.conscrypt" "$DEST_ANDROID_ROOT/apex/com.android.conscrypt"
fi

if [ -d "$HOST_I18N_ROOT" ]; then
  rm -rf "$DEST_ANDROID_ROOT/com.android.i18n"
  cp -a "$HOST_I18N_ROOT" "$DEST_ANDROID_ROOT/com.android.i18n"
fi

if [ -d "$HOST_TZDATA_ROOT" ]; then
  rm -rf "$DEST_ANDROID_ROOT/com.android.tzdata"
  cp -a "$HOST_TZDATA_ROOT" "$DEST_ANDROID_ROOT/com.android.tzdata"
fi

rm -f "$DEST_ANDROID_ROOT/com.android.art"
ln -s "apex/com.android.art" "$DEST_ANDROID_ROOT/com.android.art"

mkdir -p "$DEST_ROOT/android-data"

if [ -n "$HOST_LIBART" ]; then
  HOST_LIB_DIR="$(dirname "$HOST_LIBART")"
  find "$HOST_LIB_DIR" -maxdepth 1 -type f -name '*.so' -exec cp -a {} "$DEST_LIB/" \;
  echo "staged ART runtime into $DEST_ROOT"
  exit 0
fi

if ((ALLOW_APEX_ONLY)); then
  echo "staged ART classpath layout into $DEST_ROOT (host libart.so not available)"
  exit 0
fi

echo "staged ART classpath layout into $DEST_ROOT, but no host/glibc libart.so was found under $AOSP_ROOT/out" >&2
echo "Build the host ART runtime separately before attempting to run NovaART." >&2
exit 1
