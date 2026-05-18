#!/usr/bin/env bash
set -euo pipefail

ROOT="/mnt/mydata/projects2/qos/deps/NovaART"
APK="$ROOT/apks/gles3jni.apk"
LAUNCHER_SRC="$ROOT/src/java/nova-shims/nova/internal/Launcher.java"
JAVAC_BIN="${JAVAC:-$(command -v javac)}"
JAVA_BIN="${JAVA:-$(command -v java)}"
TMPDIR="$(mktemp -d)"
trap 'rm -rf "$TMPDIR"' EXIT

fail() {
  echo "FAIL: $*" >&2
  exit 1
}

[ -f "$APK" ] || fail "missing APK: $APK"
[ -f "$LAUNCHER_SRC" ] || fail "missing Launcher.java: $LAUNCHER_SRC"
[ -x "$JAVAC_BIN" ] || fail "missing javac: $JAVAC_BIN"
[ -x "$JAVA_BIN" ] || fail "missing java: $JAVA_BIN"
[ -f "$ROOT/output/lib/libandroid.so" ] || fail "missing host libandroid.so prerequisite"

mkdir -p "$TMPDIR/src" "$TMPDIR/classes" "$TMPDIR/native-libs"
mkdir -p "$TMPDIR/output/lib"
cp -L "$ROOT/output/lib/libandroid.so" "$TMPDIR/output/lib/libandroid.so"

cat >"$TMPDIR/src/TestLauncherNativeLibs.java" <<'EOF'
import java.io.File;
import java.lang.reflect.Method;

public final class TestLauncherNativeLibs {
    public static void main(String[] args) throws Exception {
        File apk = new File(args[0]);
        File outDir = new File(args[1]);
        File nativeParent = outDir.getParentFile();
        if (nativeParent == null || !nativeParent.getName().equals("dex")) {
            throw new IllegalStateException("expected dex dir parent for " + outDir);
        }

        Class<?> launcherClass = Class.forName("nova.internal.Launcher");
        Method method = launcherClass.getDeclaredMethod(
                "extractNativeLibraries", String.class, File.class, String.class);
        method.setAccessible(true);
        method.invoke(null, apk.getAbsolutePath(), outDir, "x86_64");

        File appLib = new File(outDir, "libgles3jni.so");
        if (!appLib.isFile()) {
            throw new IllegalStateException("missing extracted app library: " + appLib);
        }

        File compatLib = new File(outDir, "libGLESv3.so");
        if (!compatLib.exists()) {
            throw new IllegalStateException("missing GLESv3 compatibility library: " + compatLib);
        }

        File hostShim = new File(outDir, "libandroid.so");
        if (!hostShim.exists()) {
            throw new IllegalStateException("missing staged host shim: " + hostShim);
        }
    }
}
EOF

"$JAVAC_BIN" \
  -d "$TMPDIR/classes" \
  "$LAUNCHER_SRC" \
  "$TMPDIR/src/TestLauncherNativeLibs.java"

"$JAVA_BIN" \
  -cp "$TMPDIR/classes" \
  TestLauncherNativeLibs \
  "$APK" \
  "$TMPDIR/output/android-data/dex/native-libs" >/dev/null

[ -f "$TMPDIR/output/android-data/dex/native-libs/libgles3jni.so" ] \
  || fail "missing extracted libgles3jni.so"
[ -e "$TMPDIR/output/android-data/dex/native-libs/libGLESv3.so" ] \
  || fail "missing GLESv3 compatibility library"
[ -e "$TMPDIR/output/android-data/dex/native-libs/libandroid.so" ] \
  || fail "missing staged libandroid.so"
[ -e "$TMPDIR/output/android-data/dex/native-libs/libc.so" ] \
  || fail "missing staged libc.so compatibility library"
[ -e "$TMPDIR/output/android-data/dex/native-libs/libm.so" ] \
  || fail "missing staged libm.so compatibility library"
[ -e "$TMPDIR/output/android-data/dex/native-libs/libdl.so" ] \
  || fail "missing staged libdl.so compatibility library"

echo "PASS"
