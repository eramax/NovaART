/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full/out/host/linux-x86/bin/aidl --lang=java -o /mnt/mydata/projects2/qos/deps/NovaART/src/generated/aidl/nova -I /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova/android/view/IWindowManager.aidl
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package android.view;
/** Minimal NovaART window manager shape for local in-process dispatch. */
public interface IWindowManager extends android.os.IInterface
{
  /** Default implementation for IWindowManager. */
  public static class Default implements android.view.IWindowManager
  {
    @Override public android.view.IWindowSession openSession(android.view.IWindowSessionCallback callback) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements android.view.IWindowManager
  {
    /** Construct the stub and attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an android.view.IWindowManager interface,
     * generating a proxy if needed.
     */
    public static android.view.IWindowManager asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof android.view.IWindowManager))) {
        return ((android.view.IWindowManager)iin);
      }
      return new android.view.IWindowManager.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_openSession:
        {
          android.view.IWindowSessionCallback _arg0;
          _arg0 = android.view.IWindowSessionCallback.Stub.asInterface(data.readStrongBinder());
          android.view.IWindowSession _result = this.openSession(_arg0);
          reply.writeNoException();
          reply.writeStrongInterface(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements android.view.IWindowManager
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public android.view.IWindowSession openSession(android.view.IWindowSessionCallback callback) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.view.IWindowSession _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(callback);
          boolean _status = mRemote.transact(Stub.TRANSACTION_openSession, _data, _reply, 0);
          _reply.readException();
          _result = android.view.IWindowSession.Stub.asInterface(_reply.readStrongBinder());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    /** @hide */
    public static final java.lang.String DESCRIPTOR = "android.view.IWindowManager";
    static final int TRANSACTION_openSession = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
  }
  public android.view.IWindowSession openSession(android.view.IWindowSessionCallback callback) throws android.os.RemoteException;
}
