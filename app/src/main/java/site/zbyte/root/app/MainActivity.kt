package site.zbyte.root.app

import android.annotation.SuppressLint
import android.app.ActivityManagerNative
import android.app.Application
import android.app.IActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import site.zbyte.root.sdk.ZRoot
import android.os.*
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContentResolverCompat
import site.zbyte.root.sdk.Starter
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.text)

        thread {
            val zRoot= (application as App).zRoot ?: return@thread

            runOnUiThread{
                textView.apply {
                    text = "$text\n"+"Message from remote: ${zRoot.getUid()}"
                }
            }

            /**
             * get default remote service
             */
            val remoteService = zRoot.getRemoteService("activity")
            println("remote:$remoteService")

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
                -1,
                null,
                true,
                false,
                0
            )

            /*
             * get ContentProvider from activity service via root
             */
            val authority = "settings"
            val provider = if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
                mAm.getContentProviderExternal(authority, 0, null, null).provider
            else
                mAm.getContentProviderExternal(authority, 0, null).provider

            /**
             * it equivalent to:
             *     settings put secure accessibility_enabled 1
             * via root user
             */
            val bundle = Bundle()
            bundle.putString("value", "1")
            zRoot.callContentProvider(
                provider.asBinder(),
                "com.android.shell",
                authority,
                "PUT_secure",
                "accessibility_enabled",
                bundle
            )

            /**
             * proxy an existing local binder
             */
            val existingBinder=ServiceManager.getService("activity")

            /**
             * get a proxy
             */
            val proxyBinder=zRoot.makeBinderProxy(existingBinder)
            /**
             * now use mAm2 will be same as using mAm
             */
            val mAm2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                IActivityManager.Stub.asInterface(proxyBinder)
            else
                ActivityManagerNative.asInterface(proxyBinder)

            /**
             * create a shell process
             */
            if(zRoot.getUid()==0){
                val shellZRoot=zRoot.forkBlocked(this,2000,5000)?:return@thread
                runOnUiThread{
                    textView.apply {
                        text = "$text\n"+"Message from remote: ${shellZRoot.getUid()}"
                    }
                }
            }
        }

    }
}