package android.content;

import android.content.pm.NovaPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private static final ContentResolver sContentResolver = new ContentResolver();
    private static final PackageManager sPackageManager = NovaPackageManager.getInstance();
    private static final Resources sResources = Resources.getSystem();
    private static final Map<String, SharedPreferences> sSharedPreferences = new HashMap<>();
    private static String sCurrentPackageName = "";

    public ContentResolver getContentResolver() {
        return sContentResolver;
    }

    public Context getApplicationContext() {
        return this;
    }

    public PackageManager getPackageManager() {
        return sPackageManager;
    }

    public Resources getResources() {
        return sResources;
    }

    public File getFilesDir() {
        return ensureAppDir("files");
    }

    public File getCacheDir() {
        return ensureAppDir("cache");
    }

    public String getPackageName() {
        return sCurrentPackageName;
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        String key = name != null ? name : "";
        SharedPreferences prefs = sSharedPreferences.get(key);
        if (prefs == null) {
            prefs = new SimpleSharedPreferences();
            sSharedPreferences.put(key, prefs);
        }
        return prefs;
    }

    public void startActivity(Intent intent) {
    }

    public static void novaSetCurrentPackageName(String packageName) {
        sCurrentPackageName = packageName != null ? packageName : "";
    }

    private File ensureAppDir(String child) {
        String androidData = System.getenv("ANDROID_DATA");
        File baseDir = new File(androidData != null && !androidData.isEmpty() ? androidData : "output/android-data", "data");
        String packageName = sCurrentPackageName.isEmpty() ? "unknown" : sCurrentPackageName;
        File appDir = new File(baseDir, packageName);
        File dir = new File(appDir, child);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
