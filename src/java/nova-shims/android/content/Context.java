package android.content;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class Context {

    // System service names
    public static final String WINDOW_SERVICE          = "window";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String ACTIVITY_SERVICE        = "activity";
    public static final String POWER_SERVICE           = "power";
    public static final String ALARM_SERVICE           = "alarm";
    public static final String NOTIFICATION_SERVICE    = "notification";
    public static final String KEYGUARD_SERVICE        = "keyguard";
    public static final String LOCATION_SERVICE        = "location";
    public static final String SEARCH_SERVICE          = "search";
    public static final String SENSOR_SERVICE          = "sensor";
    public static final String STORAGE_SERVICE         = "storage";
    public static final String WALLPAPER_SERVICE       = "wallpaper";
    public static final String VIBRATOR_SERVICE        = "vibrator";
    public static final String CONNECTIVITY_SERVICE    = "connectivity";
    public static final String WIFI_SERVICE            = "wifi";
    public static final String WIFI_P2P_SERVICE        = "wifip2p";
    public static final String NSD_SERVICE             = "servicediscovery";
    public static final String AUDIO_SERVICE           = "audio";
    public static final String FINGERPRINT_SERVICE     = "fingerprint";
    public static final String MEDIA_ROUTER_SERVICE    = "media_router";
    public static final String TELEPHONY_SERVICE       = "phone";
    public static final String TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service";
    public static final String CARRIER_CONFIG_SERVICE  = "carrier_config";
    public static final String TELECOM_SERVICE         = "telecom";
    public static final String CLIPBOARD_SERVICE       = "clipboard";
    public static final String INPUT_METHOD_SERVICE    = "input_method";
    public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final String APPWIDGET_SERVICE       = "appwidget";
    public static final String DROPBOX_SERVICE         = "dropbox";
    public static final String DEVICE_POLICY_SERVICE   = "device_policy";
    public static final String UI_MODE_SERVICE         = "uimode";
    public static final String DOWNLOAD_SERVICE        = "download";
    public static final String NFC_SERVICE             = "nfc";
    public static final String BLUETOOTH_SERVICE       = "bluetooth";
    public static final String USB_SERVICE             = "usb";
    public static final String LAUNCHER_APPS_SERVICE   = "launcherapps";
    public static final String INPUT_SERVICE           = "input";
    public static final String DISPLAY_SERVICE         = "display";
    public static final String USER_SERVICE            = "user";
    public static final String PRINT_SERVICE           = "print";
    public static final String CONSUMER_IR_SERVICE     = "consumer_ir";
    public static final String APP_OPS_SERVICE         = "appops";
    public static final String CAMERA_SERVICE          = "camera";
    public static final String MEDIA_SESSION_SERVICE   = "media_session";
    public static final String BATTERY_SERVICE         = "batterymanager";
    public static final String JOB_SCHEDULER_SERVICE   = "jobscheduler";
    public static final String MEDIA_PROJECTION_SERVICE = "media_projection";
    public static final String MIDI_SERVICE            = "midi";
    public static final String HARDWARE_PROPERTIES_SERVICE = "hardware_properties";
    public static final String SHORTCUT_SERVICE        = "shortcut";
    public static final String NETWORK_STATS_SERVICE   = "netstats";
    public static final String ACCESSIBILITY_SERVICE   = "accessibility";
    public static final String CAPTIONING_SERVICE      = "captioning";
    public static final String RESTRICTIONS_SERVICE    = "restrictions";
    public static final String ACCOUNT_SERVICE         = "account";
    public static final String WIFI_AWARE_SERVICE      = "wifiaware";
    public static final String CROSS_PROFILE_APPS_SERVICE = "crossprofileapps";
    public static final String TEXT_CLASSIFICATION_SERVICE = "textclassification";
    public static final String CONTENT_CAPTURE_MANAGER_SERVICE = "content_capture";
    public static final String BIOMETRIC_SERVICE       = "biometric";
    public static final String BLOB_STORE_SERVICE      = "blob_store";
    public static final String BUGREPORT_SERVICE       = "bugreport";
    public static final String VPN_MANAGEMENT_SERVICE  = "vpn_management";
    public static final String STATS_MANAGER_SERVICE   = "stats";
    public static final String ROLE_SERVICE            = "role";
    public static final String PERMISSION_SERVICE      = "permission";

    // File modes
    public static final int MODE_PRIVATE              = 0x0000;
    public static final int MODE_WORLD_READABLE       = 0x0001;
    public static final int MODE_WORLD_WRITEABLE      = 0x0002;
    public static final int MODE_APPEND               = 0x8000;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 0x0008;
    public static final int MODE_NO_LOCALIZED_COLLATORS = 0x0010;
    public static final int BIND_AUTO_CREATE          = 0x0001;
    public static final int BIND_DEBUG_UNBIND         = 0x0002;
    public static final int BIND_NOT_FOREGROUND       = 0x0004;
    public static final int BIND_ABOVE_CLIENT         = 0x0008;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 0x0010;
    public static final int BIND_WAIVE_PRIORITY       = 0x0020;
    public static final int BIND_IMPORTANT            = 0x0040;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 0x0080;
    public static final int BIND_EXTERNAL_SERVICE     = 0x80000000;

    public static final int RECEIVER_EXPORTED         = 0x4;
    public static final int RECEIVER_NOT_EXPORTED     = 0x8;

    // Abstract core methods
    public abstract AssetManager getAssets();
    public abstract Resources getResources();
    public abstract PackageManager getPackageManager();
    public abstract ContentResolver getContentResolver();
    public abstract Looper getMainLooper();
    public abstract Context getApplicationContext();
    public abstract void setTheme(int resid);
    public abstract Resources.Theme getTheme();
    public abstract ClassLoader getClassLoader();
    public abstract String getPackageName();
    public abstract ApplicationInfo getApplicationInfo();
    public abstract String getPackageResourcePath();
    public abstract String getPackageCodePath();
    public abstract File getFilesDir();
    public abstract File getNoBackupFilesDir();
    public abstract File getExternalFilesDir(String type);
    public abstract File[] getExternalFilesDirs(String type);
    public abstract File getObbDir();
    public abstract File[] getObbDirs();
    public abstract File getCacheDir();
    public abstract File getCodeCacheDir();
    public abstract File getExternalCacheDir();
    public abstract File[] getExternalCacheDirs();
    public abstract File[] getExternalMediaDirs();
    public abstract File getDir(String name, int mode);
    public abstract File getDatabasePath(String name);
    public abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory);
    public abstract android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory, android.database.DatabaseErrorHandler errorHandler);
    public abstract boolean deleteDatabase(String name);
    public abstract String[] databaseList();
    public abstract FileInputStream openFileInput(String name) throws java.io.FileNotFoundException;
    public abstract FileOutputStream openFileOutput(String name, int mode) throws java.io.FileNotFoundException;
    public abstract boolean deleteFile(String name);
    public abstract File getFileStreamPath(String name);
    public abstract String[] fileList();
    public abstract android.content.SharedPreferences getSharedPreferences(String name, int mode);
    public abstract boolean moveSharedPreferencesFrom(Context sourceContext, String name);
    public abstract boolean deleteSharedPreferences(String name);
    public abstract Object getSystemService(String name);
    public abstract String getSystemServiceName(Class<?> serviceClass);
    public abstract boolean checkPermission(String permission, int pid, int uid);
    public abstract int checkSelfPermission(String permission);
    public abstract void enforcePermission(String permission, int pid, int uid, String message);
    public abstract void startActivity(Intent intent);
    public abstract void startActivity(Intent intent, Bundle options);
    public abstract void startActivities(Intent[] intents);
    public abstract void startActivities(Intent[] intents, Bundle options);
    public abstract void startBroadcast(Intent intent);
    public abstract void sendBroadcast(Intent intent);
    public abstract void sendBroadcast(Intent intent, String receiverPermission);
    public abstract void sendOrderedBroadcast(Intent intent, String receiverPermission);
    public abstract void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);
    public abstract void sendStickyBroadcast(Intent intent);
    public abstract void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras);
    public abstract void removeStickyBroadcast(Intent intent);
    public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);
    public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags);
    public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler);
    public abstract Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags);
    public abstract void unregisterReceiver(BroadcastReceiver receiver);
    public abstract ComponentName startService(Intent service);
    public abstract ComponentName startForegroundService(Intent service);
    public abstract boolean stopService(Intent service);
    public abstract boolean bindService(Intent service, ServiceConnection conn, int flags);
    public abstract void unbindService(ServiceConnection conn);
    public abstract Context createPackageContext(String packageName, int flags) throws android.content.pm.PackageManager.NameNotFoundException;
    public abstract Context createContextForSplit(String splitName) throws android.content.pm.PackageManager.NameNotFoundException;
    public abstract Context createConfigurationContext(android.content.res.Configuration overrideConfiguration);
    public abstract Context createDisplayContext(android.view.Display display);
    public abstract Context createDeviceProtectedStorageContext();
    public abstract Context createWindowContext(int type, Bundle options);
    public abstract Context createWindowContext(android.view.Display display, int type, Bundle options);
    public abstract boolean isDeviceProtectedStorage();
    public abstract boolean moveDatabaseFrom(Context sourceContext, String name);

    // Non-abstract helpers
    public <T> T getSystemService(Class<T> serviceClass) {
        Object s = getSystemService(getSystemServiceName(serviceClass));
        return serviceClass.isInstance(s) ? serviceClass.cast(s) : null;
    }

    public android.view.LayoutInflater getLayoutInflater() {
        return (android.view.LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public boolean checkCallingPermission(String permission) { return true; }
    public boolean checkCallingOrSelfPermission(String permission) { return true; }
    public boolean hasSystemFeature(String name) { return false; }

    public String getString(int resId) { return ""; }
    public String getString(int resId, Object... args) { return ""; }
    public CharSequence getText(int resId) { return ""; }
    public int getColor(int id) { return 0; }
    public android.graphics.drawable.Drawable getDrawable(int id) { return null; }

    public android.os.Handler getMainExecutor() { return null; }
    public java.util.concurrent.Executor getMainExecutorService() { return null; }

    // Nested interfaces/classes needed by Context references
    public interface ServiceConnection {
        void onServiceConnected(ComponentName name, android.os.IBinder service);
        void onServiceDisconnected(ComponentName name);
        default void onBindingDied(ComponentName name) {}
        default void onNullBinding(ComponentName name) {}
    }
}
