package android.os;

public class Binder implements IBinder {

    private String mDescriptor = "";

    public Binder() {}
    public Binder(String descriptor) { mDescriptor = descriptor != null ? descriptor : ""; }

    public static long clearCallingIdentity() { return 0L; }
    public static void restoreCallingIdentity(long token) {}
    public static int getCallingPid() { return android.os.Process.myPid(); }
    public static int getCallingUid() { return android.os.Process.myUid(); }
    public static int getCallingUidOrThrow() { return android.os.Process.myUid(); }
    public static android.os.UserHandle getCallingUserHandle() { return android.os.UserHandle.getUserHandleForUid(getCallingUid()); }
    public static void setThreadStrictModePolicy(int policyMask) {}
    public static int getThreadStrictModePolicy() { return 0; }
    public static void joinThreadPool() {}
    public static boolean isProxy(IInterface iface) { return false; }

    public void attachInterface(IInterface owner, String descriptor) { mDescriptor = descriptor != null ? descriptor : ""; }

    public final IInterface queryLocalInterface(String descriptor) {
        if (mDescriptor.equals(descriptor)) return null;
        return null;
    }

    public String getInterfaceDescriptor() { return mDescriptor; }

    @Override public boolean pingBinder() { return true; }
    @Override public boolean isBinderAlive() { return true; }

    @Override
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return onTransact(code, data, reply, flags);
    }

    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            if (reply != null) reply.writeString(mDescriptor);
            return true;
        }
        if (code == PING_TRANSACTION) { return true; }
        return false;
    }

    @Override public void dump(java.io.FileDescriptor fd, String[] args) throws RemoteException {}
    @Override public void dumpAsync(java.io.FileDescriptor fd, String[] args) throws RemoteException {}
    @Override public void shellCommand(java.io.FileDescriptor in, java.io.FileDescriptor out,
            java.io.FileDescriptor err, String[] args, Object shellCallback, Object resultReceiver) throws RemoteException {}
    @Override public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException {}
    @Override public boolean unlinkToDeath(DeathRecipient recipient, int flags) { return true; }

    public static void allowBlocking(IBinder binder) {}
    public static IBinder withStrictModePolicy(IBinder binder, int policy) { return binder; }

    protected void finalize() throws Throwable { super.finalize(); }

    public static void setProxyTransactListener(Object listener) {}

    @Override public String toString() { return "Binder[" + mDescriptor + "]"; }
}
