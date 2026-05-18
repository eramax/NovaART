/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full/out/host/linux-x86/bin/aidl --lang=java -o /mnt/mydata/projects2/qos/deps/NovaART/src/generated/aidl/nova -I /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova/android/view/IWindow.aidl
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package android.view;
/** Minimal token interface used by NovaART window session calls. */
public interface IWindow extends android.os.IInterface
{
  /** Default implementation for IWindow. */
  public static class Default implements android.view.IWindow
  {
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements android.view.IWindow
  {
    /** Construct the stub and attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an android.view.IWindow interface,
     * generating a proxy if needed.
     */
    public static android.view.IWindow asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof android.view.IWindow))) {
        return ((android.view.IWindow)iin);
      }
      return new android.view.IWindow.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements android.view.IWindow
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
    }
    /** @hide */
    public static final java.lang.String DESCRIPTOR = "android.view.IWindow";
  }
}
