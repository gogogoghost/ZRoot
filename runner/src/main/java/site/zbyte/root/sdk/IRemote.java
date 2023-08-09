/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package site.zbyte.root.sdk;
// Declare any non-default types here with import statements

public interface IRemote extends android.os.IInterface
{
  /** Default implementation for IRemote. */
  public static class Default implements site.zbyte.root.sdk.IRemote
  {
    //注册一个watcher 用来跟踪app是否死亡 然后runner退出

    @Override public void registerWatcher(android.os.IBinder binder) throws android.os.RemoteException
    {
    }
    //获取worker

    @Override public android.os.IBinder getWorker() throws android.os.RemoteException
    {
      return null;
    }
    //获取Caller 专门接收transact的

    @Override public android.os.IBinder getCaller() throws android.os.RemoteException
    {
      return null;
    }
    //获取transact code

    @Override public int getTransactCode(java.lang.String clsName, java.lang.String fieldName) throws android.os.RemoteException
    {
      return 0;
    }
    //content provider专用call

    @Override public android.os.Bundle callContentProvider(android.os.IBinder contentProvider, java.lang.String packageName, java.lang.String authority, java.lang.String methodName, java.lang.String key, android.os.Bundle data) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements site.zbyte.root.sdk.IRemote
  {
    private static final java.lang.String DESCRIPTOR = "site.zbyte.root.sdk.IRemote";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an site.zbyte.root.sdk.IRemote interface,
     * generating a proxy if needed.
     */
    public static site.zbyte.root.sdk.IRemote asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof site.zbyte.root.sdk.IRemote))) {
        return ((site.zbyte.root.sdk.IRemote)iin);
      }
      return new site.zbyte.root.sdk.IRemote.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_registerWatcher:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.registerWatcher(_arg0);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_getWorker:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _result = this.getWorker();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          return true;
        }
        case TRANSACTION_getCaller:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _result = this.getCaller();
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          return true;
        }
        case TRANSACTION_getTransactCode:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _result = this.getTransactCode(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(_result);
          return true;
        }
        case TRANSACTION_callContentProvider:
        {
          data.enforceInterface(descriptor);
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          java.lang.String _arg1;
          _arg1 = data.readString();
          java.lang.String _arg2;
          _arg2 = data.readString();
          java.lang.String _arg3;
          _arg3 = data.readString();
          java.lang.String _arg4;
          _arg4 = data.readString();
          android.os.Bundle _arg5;
          if ((0!=data.readInt())) {
            _arg5 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg5 = null;
          }
          android.os.Bundle _result = this.callContentProvider(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements site.zbyte.root.sdk.IRemote
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
      //注册一个watcher 用来跟踪app是否死亡 然后runner退出

      @Override public void registerWatcher(android.os.IBinder binder) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(binder);
          boolean _status = mRemote.transact(Stub.TRANSACTION_registerWatcher, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().registerWatcher(binder);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      //获取worker

      @Override public android.os.IBinder getWorker() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getWorker, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getWorker();
          }
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //获取Caller 专门接收transact的

      @Override public android.os.IBinder getCaller() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getCaller, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getCaller();
          }
          _reply.readException();
          _result = _reply.readStrongBinder();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //获取transact code

      @Override public int getTransactCode(java.lang.String clsName, java.lang.String fieldName) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(clsName);
          _data.writeString(fieldName);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getTransactCode, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getTransactCode(clsName, fieldName);
          }
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //content provider专用call

      @Override public android.os.Bundle callContentProvider(android.os.IBinder contentProvider, java.lang.String packageName, java.lang.String authority, java.lang.String methodName, java.lang.String key, android.os.Bundle data) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.Bundle _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(contentProvider);
          _data.writeString(packageName);
          _data.writeString(authority);
          _data.writeString(methodName);
          _data.writeString(key);
          if ((data!=null)) {
            _data.writeInt(1);
            data.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_callContentProvider, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().callContentProvider(contentProvider, packageName, authority, methodName, key, data);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.os.Bundle.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static site.zbyte.root.sdk.IRemote sDefaultImpl;
    }
    static final int TRANSACTION_registerWatcher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getWorker = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getCaller = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getTransactCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_callContentProvider = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    public static boolean setDefaultImpl(site.zbyte.root.sdk.IRemote impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static site.zbyte.root.sdk.IRemote getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  //注册一个watcher 用来跟踪app是否死亡 然后runner退出

  public void registerWatcher(android.os.IBinder binder) throws android.os.RemoteException;
  //获取worker

  public android.os.IBinder getWorker() throws android.os.RemoteException;
  //获取Caller 专门接收transact的

  public android.os.IBinder getCaller() throws android.os.RemoteException;
  //获取transact code

  public int getTransactCode(java.lang.String clsName, java.lang.String fieldName) throws android.os.RemoteException;
  //content provider专用call

  public android.os.Bundle callContentProvider(android.os.IBinder contentProvider, java.lang.String packageName, java.lang.String authority, java.lang.String methodName, java.lang.String arg, android.os.Bundle data) throws android.os.RemoteException;
}
