package android.app;

import android.content.ComponentName;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.TransactionTooLargeException;

public interface IActivityManager {

    /**
     * 发送广播
     */
    public abstract int broadcastIntent(IApplicationThread caller,
                                        Intent intent, String resolvedType, IIntentReceiver resultTo,
                                        int resultCode, String resultData, Bundle resultExtras,
                                        String[] requiredPermissions, int appOp, Bundle bOptions,
                                        boolean serialized, boolean sticky, int userId);

    /**
     * 7.0+ 返回这个 6.0及一下直接返回List<T>
     */
    public ParceledListSlice getRecentTasks(int maxNum, int flags, int userId);


    /**
     * 7.0+需要传isMonkey
     */
    public void setActivityController(IActivityController controller, boolean isMonkey);

    /**
     * 6.0无需isMonkey
     */
    public void setActivityController(IActivityController controller);


    public int startActivityAsUser(IApplicationThread caller, String callingPackage,
                                         Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
                                         int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId);

    /**
     * 杀后台
     */
    public void killBackgroundProcesses(final String packageName, int userId);


    /**
     * 开启服务 安卓10
     */
    public ComponentName startService(IApplicationThread caller, Intent service,
                                      String resolvedType, boolean requireForeground, String callingPackage, int userId)
            throws TransactionTooLargeException;

    /**
     * 开启服务 安卓11
     */
    public ComponentName startService(IApplicationThread caller, Intent service,
                                      String resolvedType, boolean requireForeground, String callingPackage,String callingFeature, int userId)
            throws TransactionTooLargeException;

    public android.app.ContentProviderHolder getContentProviderExternal(
            String name, int userId, IBinder token, String tag);

    public ContentProviderHolder getContentProviderExternal(
            String name, int userId, IBinder token);


    public class ContentProviderHolder {
        public final ProviderInfo info=null;
        public IContentProvider provider;
        public IBinder connection;

        /**
         * Whether the provider here is a local provider or not.
         */
        public boolean mLocal;

    }


    public static abstract class Stub extends Binder implements IActivityManager {
        public static IActivityManager asInterface(IBinder binder){
            return null;
        }
    }
}
