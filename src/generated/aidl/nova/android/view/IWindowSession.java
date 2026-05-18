/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /mnt/mydata/projects2/qos/deps/NovaART/deps/aosp-full/out/host/linux-x86/bin/aidl --lang=java -o /mnt/mydata/projects2/qos/deps/NovaART/src/generated/aidl/nova -I /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova /mnt/mydata/projects2/qos/deps/NovaART/src/aidl/nova/android/view/IWindowSession.aidl
 *
 * DO NOT CHECK THIS FILE INTO A CODE TREE (e.g. git, etc..).
 * ALWAYS GENERATE THIS FILE FROM UPDATED AIDL COMPILER
 * AS A BUILD INTERMEDIATE ONLY. THIS IS NOT SOURCE CODE.
 */
package android.view;
/** Minimal NovaART window session shape for local in-process dispatch. */
public interface IWindowSession extends android.os.IInterface
{
  /** Default implementation for IWindowSession. */
  public static class Default implements android.view.IWindowSession
  {
    @Override public int addToDisplay(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int viewVisibility, int layerStackId, int requestedVisibleTypes, android.view.InputChannel outInputChannel, android.view.WindowRelayoutResult result) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void remove(android.os.IBinder clientToken) throws android.os.RemoteException
    {
    }
    @Override public int relayout(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewVisibility, int flags, int seq, int lastSyncSeqId, android.view.WindowRelayoutResult outRelayoutResult, android.view.SurfaceControl outSurface) throws android.os.RemoteException
    {
      return 0;
    }
    @Override public void finishDrawing(android.view.IWindow window, android.view.SurfaceControl.Transaction postDrawTransaction, int seqId) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements android.view.IWindowSession
  {
    /** Construct the stub and attach it to the interface. */
    @SuppressWarnings("this-escape")
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an android.view.IWindowSession interface,
     * generating a proxy if needed.
     */
    public static android.view.IWindowSession asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof android.view.IWindowSession))) {
        return ((android.view.IWindowSession)iin);
      }
      return new android.view.IWindowSession.Stub.Proxy(obj);
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
        case TRANSACTION_addToDisplay:
        {
          android.view.IWindow _arg0;
          _arg0 = android.view.IWindow.Stub.asInterface(data.readStrongBinder());
          android.view.WindowManager.LayoutParams _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.view.WindowManager.LayoutParams.CREATOR);
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          android.view.InputChannel _arg5;
          _arg5 = new android.view.InputChannel();
          android.view.WindowRelayoutResult _arg6;
          _arg6 = new android.view.WindowRelayoutResult();
          int _result = this.addToDisplay(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
          reply.writeNoException();
          reply.writeInt(_result);
          _Parcel.writeTypedObject(reply, _arg5, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          _Parcel.writeTypedObject(reply, _arg6, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_remove:
        {
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.remove(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_relayout:
        {
          android.view.IWindow _arg0;
          _arg0 = android.view.IWindow.Stub.asInterface(data.readStrongBinder());
          android.view.WindowManager.LayoutParams _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.view.WindowManager.LayoutParams.CREATOR);
          int _arg2;
          _arg2 = data.readInt();
          int _arg3;
          _arg3 = data.readInt();
          int _arg4;
          _arg4 = data.readInt();
          int _arg5;
          _arg5 = data.readInt();
          int _arg6;
          _arg6 = data.readInt();
          int _arg7;
          _arg7 = data.readInt();
          android.view.WindowRelayoutResult _arg8;
          _arg8 = new android.view.WindowRelayoutResult();
          android.view.SurfaceControl _arg9;
          _arg9 = new android.view.SurfaceControl();
          int _result = this.relayout(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
          reply.writeNoException();
          reply.writeInt(_result);
          _Parcel.writeTypedObject(reply, _arg8, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          _Parcel.writeTypedObject(reply, _arg9, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          break;
        }
        case TRANSACTION_finishDrawing:
        {
          android.view.IWindow _arg0;
          _arg0 = android.view.IWindow.Stub.asInterface(data.readStrongBinder());
          android.view.SurfaceControl.Transaction _arg1;
          _arg1 = _Parcel.readTypedObject(data, android.view.SurfaceControl.Transaction.CREATOR);
          int _arg2;
          _arg2 = data.readInt();
          this.finishDrawing(_arg0, _arg1, _arg2);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
    }
    private static class Proxy implements android.view.IWindowSession
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
      @Override public int addToDisplay(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int viewVisibility, int layerStackId, int requestedVisibleTypes, android.view.InputChannel outInputChannel, android.view.WindowRelayoutResult result) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(window);
          _Parcel.writeTypedObject(_data, attrs, 0);
          _data.writeInt(viewVisibility);
          _data.writeInt(layerStackId);
          _data.writeInt(requestedVisibleTypes);
          boolean _status = mRemote.transact(Stub.TRANSACTION_addToDisplay, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
          if ((0!=_reply.readInt())) {
            outInputChannel.readFromParcel(_reply);
          }
          if ((0!=_reply.readInt())) {
            result.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void remove(android.os.IBinder clientToken) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(clientToken);
          boolean _status = mRemote.transact(Stub.TRANSACTION_remove, _data, _reply, 0);
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public int relayout(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewVisibility, int flags, int seq, int lastSyncSeqId, android.view.WindowRelayoutResult outRelayoutResult, android.view.SurfaceControl outSurface) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(window);
          _Parcel.writeTypedObject(_data, attrs, 0);
          _data.writeInt(requestedWidth);
          _data.writeInt(requestedHeight);
          _data.writeInt(viewVisibility);
          _data.writeInt(flags);
          _data.writeInt(seq);
          _data.writeInt(lastSyncSeqId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_relayout, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
          if ((0!=_reply.readInt())) {
            outRelayoutResult.readFromParcel(_reply);
          }
          if ((0!=_reply.readInt())) {
            outSurface.readFromParcel(_reply);
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void finishDrawing(android.view.IWindow window, android.view.SurfaceControl.Transaction postDrawTransaction, int seqId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongInterface(window);
          _Parcel.writeTypedObject(_data, postDrawTransaction, 0);
          _data.writeInt(seqId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_finishDrawing, _data, null, android.os.IBinder.FLAG_ONEWAY);
        }
        finally {
          _data.recycle();
        }
      }
    }
    /** @hide */
    public static final java.lang.String DESCRIPTOR = "android.view.IWindowSession";
    static final int TRANSACTION_addToDisplay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_remove = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_relayout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_finishDrawing = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
  }
  public int addToDisplay(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int viewVisibility, int layerStackId, int requestedVisibleTypes, android.view.InputChannel outInputChannel, android.view.WindowRelayoutResult result) throws android.os.RemoteException;
  public void remove(android.os.IBinder clientToken) throws android.os.RemoteException;
  public int relayout(android.view.IWindow window, android.view.WindowManager.LayoutParams attrs, int requestedWidth, int requestedHeight, int viewVisibility, int flags, int seq, int lastSyncSeqId, android.view.WindowRelayoutResult outRelayoutResult, android.view.SurfaceControl outSurface) throws android.os.RemoteException;
  public void finishDrawing(android.view.IWindow window, android.view.SurfaceControl.Transaction postDrawTransaction, int seqId) throws android.os.RemoteException;
  /** @hide */
  static class _Parcel {
    static private <T> T readTypedObject(
        android.os.Parcel parcel,
        android.os.Parcelable.Creator<T> c) {
      if (parcel.readInt() != 0) {
          return c.createFromParcel(parcel);
      } else {
          return null;
      }
    }
    static private <T extends android.os.Parcelable> void writeTypedObject(
        android.os.Parcel parcel, T value, int parcelableFlags) {
      if (value != null) {
        parcel.writeInt(1);
        value.writeToParcel(parcel, parcelableFlags);
      } else {
        parcel.writeInt(0);
      }
    }
  }
}
