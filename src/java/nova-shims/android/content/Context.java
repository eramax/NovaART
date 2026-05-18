package android.content;

import android.content.pm.NovaPackageManager;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.Display;
import android.view.WindowManager;
import android.os.Vibrator;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Context {
    public static final String WINDOW_SERVICE      = "window";
    public static final String VIBRATOR_SERVICE    = "vibrator";
    public static final String AUDIO_SERVICE       = "audio";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String SENSOR_SERVICE      = "sensor";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final int MODE_PRIVATE = 0;

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

    public AssetManager getAssets() {
        return sResources.getAssets();
    }

    public Object getSystemService(String name) {
        if (WINDOW_SERVICE.equals(name)) {
            return new WindowManager() {
                @Override
                public Display getDefaultDisplay() {
                    return new Display(960, 540);
                }
            };
        }
        if (VIBRATOR_SERVICE.equals(name)) {
            return new Vibrator() {};
        }
        return null;
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
