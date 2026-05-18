package android.app;

import android.content.pm.IPackageManager;

public final class AppGlobals {
    private static volatile IPackageManager sPackageManager;

    private AppGlobals() {}

    public static IPackageManager getPackageManager() {
        return sPackageManager;
    }

    public static void setPackageManager(IPackageManager packageManager) {
        sPackageManager = packageManager;
    }
}
