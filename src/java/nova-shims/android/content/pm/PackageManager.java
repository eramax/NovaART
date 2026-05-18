package android.content.pm;

import android.content.ComponentName;
import android.os.RemoteException;

public class PackageManager {
    public static final int PERMISSION_GRANTED = 0;
    public static final int PERMISSION_DENIED  = -1;

    public static final int GET_ACTIVITIES       = 0x00000001;
    public static final int GET_RECEIVERS        = 0x00000002;
    public static final int GET_SERVICES         = 0x00000004;
    public static final int GET_PROVIDERS        = 0x00000008;
    public static final int GET_INSTRUMENTATION  = 0x00000010;
    public static final int GET_INTENT_FILTERS   = 0x00000020;
    public static final int GET_SIGNATURES       = 0x00000040;
    public static final int GET_RESOLVED_FILTER  = 0x00000040;
    public static final int GET_META_DATA        = 0x00000080;
    public static final int GET_GIDS             = 0x00000100;
    public static final int GET_DISABLED_COMPONENTS = 0x00000200;
    public static final int GET_SHARED_LIBRARY_FILES = 0x00000400;
    public static final int GET_URI_PERMISSION_PATTERNS = 0x00000800;
    public static final int GET_PERMISSIONS      = 0x00001000;
    public static final int GET_UNINSTALLED_PACKAGES = 0x00002000;
    public static final int GET_CONFIGURATIONS   = 0x00004000;
    public static final int GET_DISABLED_UNTIL_USED_COMPONENTS = 0x00008000;
    public static final int MATCH_DEFAULT_ONLY   = 0x00010000;
    public static final int MATCH_ALL            = 0x00020000;
    public static final int MATCH_DISABLED_COMPONENTS = GET_DISABLED_COMPONENTS;
    public static final int MATCH_SYSTEM_ONLY    = 0x00040000;
    public static final int MATCH_UNINSTALLED_PACKAGES = GET_UNINSTALLED_PACKAGES;
    public static final int GET_SIGNING_CERTIFICATES = 0x08000000;

    public ApplicationInfo getApplicationInfo(String packageName, long flags) throws NameNotFoundException {
        PackageInfo info = NovaPackageManager.getInstance().getPackageInfo(packageName, (int) flags);
        if (info != null && info.applicationInfo != null) return info.applicationInfo;
        throw new NameNotFoundException(packageName);
    }

    public ActivityInfo getActivityInfo(ComponentName component, long flags) throws NameNotFoundException {
        ActivityInfo info = NovaPackageManager.getInstance().getActivityInfo(component, (int) flags);
        if (info != null) return info;
        throw new NameNotFoundException(component != null ? component.toString() : "null");
    }

    public int checkPermission(String permName, String pkgName) { return PERMISSION_GRANTED; }
    public boolean hasSystemFeature(String name) { return false; }
    public boolean hasSystemFeature(String name, int version) { return false; }

    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        PackageInfo info = NovaPackageManager.getInstance().getPackageInfo(packageName, flags);
        if (info == null) throw new NameNotFoundException(packageName);
        return info;
    }

    public java.util.List<android.content.pm.ResolveInfo> queryIntentActivities(android.content.Intent intent, int flags) {
        return new java.util.ArrayList<>();
    }

    public android.content.pm.ResolveInfo resolveActivity(android.content.Intent intent, int flags) { return null; }
    public android.content.pm.ResolveInfo resolveService(android.content.Intent intent, int flags) { return null; }
    public java.util.List<android.content.pm.ResolveInfo> queryIntentServices(android.content.Intent intent, int flags) { return new java.util.ArrayList<>(); }
    public java.util.List<android.content.pm.ResolveInfo> queryBroadcastReceivers(android.content.Intent intent, int flags) { return new java.util.ArrayList<>(); }
    public java.util.List<PackageInfo> getInstalledPackages(int flags) { return new java.util.ArrayList<>(); }
    public java.util.List<ApplicationInfo> getInstalledApplications(int flags) { return new java.util.ArrayList<>(); }
    public CharSequence getApplicationLabel(ApplicationInfo info) { return info.packageName; }
    public android.graphics.drawable.Drawable getApplicationIcon(ApplicationInfo info) { return null; }
    public android.graphics.drawable.Drawable getApplicationIcon(String packageName) throws NameNotFoundException { return null; }
    public android.graphics.drawable.Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException { return null; }
    public android.graphics.drawable.Drawable getDefaultActivityIcon() { return null; }
    public android.content.res.Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException { return null; }
    public android.content.res.Resources getResourcesForApplication(String appPackageName) throws NameNotFoundException { return null; }
    public String getNameForUid(int uid) { return null; }
    public int getUidForSharedUser(String sharedUserName) throws NameNotFoundException { return 0; }
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException { return 0; }
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {}
    public int getApplicationEnabledSetting(String packageName) { return android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DEFAULT; }
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {}
    public int getComponentEnabledSetting(ComponentName componentName) { return COMPONENT_ENABLED_STATE_DEFAULT; }
    public android.content.pm.Signature[] getPackageSignatures(String packageName) throws NameNotFoundException { return new android.content.pm.Signature[0]; }

    public static final int COMPONENT_ENABLED_STATE_DEFAULT  = 0;
    public static final int COMPONENT_ENABLED_STATE_ENABLED  = 1;
    public static final int COMPONENT_ENABLED_STATE_DISABLED = 2;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_USER = 3;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = 4;

    public static final int DONT_KILL_APP = 0x00000001;
    public static final int FEATURE_CAMERA = 0x00000001;
    public static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash";
    public static final String FEATURE_TOUCHSCREEN = "android.hardware.touchscreen";
    public static final String FEATURE_WIFI = "android.hardware.wifi";
    public static final String FEATURE_BLUETOOTH = "android.hardware.bluetooth";

    public static class NameNotFoundException extends Exception {
        public NameNotFoundException() { super(); }
        public NameNotFoundException(String name) { super(name); }
    }
}
