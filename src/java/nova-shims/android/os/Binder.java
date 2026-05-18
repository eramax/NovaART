package android.os;

public class Binder implements IBinder, IInterface {
    private IInterface owner;
    private String descriptor;

    public Binder() {}

    public void attachInterface(IInterface owner, String descriptor) {
        this.owner = owner;
        this.descriptor = descriptor;
    }

    @Override
    public String getInterfaceDescriptor() {
        return descriptor;
    }

    @Override
    public boolean pingBinder() {
        return true;
    }

    @Override
    public boolean isBinderAlive() {
        return true;
    }

    @Override
    public IInterface queryLocalInterface(String descriptor) {
        if (this.descriptor != null && this.descriptor.equals(descriptor)) {
            return owner;
        }
        return null;
    }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return onTransact(code, data, reply, flags);
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            if (reply != null) {
                reply.writeString(descriptor);
            }
            return true;
        }
        return false;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static native void blockUntilThreadAvailable();
    private static native long getNativeBBinderHolder();
    private static native long getNativeFinalizer();
    private native void destroy(long holder);
}
