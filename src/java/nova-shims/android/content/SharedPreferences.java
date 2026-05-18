package android.content;

import java.util.Map;

public interface SharedPreferences {
    interface Editor {
        Editor putBoolean(String key, boolean value);
        Editor putInt(String key, int value);
        Editor putString(String key, String value);
        Editor apply();
        boolean commit();
    }

    boolean getBoolean(String key, boolean defValue);
    int getInt(String key, int defValue);
    String getString(String key, String defValue);
    Map<String, ?> getAll();
    Editor edit();
}
