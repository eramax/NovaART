package android.os;

import java.util.ArrayList;

/**
 * Minimal NovaART shim for android.os.SystemProperties.
 *
 * This preserves the Java and JNI entry points the current runtime stubs
 * expect without importing the full hidden-framework implementation.
 */
public final class SystemProperties {
    public static final int PROP_NAME_MAX = Integer.MAX_VALUE;
    public static final int PROP_VALUE_MAX = 91;

    private static final ArrayList<Runnable> sChangeCallbacks = new ArrayList<>();

    private SystemProperties() {}

    public static String get(String key) {
        return native_get(key, "");
    }

    public static String get(String key, String def) {
        return native_get(key, def);
    }

    public static int getInt(String key, int def) {
        return native_getInt(key, def);
    }

    public static long getLong(String key, long def) {
        return native_getLong(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        return native_getBoolean(key, def);
    }

    public static void set(String key, String val) {
        native_set(key, val);
    }

    public static void addChangeCallback(Runnable callback) {
        if (callback == null) {
            throw new NullPointerException("callback");
        }
        synchronized (sChangeCallbacks) {
            if (sChangeCallbacks.isEmpty()) {
                native_addChangeCallback();
            }
            sChangeCallbacks.add(callback);
        }
    }

    public static void removeChangeCallback(Runnable callback) {
        synchronized (sChangeCallbacks) {
            sChangeCallbacks.remove(callback);
        }
    }

    public static void reportSyspropChanged() {
        ArrayList<Runnable> callbacks;
        synchronized (sChangeCallbacks) {
            callbacks = new ArrayList<>(sChangeCallbacks);
        }
        for (Runnable callback : callbacks) {
            callback.run();
        }
    }

    private static native String native_get(String key, String def);
    private static native String native_getString(String key, String def);
    private static native int native_getInt(String key, int def);
    private static native long native_getLong(String key, long def);
    private static native boolean native_getBoolean(String key, boolean def);
    private static native void native_set(String key, String val);
    private static native void native_addChangeCallback();
}
