package site.zbyte.root;

import android.annotation.SuppressLint;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.AttributionSource;
import android.content.ContentProviderNative;
import android.content.IContentProvider;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import java.lang.reflect.Field;

import site.zbyte.root.sdk.IRemote;


public class Runner {

    private static final String TAG = "rd_runner";

    private static IBinder clientWatcher = null;

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
            System.exit(-1);
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
                if (i == 5)
                    System.exit(-2);
            }
        }).start();
    }

    private void run(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Bad args");
        }
        String packageName = args[0];
        IActivityManager mAm;
        IBinder activityRaw = getService("activity");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAm = IActivityManager.Stub.asInterface(activityRaw);
        } else {
            mAm = ActivityManagerNative.asInterface(activityRaw);
        }

        Looper.prepareMainLooper();

        //创建类
        IBinder finalWorker = Config.RemoteClass.isEmpty() ? null :
                (IBinder) Class.forName(Config.RemoteClass).newInstance();

        IBinder executor = new IRemote.Stub() {

            @Override
            public void registerWatcher(IBinder binder) throws RemoteException {
                clientWatcher = binder;
                //开始工作
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
            public IBinder getCaller() throws RemoteException {
                return new Binder() {
                    @Override
                    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                        IBinder service = data.readStrongBinder();
                        Parcel remoteData = Parcel.obtain();
                        try {
                            //写入数据
                            remoteData.appendFrom(data, data.dataPosition(), data.dataAvail());
                            service.transact(code, remoteData, reply, 0);
                        } catch (Exception e) {
                            throw e;
                        } finally {
                            remoteData.recycle();
                        }
                        return true;
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
                                              String authority, String methodName, String key,
                                              Bundle data) throws RemoteException {
                IContentProvider provider = ContentProviderNative.asInterface(contentProvider);
                if (provider == null) return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AttributionSource.Builder builder = new AttributionSource.Builder(0);
                    builder.setPackageName(packageName);
                    return provider.call(builder.build(), authority, methodName, key, data);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return provider.call(packageName, null, authority, methodName, key, data);

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return provider.call(packageName, authority, methodName, key, data);
                } else {
                    return provider.call(packageName, methodName, key, data);
                }
            }
        };


        Intent intent = new Intent(packageName+".ZR_TRANSFER");
        intent.setPackage(packageName);

        Bundle bundle = new Bundle();
        bundle.putBinder("runner", executor);
        bundle.putBinder("caller", null);
        intent.putExtras(bundle);


        int res=mAm.broadcastIntent(
                null,
                intent,
                intent.getType(),
                null,
                0,
                null,
                null,
                null,
                //OP_NONE
                -1,
                null,
                true,
                false,
                0
        );
        if(res!=0){
            //Fail
            Log.w(TAG,"broadcast fail with result:"+res);
            System.exit(-2);
        }
        checkStarted();
        Looper.loop();
    }

    public static void main(String[] args) throws Exception {
        new Runner().run(args);
    }
}
