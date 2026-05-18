package android.content.pm;

import android.content.ComponentName;

public final class NovaPackageManager extends PackageManager {
    private static final NovaPackageManager INSTANCE = new NovaPackageManager();
    private PackageInfo mCurrentPackageInfo;
    private ActivityInfo mCurrentActivityInfo;

    private NovaPackageManager() {
    }

    public static NovaPackageManager getInstance() {
        return INSTANCE;
    }

    public synchronized void setCurrentPackage(String packageName, String activityClass, String apkPath) {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        ActivityInfo activityInfo = new ActivityInfo();
        PackageInfo packageInfo = new PackageInfo();

        applicationInfo.packageName = packageName;
        applicationInfo.className = activityClass;
        applicationInfo.sourceDir = apkPath;

        activityInfo.applicationInfo = applicationInfo;
        activityInfo.name = activityClass;
        activityInfo.packageName = packageName;

        packageInfo.applicationInfo = applicationInfo;
        packageInfo.packageName = packageName;
        packageInfo.versionCode = 1;
        packageInfo.versionName = "1.0";

        mCurrentActivityInfo = activityInfo;
        mCurrentPackageInfo = packageInfo;
    }

    synchronized ActivityInfo getActivityInfo(ComponentName component, int flags) {
        if (component == null || mCurrentActivityInfo == null) {
            return null;
        }
        if (!component.getPackageName().equals(mCurrentActivityInfo.packageName)) {
            return null;
        }
        if (!component.getClassName().equals(mCurrentActivityInfo.name)) {
            return null;
        }
        return mCurrentActivityInfo;
    }

    public synchronized PackageInfo getPackageInfo(String packageName, int flags) {
        if (mCurrentPackageInfo == null) {
            return null;
        }
        if (packageName == null || !packageName.equals(mCurrentPackageInfo.packageName)) {
            return null;
        }
        return mCurrentPackageInfo;
    }

    public synchronized PackageInfo getCurrentPackageInfo() {
        return mCurrentPackageInfo;
    }
}
