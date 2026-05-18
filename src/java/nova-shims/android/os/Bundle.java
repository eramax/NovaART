package android.os;

import java.util.HashMap;
import java.util.Set;

public final class Bundle implements Cloneable, Parcelable {

    public static final Bundle EMPTY = new Bundle();

    private final HashMap<String, Object> mMap = new HashMap<>();

    public Bundle() {}
    public Bundle(Bundle b) { if (b != null) mMap.putAll(b.mMap); }
    public Bundle(int capacity) {}

    public static Bundle forPair(String key, String value) {
        Bundle b = new Bundle(); b.putString(key, value); return b;
    }

    public boolean isEmpty() { return mMap.isEmpty(); }
    public int size() { return mMap.size(); }
    public void clear() { mMap.clear(); }
    public boolean containsKey(String key) { return mMap.containsKey(key); }
    public Object get(String key) { return mMap.get(key); }
    public void remove(String key) { mMap.remove(key); }
    public Set<String> keySet() { return mMap.keySet(); }

    public void putAll(Bundle bundle) { if (bundle != null) mMap.putAll(bundle.mMap); }

    public void putBoolean(String key, boolean value) { mMap.put(key, value); }
    public void putByte(String key, byte value)       { mMap.put(key, value); }
    public void putChar(String key, char value)       { mMap.put(key, value); }
    public void putShort(String key, short value)     { mMap.put(key, value); }
    public void putInt(String key, int value)         { mMap.put(key, value); }
    public void putLong(String key, long value)       { mMap.put(key, value); }
    public void putFloat(String key, float value)     { mMap.put(key, value); }
    public void putDouble(String key, double value)   { mMap.put(key, value); }
    public void putString(String key, String value)   { mMap.put(key, value); }
    public void putCharSequence(String key, CharSequence value) { mMap.put(key, value); }
    public void putParcelable(String key, Parcelable value) { mMap.put(key, value); }
    public void putBinder(String key, IBinder value)  { mMap.put(key, value); }
    public void putBundle(String key, Bundle value)   { mMap.put(key, value); }
    public void putIntArray(String key, int[] value)  { mMap.put(key, value); }
    public void putLongArray(String key, long[] value){ mMap.put(key, value); }
    public void putFloatArray(String key, float[] value) { mMap.put(key, value); }
    public void putDoubleArray(String key, double[] value) { mMap.put(key, value); }
    public void putStringArray(String key, String[] value) { mMap.put(key, value); }
    public void putByteArray(String key, byte[] value) { mMap.put(key, value); }
    public void putCharArray(String key, char[] value) { mMap.put(key, value); }
    public void putShortArray(String key, short[] value) { mMap.put(key, value); }
    public void putBooleanArray(String key, boolean[] value) { mMap.put(key, value); }
    public <T extends Parcelable> void putParcelableArray(String key, T[] value) { mMap.put(key, value); }
    public <T extends Parcelable> void putParcelableArrayList(String key, java.util.ArrayList<T> value) { mMap.put(key, value); }
    public void putStringArrayList(String key, java.util.ArrayList<String> value) { mMap.put(key, value); }
    public void putIntegerArrayList(String key, java.util.ArrayList<Integer> value) { mMap.put(key, value); }
    public void putSerializable(String key, java.io.Serializable value) { mMap.put(key, value); }

    public boolean getBoolean(String key) { return getBoolean(key, false); }
    public boolean getBoolean(String key, boolean def) { Object v = mMap.get(key); return v instanceof Boolean ? (Boolean)v : def; }
    public byte getByte(String key) { return getByte(key, (byte)0); }
    public byte getByte(String key, byte def) { Object v = mMap.get(key); return v instanceof Byte ? (Byte)v : def; }
    public char getChar(String key) { return getChar(key, (char)0); }
    public char getChar(String key, char def) { Object v = mMap.get(key); return v instanceof Character ? (Character)v : def; }
    public short getShort(String key) { return getShort(key, (short)0); }
    public short getShort(String key, short def) { Object v = mMap.get(key); return v instanceof Short ? (Short)v : def; }
    public int getInt(String key) { return getInt(key, 0); }
    public int getInt(String key, int def) { Object v = mMap.get(key); return v instanceof Integer ? (Integer)v : def; }
    public long getLong(String key) { return getLong(key, 0L); }
    public long getLong(String key, long def) { Object v = mMap.get(key); return v instanceof Long ? (Long)v : def; }
    public float getFloat(String key) { return getFloat(key, 0f); }
    public float getFloat(String key, float def) { Object v = mMap.get(key); return v instanceof Float ? (Float)v : def; }
    public double getDouble(String key) { return getDouble(key, 0.0); }
    public double getDouble(String key, double def) { Object v = mMap.get(key); return v instanceof Double ? (Double)v : def; }
    public String getString(String key) { return getString(key, null); }
    public String getString(String key, String def) { Object v = mMap.get(key); return v instanceof String ? (String)v : def; }
    public CharSequence getCharSequence(String key) { Object v = mMap.get(key); return v instanceof CharSequence ? (CharSequence)v : null; }
    public CharSequence getCharSequence(String key, CharSequence def) { CharSequence v = getCharSequence(key); return v != null ? v : def; }
    public Bundle getBundle(String key) { Object v = mMap.get(key); return v instanceof Bundle ? (Bundle)v : null; }
    public IBinder getBinder(String key) { Object v = mMap.get(key); return v instanceof IBinder ? (IBinder)v : null; }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T getParcelable(String key) { return (T) mMap.get(key); }
    @SuppressWarnings("unchecked")
    public Parcelable[] getParcelableArray(String key) { Object v = mMap.get(key); return v instanceof Parcelable[] ? (Parcelable[])v : null; }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> java.util.ArrayList<T> getParcelableArrayList(String key) { return (java.util.ArrayList<T>) mMap.get(key); }
    @SuppressWarnings("unchecked")
    public java.util.ArrayList<String> getStringArrayList(String key) { return (java.util.ArrayList<String>) mMap.get(key); }
    @SuppressWarnings("unchecked")
    public java.util.ArrayList<Integer> getIntegerArrayList(String key) { return (java.util.ArrayList<Integer>) mMap.get(key); }
    public int[] getIntArray(String key) { Object v = mMap.get(key); return v instanceof int[] ? (int[])v : null; }
    public long[] getLongArray(String key) { Object v = mMap.get(key); return v instanceof long[] ? (long[])v : null; }
    public float[] getFloatArray(String key) { Object v = mMap.get(key); return v instanceof float[] ? (float[])v : null; }
    public double[] getDoubleArray(String key) { Object v = mMap.get(key); return v instanceof double[] ? (double[])v : null; }
    public String[] getStringArray(String key) { Object v = mMap.get(key); return v instanceof String[] ? (String[])v : null; }
    public byte[] getByteArray(String key) { Object v = mMap.get(key); return v instanceof byte[] ? (byte[])v : null; }
    public char[] getCharArray(String key) { Object v = mMap.get(key); return v instanceof char[] ? (char[])v : null; }
    public short[] getShortArray(String key) { Object v = mMap.get(key); return v instanceof short[] ? (short[])v : null; }
    public boolean[] getBooleanArray(String key) { Object v = mMap.get(key); return v instanceof boolean[] ? (boolean[])v : null; }
    @SuppressWarnings("unchecked")
    public java.io.Serializable getSerializable(String key) { Object v = mMap.get(key); return v instanceof java.io.Serializable ? (java.io.Serializable)v : null; }

    @Override
    public Bundle clone() { return new Bundle(this); }

    @Override
    public String toString() { return "Bundle[" + mMap.toString() + "]"; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    public static final Parcelable.Creator<Bundle> CREATOR = new Parcelable.Creator<Bundle>() {
        @Override public Bundle createFromParcel(Parcel in) { return new Bundle(); }
        @Override public Bundle[] newArray(int size) { return new Bundle[size]; }
    };
}
