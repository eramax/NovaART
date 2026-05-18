package android.content.pm;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;

/** Minimal NovaART package manager shape for local in-process dispatch. */
interface IPackageManager {
    ApplicationInfo getApplicationInfo(String packageName, long flags, int userId);
    ActivityInfo getActivityInfo(in ComponentName className, long flags, int userId);
    int checkPermission(String permName, String pkgName, int userId);
}
