package site.zbyte.root.app

import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import site.zbyte.root.sdk.ZRoot
import android.os.*
import android.util.Log

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val zRoot = ZRoot(this)
        zRoot.start(5000) {
            //start fail
            if (!it)
                return@start

            /**
             * get custom remote worker
             */
            val worker = IWorker.Stub.asInterface(zRoot.getWorker())

            /**
             * invoke remote method
             */
            val msg = worker.work()
            Log.i(TAG, "from remote: $msg")

            /**
             * get remote service
             */
            val remoteService = zRoot.getRemoteService("activity")

            /**
             * convert to IActivityManager
             */
            val mAm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                IActivityManager.Stub.asInterface(remoteService)
            else
                ActivityManagerNative.asInterface(remoteService)

            /**
             * send broadcast via root
             */
            val intent = Intent("android.abc.abc.abc")
            mAm.broadcastIntent(
                null,
                intent,
                intent.type,
                null,
                0,
                null,
                null,
                null,
                0,
                null,
                true,
                false,
                0
            )

            /*
             * get ContentProvider from activity service via root
             */
            val authority = "settings"
            val holder = mAm.getContentProviderExternal(authority, 0, null, null)

            /**
             * it equivalent to:
             *     settings put secure accessibility_enabled 1
             * via root user
             */
            val bundle = Bundle()
            bundle.putString("value", "1")
            zRoot.getExecutor()!!.callContentProvider(
                holder.provider.asBinder(),
                "android",
                authority,
                "PUT_secure",
                "accessibility_enabled",
                bundle
            )
        }
    }
}