/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Using: /home/ghost/Android/Sdk/build-tools/35.0.0/aidl -p/home/ghost/Android/Sdk/platforms/android-35/framework.aidl -o/home/ghost/sec-project/ZRoot/sdk/build/generated/aidl_source_output_dir/release/out -I/home/ghost/sec-project/ZRoot/sdk/src/main/aidl -I/home/ghost/sec-project/ZRoot/sdk/src/release/aidl -d/tmp/aidl12363080075194172707.d /home/ghost/sec-project/ZRoot/sdk/src/main/aidl/site/zbyte/root/sdk/IRemote.aidl
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
    //将binder发到远程返回一个代理
    @Override public android.os.IBinder obtainBinderProxy(android.os.IBinder src) throws android.os.RemoteException
    {
      return null;
    }
    //获取transact code
    @Override public int getTransactCode(java.lang.String clsName, java.lang.String fieldName) throws android.os.RemoteException
    {
      return 0;
    }
    //start new remote process
    //    int forkProcess(int uid);
    //获取当前进程uid
    @Override public int getUid() throws android.os.RemoteException
    {
      return 0;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements site.zbyte.root.sdk.IRemote
  {
    /** Construct the stub at attach it to the interface. */
    @SuppressWarnings("this-escape")
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
      if (code >= android.os.IBinder.FIRST_CALL_TRANSACTION && code <= android.os.IBinder.LAST_CALL_TRANSACTION) {
        data.enforceInterface(descriptor);
      }
      if (code == INTERFACE_TRANSACTION) {
        reply.writeString(descriptor);
        return true;
      }
      switch (code)
      {
        case TRANSACTION_registerWatcher:
        {
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          this.registerWatcher(_arg0);
          reply.writeNoException();
          break;
        }
        case TRANSACTION_obtainBinderProxy:
        {
          android.os.IBinder _arg0;
          _arg0 = data.readStrongBinder();
          android.os.IBinder _result = this.obtainBinderProxy(_arg0);
          reply.writeNoException();
          reply.writeStrongBinder(_result);
          break;
        }
        case TRANSACTION_getTransactCode:
        {
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          int _result = this.getTransactCode(_arg0, _arg1);
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        case TRANSACTION_getUid:
        {
          int _result = this.getUid();
          reply.writeNoException();
          reply.writeInt(_result);
          break;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
      return true;
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
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      //将binder发到远程返回一个代理
      @Override public android.os.IBinder obtainBinderProxy(android.os.IBinder src) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.IBinder _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeStrongBinder(src);
          boolean _status = mRemote.transact(Stub.TRANSACTION_obtainBinderProxy, _data, _reply, 0);
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
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      //start new remote process
      //    int forkProcess(int uid);
      //获取当前进程uid
      @Override public int getUid() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        int _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUid, _data, _reply, 0);
          _reply.readException();
          _result = _reply.readInt();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
    }
    static final int TRANSACTION_registerWatcher = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_obtainBinderProxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getTransactCode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getUid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
  }
  /** @hide */
  public static final java.lang.String DESCRIPTOR = "site.zbyte.root.sdk.IRemote";
  //注册一个watcher 用来跟踪app是否死亡 然后runner退出
  public void registerWatcher(android.os.IBinder binder) throws android.os.RemoteException;
  //将binder发到远程返回一个代理
  public android.os.IBinder obtainBinderProxy(android.os.IBinder src) throws android.os.RemoteException;
  //获取transact code
  public int getTransactCode(java.lang.String clsName, java.lang.String fieldName) throws android.os.RemoteException;
  //start new remote process
  //    int forkProcess(int uid);
  //获取当前进程uid
  public int getUid() throws android.os.RemoteException;
}
