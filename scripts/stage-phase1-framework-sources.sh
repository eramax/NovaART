#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
SOURCE_ROOT="$ROOT/deps/aosp-framework-src/frameworks/base"
DEST_ROOT="$ROOT"

usage() {
  cat <<'EOF'
Usage: stage-phase1-framework-sources.sh [--source-root PATH] [--dest-root PATH]

Copies the minimal Phase 1 NovaART framework source batch from frameworks/base
into a stable NovaART staging layout.
EOF
}

while (($# > 0)); do
  case "$1" in
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

[ -d "$SOURCE_ROOT" ] || {
  echo "missing source root: $SOURCE_ROOT" >&2
  exit 1
}

JAVA_DEST="$DEST_ROOT/src/java/aosp"
AIDL_DEST="$DEST_ROOT/src/aidl/aosp"
MANIFEST="$DEST_ROOT/staging-manifest.txt"

mkdir -p "$JAVA_DEST" "$AIDL_DEST"
: > "$MANIFEST"

copy_rel() {
  local category="$1"
  local src_root="$2"
  local rel_src="$3"
  local dest_root="$4"
  local rel_dest="${5:-$rel_src}"
  local src="$src_root/$rel_src"
  local dst="$dest_root/$rel_dest"

  [ -f "$src" ] || {
    echo "missing source file: $src" >&2
    exit 1
  }

  mkdir -p "$(dirname "$dst")"
  cp "$src" "$dst"
  printf '%s %s -> %s\n' "$category" "$src" "$dst" >> "$MANIFEST"
}

OPENGL_ROOT="$SOURCE_ROOT/opengl/java"
CORE_ROOT="$SOURCE_ROOT/core/java"
GRAPHICS_ROOT="$SOURCE_ROOT/graphics/java"

opengl_files=(
  android/opengl/GLSurfaceView.java
  android/opengl/GLDebugHelper.java
  android/opengl/EGLLogWrapper.java
  android/opengl/GLException.java
  com/google/android/gles_jni/EGLImpl.java
  com/google/android/gles_jni/EGLContextImpl.java
  com/google/android/gles_jni/EGLDisplayImpl.java
  com/google/android/gles_jni/EGLSurfaceImpl.java
  com/google/android/gles_jni/EGLConfigImpl.java
  com/google/android/gles_jni/GLImpl.java
  javax/microedition/khronos/egl/EGL10.java
  javax/microedition/khronos/egl/EGL11.java
  javax/microedition/khronos/egl/EGLConfig.java
  javax/microedition/khronos/egl/EGLContext.java
  javax/microedition/khronos/egl/EGLDisplay.java
  javax/microedition/khronos/egl/EGLSurface.java
  javax/microedition/khronos/opengles/GL.java
  javax/microedition/khronos/opengles/GL10.java
  javax/microedition/khronos/opengles/GL10Ext.java
  javax/microedition/khronos/opengles/GL11.java
  javax/microedition/khronos/opengles/GL11Ext.java
  javax/microedition/khronos/opengles/GL11ExtensionPack.java
)

core_java_files=(
  android/os/Bundle.java
  android/os/Handler.java
  android/os/Looper.java
  android/os/Message.java
  android/os/IBinder.java
  android/os/Binder.java
  android/os/IInterface.java
  android/os/RemoteException.java
  android/os/Parcelable.java
  android/os/Parcel.java
  android/os/SystemClock.java
  android/os/Trace.java
  android/util/Log.java
  android/util/AttributeSet.java
  android/util/DisplayMetrics.java
  android/content/Context.java
  android/content/ContextWrapper.java
  android/content/Intent.java
  android/content/ComponentName.java
  android/content/pm/ApplicationInfo.java
  android/content/pm/ActivityInfo.java
  android/app/Application.java
  android/view/SurfaceHolder.java
  android/view/Surface.java
  android/view/Display.java
  android/view/DisplayInfo.java
  android/view/InputChannel.java
  android/view/WindowRelayoutResult.java
  android/view/WindowManager.java
  android/view/SurfaceControl.java
)

graphics_java_files=(
  android/graphics/PixelFormat.java
  android/graphics/Point.java
  android/graphics/Rect.java
)

aidl_files=(
  android/view/IWindowManager.aidl
  android/view/IWindowSession.aidl
  android/hardware/display/IDisplayManager.aidl
  android/content/pm/IPackageManager.aidl
)

for f in "${opengl_files[@]}"; do
  copy_rel "java" "$OPENGL_ROOT" "$f" "$JAVA_DEST"
done

for f in "${core_java_files[@]}"; do
  copy_rel "java" "$CORE_ROOT" "$f" "$JAVA_DEST"
done

for f in "${graphics_java_files[@]}"; do
  copy_rel "java" "$GRAPHICS_ROOT" "$f" "$JAVA_DEST"
done

copy_rel \
  "java" \
  "$CORE_ROOT" \
  "android/os/LegacyMessageQueue/MessageQueue.java" \
  "$JAVA_DEST" \
  "android/os/MessageQueue.java"

for f in "${aidl_files[@]}"; do
  copy_rel "aidl" "$CORE_ROOT" "$f" "$AIDL_DEST"
done

echo "staged framework sources into $DEST_ROOT"
