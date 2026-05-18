package android.os;

public interface IBinder {
    String getInterfaceDescriptor() throws RemoteException;
    boolean pingBinder();
    boolean isBinderAlive();
    IInterface queryLocalInterface(String descriptor);
    boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException;
    void dump(java.io.FileDescriptor fd, String[] args) throws RemoteException;
    void dumpAsync(java.io.FileDescriptor fd, String[] args) throws RemoteException;
    void shellCommand(java.io.FileDescriptor in, java.io.FileDescriptor out,
            java.io.FileDescriptor err, String[] args, Object shellCallback,
            Object resultReceiver) throws RemoteException;
    void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException;
    boolean unlinkToDeath(DeathRecipient recipient, int flags);

    interface DeathRecipient {
        void binderDied();
        default void binderDied(IBinder who) { binderDied(); }
    }

    int FIRST_CALL_TRANSACTION  = 0x00000001;
    int LAST_CALL_TRANSACTION   = 0x00FFFFFF;
    int PING_TRANSACTION        = ('_' << 24) | ('P' << 16) | ('N' << 8) | 'G';
    int DUMP_TRANSACTION        = ('_' << 24) | ('D' << 16) | ('M' << 8) | 'P';
    int SHELL_COMMAND_TRANSACTION = ('_' << 24) | ('C' << 16) | ('M' << 8) | 'D';
    int INTERFACE_TRANSACTION   = ('_' << 24) | ('N' << 16) | ('T' << 8) | 'F';
    int FLAG_ONEWAY             = 0x00000001;
    int FLAG_CLEAR_BUF          = 0x00000020;
}
