package android.content.pm;

import android.app.ActivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PersistableBundle;

public interface IPackageManager {
    public void setApplicationEnabledSetting(String appPackageName,
                                             int newState, int flags, int userId, String callingPackage);

    //public String[] getPackagesForUid(int uid);

    /**
     * api 28 安卓9
     */
    public String[] setPackagesSuspendedAsUser(String[] packageNames, boolean suspended,
                                               PersistableBundle appExtras, PersistableBundle launcherExtras, String dialogMessage,
                                               String callingPackage, int userId);

    /**
     * api 29 安卓10
     */
    public String[] setPackagesSuspendedAsUser(String[] packageNames, boolean suspended,
                                               PersistableBundle appExtras, PersistableBundle launcherExtras, SuspendDialogInfo dialogMessage,
                                               String callingPackage, int userId);

    /**
     * 获取应用信息
     */
    public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId);

    public static abstract class Stub extends Binder implements IPackageManager {
        public static IPackageManager asInterface(IBinder binder){
            return null;
        }
    }
}
