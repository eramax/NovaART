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
[ -f "$TMPDIR/out/classes/android/os/BaseBundle.class" ] \
  || fail "missing BaseBundle.class"
[ -f "$TMPDIR/out/classes/android/os/Build.class" ] \
  || fail "missing Build.class"
[ -f "$TMPDIR/out/classes/android/content/Intent.class" ] \
  || fail "missing Intent.class"
[ -f "$TMPDIR/out/classes/android/content/ContentResolver.class" ] \
  || fail "missing ContentResolver.class"
[ -f "$TMPDIR/out/classes/android/content/res/Resources.class" ] \
  || fail "missing Resources.class"
[ -f "$TMPDIR/out/classes/android/content/res/Configuration.class" ] \
  || fail "missing Configuration.class"
[ -f "$TMPDIR/out/classes/android/content/res/XmlResourceParser.class" ] \
  || fail "missing XmlResourceParser.class"
[ -f "$TMPDIR/out/classes/android/util/SparseArray.class" ] \
  || fail "missing SparseArray.class"
[ -f "$TMPDIR/out/classes/android/content/SharedPreferences.class" ] \
  || fail "missing SharedPreferences.class"
[ -f "$TMPDIR/out/classes/android/content/pm/PackageManager.class" ] \
  || fail "missing PackageManager.class"
[ -f "$TMPDIR/out/classes/android/content/pm/PackageInfo.class" ] \
  || fail "missing PackageInfo.class"
[ -f "$TMPDIR/out/classes/android/content/pm/ApplicationInfo.class" ] \
  || fail "missing ApplicationInfo.class"
[ -f "$TMPDIR/out/classes/android/content/pm/ActivityInfo.class" ] \
  || fail "missing ActivityInfo.class"
[ -f "$TMPDIR/out/classes/android/content/pm/PackageManager.class" ] \
  || fail "missing PackageManager.class"
[ -f "$TMPDIR/out/classes/android/content/pm/PackageManager.class" ] \
  || fail "missing PackageManager.class"
[ -f "$TMPDIR/out/classes/android/content/pm/PackageInfo.class" ] \
  || fail "missing PackageInfo.class"
[ -f "$TMPDIR/out/classes/android/os/Looper.class" ] \
  || fail "missing Looper.class"
[ -f "$TMPDIR/out/classes/android/preference/PreferenceManager.class" ] \
  || fail "missing PreferenceManager.class"
[ -f "$TMPDIR/out/classes/android/provider/Settings.class" ] \
  || fail "missing Settings.class"
[ -f "$TMPDIR/out/classes/android/view/ViewTreeObserver.class" ] \
  || fail "missing ViewTreeObserver.class"
[ -f "$TMPDIR/out/classes/android/view/Window.class" ] \
  || fail "missing Window.class"
[ -f "$TMPDIR/out/classes/android/app/AlertDialog.class" ] \
  || fail "missing AlertDialog.class"
[ -f "$TMPDIR/out/classes/android/webkit/WebView.class" ] \
  || fail "missing WebView.class"
[ -f "$TMPDIR/out/classes/android/webkit/WebSettings.class" ] \
  || fail "missing WebSettings.class"
[ -f "$TMPDIR/out/classes/android/widget/Toast.class" ] \
  || fail "missing Toast.class"

javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.Activity \
  | grep -q 'boolean requestWindowFeature(int);' \
  || fail "Activity.requestWindowFeature(int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.Activity \
  | grep -q 'android.view.Window getWindow();' \
  || fail "Activity.getWindow() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.Activity \
  | grep -q 'void finish();' \
  || fail "Activity.finish() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.Activity \
  | grep -q 'void setContentView(int);' \
  || fail "Activity.setContentView(int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.Activity \
  | grep -q 'android.view.View findViewById(int);' \
  || fail "Activity.findViewById(int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.app.AlertDialog\$Builder \
  | grep -q 'android.app.AlertDialog create();' \
  || fail "AlertDialog.Builder.create() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'android.content.ContentResolver getContentResolver();' \
  || fail "Context.getContentResolver() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'android.content.pm.PackageManager getPackageManager();' \
  || fail "Context.getPackageManager() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.pm.PackageManager \
  | grep -q 'android.content.pm.PackageInfo getPackageInfo(java.lang.String, int)' \
  || fail "PackageManager.getPackageInfo(String,int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'android.content.res.Resources getResources();' \
  || fail "Context.getResources() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'java.io.File getFilesDir();' \
  || fail "Context.getFilesDir() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'java.io.File getCacheDir();' \
  || fail "Context.getCacheDir() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebView \
  | grep -q 'android.webkit.WebSettings getSettings();' \
  || fail "WebView.getSettings() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebView \
  | grep -q 'void loadUrl(java.lang.String);' \
  || fail "WebView.loadUrl(String) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebView \
  | grep -q 'void loadDataWithBaseURL(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String);' \
  || fail "WebView.loadDataWithBaseURL(...) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebSettings \
  | grep -q 'void setDatabaseEnabled(boolean);' \
  || fail "WebSettings.setDatabaseEnabled(boolean) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebSettings \
  | grep -q 'void setRenderPriority(android.webkit.WebSettings\$RenderPriority);' \
  || fail "WebSettings.setRenderPriority(RenderPriority) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.webkit.WebSettings\$RenderPriority \
  | grep -q 'class android.webkit.WebSettings\$RenderPriority' \
  || fail "WebSettings.RenderPriority missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.widget.Toast \
  | grep -q 'android.widget.Toast makeText(android.content.Context, java.lang.CharSequence, int);' \
  || fail "Toast.makeText(...) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.widget.Toast \
  | grep -q 'android.widget.Toast makeText(android.content.Context, int, int);' \
  || fail "Toast.makeText(Context,int,int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.util.SparseArray \
  | grep -q 'int size();' \
  || fail "SparseArray.size() missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.res.Resources \
  | grep -q 'android.content.res.XmlResourceParser getXml(int);' \
  || fail "Resources.getXml(int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.res.Configuration \
  | grep -q 'int screenLayout;' \
  || fail "Configuration.screenLayout missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'android.content.SharedPreferences getSharedPreferences(java.lang.String, int);' \
  || fail "Context.getSharedPreferences(String,int) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.content.Context \
  | grep -q 'void startActivity(android.content.Intent);' \
  || fail "Context.startActivity(Intent) missing"
javap -classpath "$TMPDIR/out/nova-framework-classes.jar" android.os.Looper \
  | grep -q 'java.lang.Thread getThread();' \
  || fail "Looper.getThread() missing"

echo "PASS"
