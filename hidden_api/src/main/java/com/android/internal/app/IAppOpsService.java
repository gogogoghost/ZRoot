package com.android.internal.app;

import android.os.Binder;
import android.os.IBinder;

public interface IAppOpsService {



    public void setMode(int code, int uid, String packageName, int mode);

    public static abstract class Stub extends Binder implements IAppOpsService {
        public static IAppOpsService asInterface(IBinder binder){
            return null;
        }
    }
}
