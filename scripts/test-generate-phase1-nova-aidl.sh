#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
SCRIPT="$ROOT/scripts/generate-phase1-nova-aidl.sh"
AIDL_BIN="$ROOT/deps/aosp-full/out/host/linux-x86/bin/aidl"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -x "$SCRIPT" ] || fail "missing executable script: $SCRIPT"
[ -x "$AIDL_BIN" ] || fail "missing aidl tool: $AIDL_BIN"

DEST_ROOT="$TMPDIR/generated"
"$SCRIPT" --aidl-bin "$AIDL_BIN" --dest-root "$DEST_ROOT"

check_file() {
  local path="$1"
  [ -f "$DEST_ROOT/$path" ] || fail "missing generated file: $path"
}

check_file "src/generated/aidl/nova/android/view/IWindowManager.java"
check_file "src/generated/aidl/nova/android/view/IWindowSession.java"
check_file "src/generated/aidl/nova/android/view/IWindow.java"
check_file "src/generated/aidl/nova/android/view/IWindowSessionCallback.java"
check_file "src/generated/aidl/nova/android/hardware/display/IDisplayManager.java"
check_file "src/generated/aidl/nova/android/hardware/display/IDisplayManagerCallback.java"
check_file "src/generated/aidl/nova/android/content/pm/IPackageManager.java"

grep -q 'abstract class Stub' "$DEST_ROOT/src/generated/aidl/nova/android/view/IWindowManager.java" \
  || fail "IWindowManager.java missing Stub"
grep -q 'abstract class Stub' "$DEST_ROOT/src/generated/aidl/nova/android/content/pm/IPackageManager.java" \
  || fail "IPackageManager.java missing Stub"

echo "PASS"
