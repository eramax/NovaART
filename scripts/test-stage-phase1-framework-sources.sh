#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
SCRIPT="$ROOT/scripts/stage-phase1-framework-sources.sh"
SRC_ROOT="$ROOT/deps/aosp-framework-src/frameworks/base"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -x "$SCRIPT" ] || fail "missing executable script: $SCRIPT"
[ -d "$SRC_ROOT" ] || fail "missing source root: $SRC_ROOT"

DEST_ROOT="$TMPDIR/staged"
"$SCRIPT" --source-root "$SRC_ROOT" --dest-root "$DEST_ROOT"

check_file() {
  local path="$1"
  [ -f "$DEST_ROOT/$path" ] || fail "missing staged file: $path"
}

check_file "src/java/aosp/android/opengl/GLSurfaceView.java"
check_file "src/java/aosp/com/google/android/gles_jni/EGLImpl.java"
check_file "src/java/aosp/javax/microedition/khronos/egl/EGL10.java"
check_file "src/java/aosp/android/os/Handler.java"
check_file "src/java/aosp/android/os/MessageQueue.java"
check_file "src/java/aosp/android/graphics/Rect.java"
check_file "src/java/aosp/android/content/pm/ApplicationInfo.java"
check_file "src/java/aosp/android/content/pm/ActivityInfo.java"
check_file "src/java/aosp/android/view/DisplayInfo.java"
check_file "src/java/aosp/android/view/InputChannel.java"
check_file "src/java/aosp/android/view/WindowRelayoutResult.java"
check_file "src/java/aosp/android/view/SurfaceControl.java"
check_file "src/aidl/aosp/android/view/IWindowManager.aidl"
check_file "src/aidl/aosp/android/content/pm/IPackageManager.aidl"

grep -q '^package android\.os;$' "$DEST_ROOT/src/java/aosp/android/os/MessageQueue.java" \
  || fail "staged MessageQueue.java has unexpected package"

grep -q 'LegacyMessageQueue' "$DEST_ROOT/staging-manifest.txt" \
  || fail "manifest should record LegacyMessageQueue source"

echo "PASS"
