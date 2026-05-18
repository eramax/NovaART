package android.os;

public interface IBinder {
    int FIRST_CALL_TRANSACTION = 1;
    int LAST_CALL_TRANSACTION = 0x00ffffff;
    int INTERFACE_TRANSACTION = 0x5f4e5446;
    int FLAG_ONEWAY = 0x00000001;

    String getInterfaceDescriptor() throws RemoteException;
    boolean pingBinder();
    boolean isBinderAlive();
    IInterface queryLocalInterface(String descriptor);
    boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException;
}
