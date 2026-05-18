package android.content;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ContextWrapper extends Context {

    protected Context mBase;

    public ContextWrapper(Context base) { mBase = base; }

    protected void attachBaseContext(Context base) { mBase = base; }
    public Context getBaseContext() { return mBase; }

    @Override public AssetManager getAssets() { return mBase != null ? mBase.getAssets() : null; }
    @Override public Resources getResources() { return mBase != null ? mBase.getResources() : null; }
    @Override public PackageManager getPackageManager() { return mBase != null ? mBase.getPackageManager() : null; }
    @Override public ContentResolver getContentResolver() { return mBase != null ? mBase.getContentResolver() : null; }
    @Override public Looper getMainLooper() { return mBase != null ? mBase.getMainLooper() : Looper.getMainLooper(); }
    @Override public Context getApplicationContext() { return mBase != null ? mBase.getApplicationContext() : this; }
    @Override public void setTheme(int resid) { if (mBase != null) mBase.setTheme(resid); }
    @Override public Resources.Theme getTheme() { return mBase != null ? mBase.getTheme() : null; }
    @Override public ClassLoader getClassLoader() { return mBase != null ? mBase.getClassLoader() : getClass().getClassLoader(); }
    @Override public String getPackageName() { return mBase != null ? mBase.getPackageName() : ""; }
    @Override public ApplicationInfo getApplicationInfo() { return mBase != null ? mBase.getApplicationInfo() : null; }
    @Override public String getPackageResourcePath() { return mBase != null ? mBase.getPackageResourcePath() : ""; }
    @Override public String getPackageCodePath() { return mBase != null ? mBase.getPackageCodePath() : ""; }
    @Override public File getFilesDir() { return mBase != null ? mBase.getFilesDir() : null; }
    @Override public File getNoBackupFilesDir() { return mBase != null ? mBase.getNoBackupFilesDir() : null; }
    @Override public File getExternalFilesDir(String type) { return mBase != null ? mBase.getExternalFilesDir(type) : null; }
    @Override public File[] getExternalFilesDirs(String type) { return mBase != null ? mBase.getExternalFilesDirs(type) : new File[0]; }
    @Override public File getObbDir() { return mBase != null ? mBase.getObbDir() : null; }
    @Override public File[] getObbDirs() { return mBase != null ? mBase.getObbDirs() : new File[0]; }
    @Override public File getCacheDir() { return mBase != null ? mBase.getCacheDir() : null; }
    @Override public File getCodeCacheDir() { return mBase != null ? mBase.getCodeCacheDir() : null; }
    @Override public File getExternalCacheDir() { return mBase != null ? mBase.getExternalCacheDir() : null; }
    @Override public File[] getExternalCacheDirs() { return mBase != null ? mBase.getExternalCacheDirs() : new File[0]; }
    @Override public File[] getExternalMediaDirs() { return mBase != null ? mBase.getExternalMediaDirs() : new File[0]; }
    @Override public File getDir(String name, int mode) { return mBase != null ? mBase.getDir(name, mode) : null; }
    @Override public File getDatabasePath(String name) { return mBase != null ? mBase.getDatabasePath(name) : null; }
    @Override public android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory) { return mBase != null ? mBase.openOrCreateDatabase(name, mode, factory) : null; }
    @Override public android.database.sqlite.SQLiteDatabase openOrCreateDatabase(String name, int mode, android.database.sqlite.SQLiteDatabase.CursorFactory factory, android.database.DatabaseErrorHandler errorHandler) { return mBase != null ? mBase.openOrCreateDatabase(name, mode, factory, errorHandler) : null; }
    @Override public boolean deleteDatabase(String name) { return mBase != null && mBase.deleteDatabase(name); }
    @Override public String[] databaseList() { return mBase != null ? mBase.databaseList() : new String[0]; }
    @Override public FileInputStream openFileInput(String name) throws java.io.FileNotFoundException { return mBase != null ? mBase.openFileInput(name) : null; }
    @Override public FileOutputStream openFileOutput(String name, int mode) throws java.io.FileNotFoundException { return mBase != null ? mBase.openFileOutput(name, mode) : null; }
    @Override public boolean deleteFile(String name) { return mBase != null && mBase.deleteFile(name); }
    @Override public File getFileStreamPath(String name) { return mBase != null ? mBase.getFileStreamPath(name) : null; }
    @Override public String[] fileList() { return mBase != null ? mBase.fileList() : new String[0]; }
    @Override public android.content.SharedPreferences getSharedPreferences(String name, int mode) { return mBase != null ? mBase.getSharedPreferences(name, mode) : null; }
    @Override public boolean moveSharedPreferencesFrom(Context sourceContext, String name) { return mBase != null && mBase.moveSharedPreferencesFrom(sourceContext, name); }
    @Override public boolean deleteSharedPreferences(String name) { return mBase != null && mBase.deleteSharedPreferences(name); }
    @Override public Object getSystemService(String name) { return mBase != null ? mBase.getSystemService(name) : null; }
    @Override public String getSystemServiceName(Class<?> serviceClass) { return mBase != null ? mBase.getSystemServiceName(serviceClass) : null; }
    @Override public boolean checkPermission(String permission, int pid, int uid) { return mBase == null || mBase.checkPermission(permission, pid, uid); }
    @Override public int checkSelfPermission(String permission) { return mBase != null ? mBase.checkSelfPermission(permission) : android.content.pm.PackageManager.PERMISSION_GRANTED; }
    @Override public void enforcePermission(String permission, int pid, int uid, String message) { if (mBase != null) mBase.enforcePermission(permission, pid, uid, message); }
    @Override public void startActivity(Intent intent) { if (mBase != null) mBase.startActivity(intent); }
    @Override public void startActivity(Intent intent, Bundle options) { if (mBase != null) mBase.startActivity(intent, options); }
    @Override public void startActivities(Intent[] intents) { if (mBase != null) mBase.startActivities(intents); }
    @Override public void startActivities(Intent[] intents, Bundle options) { if (mBase != null) mBase.startActivities(intents, options); }
    @Override public void startBroadcast(Intent intent) { if (mBase != null) mBase.startBroadcast(intent); }
    @Override public void sendBroadcast(Intent intent) { if (mBase != null) mBase.sendBroadcast(intent); }
    @Override public void sendBroadcast(Intent intent, String receiverPermission) { if (mBase != null) mBase.sendBroadcast(intent, receiverPermission); }
    @Override public void sendOrderedBroadcast(Intent intent, String receiverPermission) { if (mBase != null) mBase.sendOrderedBroadcast(intent, receiverPermission); }
    @Override public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { if (mBase != null) mBase.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras); }
    @Override public void sendStickyBroadcast(Intent intent) { if (mBase != null) mBase.sendStickyBroadcast(intent); }
    @Override public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) { if (mBase != null) mBase.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras); }
    @Override public void removeStickyBroadcast(Intent intent) { if (mBase != null) mBase.removeStickyBroadcast(intent); }
    @Override public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) { return mBase != null ? mBase.registerReceiver(receiver, filter) : null; }
    @Override public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) { return mBase != null ? mBase.registerReceiver(receiver, filter, flags) : null; }
    @Override public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) { return mBase != null ? mBase.registerReceiver(receiver, filter, broadcastPermission, scheduler) : null; }
    @Override public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) { return mBase != null ? mBase.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags) : null; }
    @Override public void unregisterReceiver(BroadcastReceiver receiver) { if (mBase != null) mBase.unregisterReceiver(receiver); }
    @Override public ComponentName startService(Intent service) { return mBase != null ? mBase.startService(service) : null; }
    @Override public ComponentName startForegroundService(Intent service) { return mBase != null ? mBase.startForegroundService(service) : null; }
    @Override public boolean stopService(Intent service) { return mBase != null && mBase.stopService(service); }
    @Override public boolean bindService(Intent service, ServiceConnection conn, int flags) { return mBase != null && mBase.bindService(service, conn, flags); }
    @Override public void unbindService(ServiceConnection conn) { if (mBase != null) mBase.unbindService(conn); }
    @Override public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException { return mBase != null ? mBase.createPackageContext(packageName, flags) : this; }
    @Override public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException { return mBase != null ? mBase.createContextForSplit(splitName) : this; }
    @Override public Context createConfigurationContext(Configuration overrideConfiguration) { return mBase != null ? mBase.createConfigurationContext(overrideConfiguration) : this; }
    @Override public Context createDisplayContext(android.view.Display display) { return mBase != null ? mBase.createDisplayContext(display) : this; }
    @Override public Context createDeviceProtectedStorageContext() { return mBase != null ? mBase.createDeviceProtectedStorageContext() : this; }
    @Override public Context createWindowContext(int type, Bundle options) { return mBase != null ? mBase.createWindowContext(type, options) : this; }
    @Override public Context createWindowContext(android.view.Display display, int type, Bundle options) { return mBase != null ? mBase.createWindowContext(display, type, options) : this; }
    @Override public boolean isDeviceProtectedStorage() { return mBase != null && mBase.isDeviceProtectedStorage(); }
    @Override public boolean moveDatabaseFrom(Context sourceContext, String name) { return mBase != null && mBase.moveDatabaseFrom(sourceContext, name); }

    @Override public String toString() { return getClass().getSimpleName() + "{base=" + mBase + "}"; }
}
