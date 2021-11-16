package site.zbyte.root.app

import android.annotation.SuppressLint
import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import site.zbyte.root.sdk.ZRoot
import android.os.*
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.text)

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
            val content = "Message from remote: $msg"
            Log.i(TAG, content)
            textView.apply {
                text = "$text\n$content"
            }
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