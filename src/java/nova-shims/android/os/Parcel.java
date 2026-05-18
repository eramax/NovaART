package android.os;

import java.util.ArrayList;
import java.util.HashMap;

public final class Parcel {

    private static final ThreadLocal<Parcel> sPool = new ThreadLocal<>();

    private ArrayList<Object> mObjects = new ArrayList<>();
    private int mReadPos = 0;

    private Parcel() {}

    public static Parcel obtain() {
        Parcel p = sPool.get();
        if (p == null) p = new Parcel();
        p.mObjects.clear();
        p.mReadPos = 0;
        return p;
    }

    public void recycle() { mObjects.clear(); mReadPos = 0; sPool.set(this); }

    public int dataSize() { return mObjects.size() * 4; }
    public int dataPosition() { return mReadPos; }
    public int dataAvail() { return mObjects.size() - mReadPos; }
    public void setDataPosition(int pos) { mReadPos = pos; }

    // Write primitives
    public void writeInt(int val)         { mObjects.add(val); }
    public void writeLong(long val)       { mObjects.add(val); }
    public void writeFloat(float val)     { mObjects.add(val); }
    public void writeDouble(double val)   { mObjects.add(val); }
    public void writeString(String val)   { mObjects.add(val); }
    public void writeBoolean(boolean val) { mObjects.add(val); }
    public void writeByte(byte val)       { mObjects.add(val); }
    public void writeByteArray(byte[] b)  { mObjects.add(b); }
    public void writeIntArray(int[] a)    { mObjects.add(a); }
    public void writeLongArray(long[] a)  { mObjects.add(a); }
    public void writeFloatArray(float[] a){ mObjects.add(a); }
    public void writeStringArray(String[] a) { mObjects.add(a); }
    public void writeStringList(java.util.List<String> list) { mObjects.add(list != null ? new ArrayList<>(list) : null); }
    public void writeParcelable(Parcelable p, int flags) { mObjects.add(p); }
    public void writeParcelableArray(Parcelable[] p, int flags) { mObjects.add(p); }
    public void writeValue(Object v)      { mObjects.add(v); }
    public void writeBundle(Bundle b)     { mObjects.add(b); }
    public void writeStrongBinder(IBinder b) { mObjects.add(b); }
    public void writeStrongInterface(IInterface i) { mObjects.add(i != null ? i.asBinder() : null); }
    public void writeException(Exception e) { mObjects.add(e); }
    public void writeNoException() { mObjects.add(null); }
    public void writeMap(java.util.Map<?,?> m) { mObjects.add(m); }
    public <T extends Parcelable> void writeTypedList(java.util.List<T> list) { mObjects.add(list != null ? new ArrayList<>(list) : null); }
    public <T extends Parcelable> void writeTypedArray(T[] arr, int flags) { mObjects.add(arr); }
    public void writeSerializable(java.io.Serializable s) { mObjects.add(s); }
    public void writeCharSequence(CharSequence s) { mObjects.add(s != null ? s.toString() : null); }
    public void writeCharSequenceArray(CharSequence[] a) { mObjects.add(a); }
    public void writeList(java.util.List<?> list) { mObjects.add(list != null ? new ArrayList<>(list) : null); }
    public void writeArray(Object[] a) { mObjects.add(a); }

    // Read primitives
    private Object next() { return mReadPos < mObjects.size() ? mObjects.get(mReadPos++) : null; }
    public int readInt()          { Object v = next(); return v instanceof Integer ? (Integer)v : 0; }
    public long readLong()        { Object v = next(); return v instanceof Long ? (Long)v : 0L; }
    public float readFloat()      { Object v = next(); return v instanceof Float ? (Float)v : 0f; }
    public double readDouble()    { Object v = next(); return v instanceof Double ? (Double)v : 0.0; }
    public String readString()    { Object v = next(); return v instanceof String ? (String)v : null; }
    public boolean readBoolean()  { Object v = next(); return v instanceof Boolean ? (Boolean)v : false; }
    public byte readByte()        { Object v = next(); return v instanceof Byte ? (Byte)v : 0; }
    public byte[] createByteArray() { Object v = next(); return v instanceof byte[] ? (byte[])v : null; }
    public int[] createIntArray() { Object v = next(); return v instanceof int[] ? (int[])v : null; }
    public long[] createLongArray(){ Object v = next(); return v instanceof long[] ? (long[])v : null; }
    public float[] createFloatArray(){ Object v = next(); return v instanceof float[] ? (float[])v : null; }
    public String[] createStringArray(){ Object v = next(); return v instanceof String[] ? (String[])v : null; }
    @SuppressWarnings("unchecked")
    public ArrayList<String> createStringArrayList() { Object v = next(); return v instanceof ArrayList ? (ArrayList<String>)v : new ArrayList<>(); }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T readParcelable(ClassLoader loader) { return (T) next(); }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T[] readParcelableArray(ClassLoader loader) { return (T[]) next(); }
    public Object readValue(ClassLoader loader) { return next(); }
    public Bundle readBundle() { Object v = next(); return v instanceof Bundle ? (Bundle)v : null; }
    public Bundle readBundle(ClassLoader loader) { return readBundle(); }
    public IBinder readStrongBinder() { Object v = next(); return v instanceof IBinder ? (IBinder)v : null; }
    public void readException() {}
    public int readExceptionCode() { return 0; }
    @SuppressWarnings("unchecked")
    public HashMap readHashMap(ClassLoader loader) { Object v = next(); return v instanceof HashMap ? (HashMap)v : new HashMap<>(); }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> ArrayList<T> createTypedArrayList(Parcelable.Creator<T> c) { Object v = next(); return v instanceof ArrayList ? (ArrayList<T>)v : new ArrayList<>(); }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> T[] createTypedArray(Parcelable.Creator<T> c) { return (T[]) next(); }
    @SuppressWarnings("unchecked")
    public <T extends Parcelable> void readTypedList(java.util.List<T> list, Parcelable.Creator<T> c) {}
    public java.io.Serializable readSerializable() { Object v = next(); return v instanceof java.io.Serializable ? (java.io.Serializable)v : null; }
    public CharSequence readCharSequence() { Object v = next(); return v instanceof CharSequence ? (CharSequence)v : null; }
    @SuppressWarnings("unchecked")
    public ArrayList readArrayList(ClassLoader loader) { Object v = next(); return v instanceof ArrayList ? (ArrayList)v : new ArrayList<>(); }
    public Object[] readArray(ClassLoader loader) { return (Object[]) next(); }
    public void readList(java.util.List outVal, ClassLoader loader) {}
    public void readMap(java.util.Map outVal, ClassLoader loader) {}

    public void appendFrom(Parcel parcel, int offset, int length) {}
    public byte[] marshall() { return new byte[0]; }
    public void unmarshall(byte[] data, int offset, int length) {}
    public boolean hasFileDescriptors() { return false; }
    public boolean hasUnixFds() { return false; }
    public static void setAppOpsService(Object i) {}
}
