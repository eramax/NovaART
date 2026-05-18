package android.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentValues {
    private final HashMap<String, Object> mValues = new HashMap<>();
    public ContentValues() {}
    public ContentValues(int size) {}
    public void put(String key, String value) { mValues.put(key, value); }
    public void put(String key, Integer value) { mValues.put(key, value); }
    public void put(String key, Long value) { mValues.put(key, value); }
    public void put(String key, Float value) { mValues.put(key, value); }
    public void put(String key, Double value) { mValues.put(key, value); }
    public void put(String key, Boolean value) { mValues.put(key, value); }
    public void put(String key, byte[] value) { mValues.put(key, value); }
    public void putNull(String key) { mValues.put(key, null); }
    public Object get(String key) { return mValues.get(key); }
    public String getAsString(String key) { Object v = mValues.get(key); return v instanceof String ? (String)v : null; }
    public Integer getAsInteger(String key) { Object v = mValues.get(key); return v instanceof Integer ? (Integer)v : null; }
    public Long getAsLong(String key) { Object v = mValues.get(key); return v instanceof Long ? (Long)v : null; }
    public boolean containsKey(String key) { return mValues.containsKey(key); }
    public void remove(String key) { mValues.remove(key); }
    public void clear() { mValues.clear(); }
    public int size() { return mValues.size(); }
    public Set<Map.Entry<String, Object>> valueSet() { return mValues.entrySet(); }
}
