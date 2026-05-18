#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
SCRIPT="$ROOT/scripts/stage-art.sh"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -x "$SCRIPT" ] || fail "missing executable script: $SCRIPT"

"$SCRIPT" --dest-root "$TMPDIR/out" --apex-only >/dev/null

[ -d "$TMPDIR/out/android-root/apex/com.android.art/javalib" ] \
  || fail "missing staged javalib directory"
[ -L "$TMPDIR/out/android-root/com.android.art" ] \
  || fail "missing com.android.art symlink"
[ -f "$TMPDIR/out/android-root/apex/com.android.art/javalib/core-oj.jar" ] \
  || fail "missing core-oj.jar"

echo "PASS"
