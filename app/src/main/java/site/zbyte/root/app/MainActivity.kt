package site.zbyte.root.app

import android.annotation.SuppressLint
import android.app.ActivityManagerNative
import android.app.IActivityManager
import android.content.AttributionSource
import android.content.ContentProviderNative
import android.content.Intent
import android.os.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        val app=(application as App)

        thread {
            val zRoot=app.zRoot

            if(zRoot==null){
                runOnUiThread{
                    textView.apply {
                        text = "$text\n"+(app.err?.toString()?:"unknown error")
                    }
                }
                return@thread
            }

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

            /**
             * call proxies content provider
             */
            val remoteProvider=ContentProviderNative.asInterface(
                zRoot.makeBinderProxy(provider.asBinder())
            )
            val methodName="PUT_secure"
            val arg="accessibility_enabled"
            val callerPkgName="com.android.shell"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val builder = AttributionSource.Builder(Process.myUid())
                builder.setPackageName(callerPkgName)
                remoteProvider.call(builder.build(), authority, methodName, arg, bundle)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                remoteProvider.call(callerPkgName, null, authority, methodName, arg, bundle)
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                remoteProvider.call(callerPkgName, authority, methodName, arg, bundle)
            } else {
                remoteProvider.call(callerPkgName, methodName, arg, bundle)
            }

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
            print(mAm2)

            /**
             * create a shell process
             */
            if(zRoot.getUid()==0){
                try{
                    val shellZRoot=zRoot.forkBlocked(this,2000,5000)
                    runOnUiThread{
                        textView.apply {
                            text = "$text\n"+"Message from remote: ${shellZRoot.getUid()}"
                        }
                    }
                }catch (e:Exception){
                    runOnUiThread{
                        textView.apply {
                            text = "$text\n"+(e.toString())
                        }
                    }
                }
            }
        }

    }
}