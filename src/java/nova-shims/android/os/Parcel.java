package android.os;

import java.util.ArrayList;

public final class Parcel {
    private final ArrayList<Object> data = new ArrayList<>();
    private int position = 0;

    private Parcel() {}

    public static Parcel obtain() {
        return new Parcel();
    }

    public void recycle() {
        data.clear();
        position = 0;
    }

    public void writeInterfaceToken(String descriptor) {
        writeString(descriptor);
    }

    public void enforceInterface(String descriptor) {
        String actual = readString();
        if (descriptor == null ? actual != null : !descriptor.equals(actual)) {
            throw new RuntimeException("Binder descriptor mismatch: expected " + descriptor + ", got " + actual);
        }
    }

    public void writeNoException() {
        writeInt(0);
    }

    public void readException() throws RemoteException {
        int code = readInt();
        if (code != 0) {
            throw new RemoteException("remote exception code=" + code);
        }
    }

    public void writeInt(int value) {
        data.add(value);
    }

    public int readInt() {
        return ((Integer) data.get(position++)).intValue();
    }

    public void writeLong(long value) {
        data.add(value);
    }

    public long readLong() {
        return ((Long) data.get(position++)).longValue();
    }

    public void writeFloat(float value) {
        data.add(value);
    }

    public float readFloat() {
        return ((Float) data.get(position++)).floatValue();
    }

    public void writeString(String value) {
        data.add(value);
    }

    public String readString() {
        return (String) data.get(position++);
    }

    public void writeStrongBinder(IBinder binder) {
        data.add(binder);
    }

    public IBinder readStrongBinder() {
        return (IBinder) data.get(position++);
    }

    public void writeStrongInterface(IInterface iface) {
        writeStrongBinder(iface != null ? iface.asBinder() : null);
    }
}
