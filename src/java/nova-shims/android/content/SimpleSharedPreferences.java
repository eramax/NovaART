package android.content;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SimpleSharedPreferences implements SharedPreferences {
    private final Map<String, Object> mValues = new HashMap<>();

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object value = mValues.get(key);
        return value instanceof Boolean ? (Boolean) value : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        Object value = mValues.get(key);
        return value instanceof Integer ? (Integer) value : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        Object value = mValues.get(key);
        return value instanceof String ? (String) value : defValue;
    }

    @Override
    public Map<String, ?> getAll() {
        return Collections.unmodifiableMap(mValues);
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    private final class EditorImpl implements Editor {
        private final Map<String, Object> mPending = new HashMap<>();

        @Override
        public Editor putBoolean(String key, boolean value) {
            mPending.put(key, value);
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mPending.put(key, value);
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            mPending.put(key, value);
            return this;
        }

        @Override
        public Editor apply() {
            commit();
            return this;
        }

        @Override
        public boolean commit() {
            mValues.putAll(mPending);
            mPending.clear();
            return true;
        }
    }
}
