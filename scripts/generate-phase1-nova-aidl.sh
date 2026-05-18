#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
AIDL_BIN="$ROOT/deps/aosp-full/out/host/linux-x86/bin/aidl"
SOURCE_ROOT="$ROOT/src/aidl/nova"
DEST_ROOT="$ROOT"

usage() {
  cat <<'EOF'
Usage: generate-phase1-nova-aidl.sh [--aidl-bin PATH] [--source-root PATH] [--dest-root PATH]

Generates Java Binder stubs from the minimal Nova-owned Phase 1 AIDL set.
EOF
}

while (($# > 0)); do
  case "$1" in
    --aidl-bin)
      AIDL_BIN="$2"
      shift 2
      ;;
    --source-root)
      SOURCE_ROOT="$2"
      shift 2
      ;;
    --dest-root)
      DEST_ROOT="$2"
      shift 2
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

[ -x "$AIDL_BIN" ] || {
  echo "missing aidl tool: $AIDL_BIN" >&2
  exit 1
}
[ -d "$SOURCE_ROOT" ] || {
  echo "missing source root: $SOURCE_ROOT" >&2
  exit 1
}

OUT_DIR="$DEST_ROOT/src/generated/aidl/nova"
mkdir -p "$OUT_DIR"

primary_aidls=(
  android/view/IWindow.aidl
  android/view/IWindowManager.aidl
  android/view/IWindowSession.aidl
  android/view/IWindowSessionCallback.aidl
  android/hardware/display/IDisplayManager.aidl
  android/hardware/display/IDisplayManagerCallback.aidl
  android/content/pm/IPackageManager.aidl
)

for rel in "${primary_aidls[@]}"; do
  "$AIDL_BIN" --lang=java -o "$OUT_DIR" -I "$SOURCE_ROOT" "$SOURCE_ROOT/$rel"
done

echo "generated AIDL Java into $OUT_DIR"
