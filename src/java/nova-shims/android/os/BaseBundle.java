package android.os;

import java.util.HashMap;
import java.util.Map;

public class BaseBundle {
    protected final Map<String, Object> mMap = new HashMap<>();

    public BaseBundle() {
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(key);
    }

    public Object get(String key) {
        return mMap.get(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = mMap.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        Object value = mMap.get(key);
        return value instanceof Integer ? (Integer) value : defaultValue;
    }

    public String getString(String key) {
        Object value = mMap.get(key);
        return value instanceof String ? (String) value : null;
    }

    public void putBoolean(String key, boolean value) {
        mMap.put(key, value);
    }

    public void putInt(String key, int value) {
        mMap.put(key, value);
    }

    public void putString(String key, String value) {
        mMap.put(key, value);
    }
}
