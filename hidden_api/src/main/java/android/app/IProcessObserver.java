package android.app;

import android.os.Binder;
import android.os.IBinder;

public interface IProcessObserver {
    void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities);
    void onForegroundServicesChanged(int pid, int uid, int serviceTypes);
    void onProcessDied(int pid, int uid);

    public static abstract class Stub extends Binder implements IProcessObserver {
        public static IProcessObserver asInterface(IBinder binder){
            return null;
        }
    }
}
