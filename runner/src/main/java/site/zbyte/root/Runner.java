package site.zbyte.root;

import android.app.ActivityManagerNative;
import android.app.ContentProviderHolder;
import android.app.IActivityManager;
import android.content.AttributionSource;
import android.content.ContentProviderNative;
import android.content.IContentProvider;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import site.zbyte.root.sdk.IRemote;


public class Runner {

    private static final String TAG = "rd_runner";

    private static IBinder clientWatcher = null;

    private static final int ERR_UNKNOWN = -10;
    private static final int ERR_WAIT_TIMEOUT = -1;
    private static final int ERR_GET_PROVIDER = -2;
    private static final int ERR_CALL_TIMEOUT = -3;
    private static final int ERR_GET_AMS = -4;

    /**
     * 获取服务
     */
    private static IBinder getService(String name) {
        IBinder res;
        while ((res = ServiceManager.getService(name)) == null) ;
        return res;
    }

    /**
     * 休眠
     */
    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.exit(ERR_UNKNOWN);
        }
    }

    /**
     * 检查启动成功与否
     */
    private static void checkStarted() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 5 && clientWatcher == null) {
                    sleep(1000);
                    i++;
                }
                if (i == 5){
                    Log.w(TAG,"wait timeout");
                    System.exit(ERR_WAIT_TIMEOUT);
                }
            }
        }).start();
    }

    private void run(String[] args) throws Exception {
        if (args.length != 3) {
            throw new Exception("Bad args");
        }
        String starterPath = args[0];
        String dexPath = args[1];
        String packageName = args[2];

        Looper.prepareMainLooper();

        IActivityManager mAm;
        IBinder activityRaw = getService("activity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAm = IActivityManager.Stub.asInterface(activityRaw);
        } else {
            mAm = ActivityManagerNative.asInterface(activityRaw);
        }
        if(mAm==null){
            System.exit(ERR_GET_AMS);
        }
//        Log.i(TAG,"args:"+starterPath+" "+dexPath+" "+packageName+" "+Process.myUid());

        //创建类
        Log.i(TAG,"create worker:"+Config.RemoteClass);
        final IBinder finalWorker = Config.RemoteClass.isEmpty() ? null :
                (IBinder) Class.forName(Config.RemoteClass).newInstance();

        IRemote executor = new IRemote.Stub() {

            @Override
            public void registerWatcher(IBinder binder) throws RemoteException {
                clientWatcher = binder;
                //开始工作 注册死亡回调
                binder.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        System.exit(0);
                    }
                }, 0);
            }

            @Override
            public IBinder getWorker() throws RemoteException {
                return finalWorker;
            }

            @Override
            public IBinder obtainBinderProxy(IBinder src) throws RemoteException {
                if(src==null){
                    throw new RemoteException("Origin binder could not be null");
                }
                Log.d(TAG,"createBinder");
                return new Binder() {
                    @Override
                    public boolean onTransact(int code,Parcel data, Parcel reply, int flags) throws RemoteException {
                        Log.d(TAG,"call:"+code);
                        return src.transact(code, data, reply, flags);
                    }
                };
            }

            @Override
            public int getTransactCode(String clsName, String fieldName) throws RemoteException {
                try {
                    Class<?> cls = Class.forName(clsName);
                    Field field = cls.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.getInt(null);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    return -1;
                }
            }

            @Override
            public Bundle callContentProvider(IBinder contentProvider, String packageName,
                                              String authority, String methodName, String arg,
                                              Bundle data) throws RemoteException {
                IContentProvider provider = ContentProviderNative.asInterface(contentProvider);
                if (provider == null) return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AttributionSource.Builder builder = new AttributionSource.Builder(Process.myUid());
                    builder.setPackageName(packageName);
                    return provider.call(builder.build(), authority, methodName, arg, data);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return provider.call(packageName, null, authority, methodName, arg, data);

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return provider.call(packageName, authority, methodName, arg, data);
                } else {
                    return provider.call(packageName, methodName, arg, data);
                }
            }

            @Override
            public int forkProcess(int uid) throws RemoteException {
                try {
                    return Runtime.getRuntime().exec(starterPath+" "+dexPath+" "+packageName+" "+uid).waitFor();
                } catch (Exception e) {
                    Log.e(TAG,"start error"+e);
                    throw new RemoteException(e.toString());
                }
            }

            @Override
            public int getUid() throws RemoteException {
                return Process.myUid();
            }
        };

        Bundle bundle = new Bundle();
        bundle.putBinder("runner", executor.asBinder());
//        bundle.putBinder("caller", null);

        String authority=packageName+".zroot";
        IContentProvider provider=null;
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentProviderHolder holder=mAm.getContentProviderExternal(authority, 0, null, null);
                if(holder==null){
                    throw new Exception("cannot get provider");
                }
                provider=holder.provider;
            }
            else{
                IActivityManager.ContentProviderHolder holder =mAm.getContentProviderExternal(authority, 0, null);
                if(holder==null){
                    throw new Exception("cannot get provider");
                }
                provider=holder.provider;
            }
        }catch (Exception e){
            Log.w(TAG,e.toString());
            System.exit(ERR_GET_PROVIDER);
        }

        try{
            Bundle res=executor.callContentProvider(
                    provider.asBinder(),
                    "android",
                    authority,
                    "transfer",
                    null,
                    bundle
            );
        }catch (Exception e){
            Log.w(TAG,e);
            System.exit(ERR_CALL_TIMEOUT);
        }

        checkStarted();
        Looper.loop();
    }

    public static void main(String[] args) throws Exception {
        new Runner().run(args);
    }
}
