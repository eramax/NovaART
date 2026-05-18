#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
SCRIPT="$ROOT/scripts/build-framework.sh"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -x "$SCRIPT" ] || fail "missing executable script: $SCRIPT"
[ -x "$ROOT/scripts/generate-phase1-nova-aidl.sh" ] || fail "missing AIDL generator"

"$SCRIPT" --out-root "$TMPDIR/out" --compile-only >/dev/null

[ -f "$TMPDIR/out/nova-framework-classes.jar" ] \
  || fail "missing classes jar"
[ -f "$TMPDIR/out/classes/android/opengl/GLSurfaceView.class" ] \
  || fail "missing GLSurfaceView.class"
[ -f "$TMPDIR/out/classes/android/view/IWindowManager.class" ] \
  || fail "missing IWindowManager.class"
[ -f "$TMPDIR/out/classes/android/view/SurfaceControl.class" ] \
  || fail "missing SurfaceControl.class"

echo "PASS"
