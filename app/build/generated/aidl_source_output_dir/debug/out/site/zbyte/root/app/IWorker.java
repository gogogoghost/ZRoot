/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package site.zbyte.root.app;
// Declare any non-default types here with import statements

public interface IWorker extends android.os.IInterface
{
  /** Default implementation for IWorker. */
  public static class Default implements site.zbyte.root.app.IWorker
  {
    @Override public java.lang.String work() throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements site.zbyte.root.app.IWorker
  {
    private static final java.lang.String DESCRIPTOR = "site.zbyte.root.app.IWorker";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an site.zbyte.root.app.IWorker interface,
     * generating a proxy if needed.
     */
    public static site.zbyte.root.app.IWorker asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof site.zbyte.root.app.IWorker))) {
        return ((site.zbyte.root.app.IWorker)iin);
      }
      return new site.zbyte.root.app.IWorker.Stub.Proxy(obj);
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
        case TRANSACTION_work:
        {
          data.enforceInterface(descriptor);
          java.lang.String _result = this.work();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements site.zbyte.root.app.IWorker
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
      @Override public java.lang.String work() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        java.lang.String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_work, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().work();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static site.zbyte.root.app.IWorker sDefaultImpl;
    }
    static final int TRANSACTION_work = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(site.zbyte.root.app.IWorker impl) {
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
    public static site.zbyte.root.app.IWorker getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public java.lang.String work() throws android.os.RemoteException;
}
