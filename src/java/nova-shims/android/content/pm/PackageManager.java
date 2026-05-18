package android.content.pm;

import android.content.ComponentName;
import android.os.RemoteException;

public class PackageManager {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED = -1;

    public ApplicationInfo getApplicationInfo(String packageName, long flags) throws NameNotFoundException {
        PackageInfo packageInfo = NovaPackageManager.getInstance().getPackageInfo(packageName, (int) flags);
        if (packageInfo != null && packageInfo.applicationInfo != null) {
            return packageInfo.applicationInfo;
        }
        IPackageManager pm = android.app.AppGlobals.getPackageManager();
        if (pm == null) {
            throw new NameNotFoundException(packageName);
        }
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, flags, 0);
            if (info == null) {
                throw new NameNotFoundException(packageName);
            }
            return info;
        } catch (RemoteException e) {
            throw new NameNotFoundException(packageName);
        }
    }

    public ActivityInfo getActivityInfo(ComponentName component, long flags) throws NameNotFoundException {
        ActivityInfo activityInfo = NovaPackageManager.getInstance().getActivityInfo(component, (int) flags);
        if (activityInfo != null) {
            return activityInfo;
        }
        IPackageManager pm = android.app.AppGlobals.getPackageManager();
        if (pm == null) {
            throw new NameNotFoundException(component != null ? component.toString() : "");
        }
        try {
            ActivityInfo info = pm.getActivityInfo(component, flags, 0);
            if (info == null) {
                throw new NameNotFoundException(component != null ? component.toString() : "");
            }
            return info;
        } catch (RemoteException e) {
            throw new NameNotFoundException(component != null ? component.toString() : "");
        }
    }

    public int checkPermission(String permName, String pkgName) {
        IPackageManager pm = android.app.AppGlobals.getPackageManager();
        if (pm == null) {
            return PERMISSION_GRANTED;
        }
        try {
            return pm.checkPermission(permName, pkgName, 0);
        } catch (RemoteException e) {
            return PERMISSION_DENIED;
        }
    }

    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        PackageInfo info = NovaPackageManager.getInstance().getPackageInfo(packageName, flags);
        if (info == null) {
            throw new NameNotFoundException(packageName);
        }
        return info;
    }

    public static class NameNotFoundException extends Exception {
        public NameNotFoundException(String name) {
            super(name);
        }
    }
}
