package site.zbyte.root.app

import android.app.Application
import android.os.Build
import dalvik.system.BaseDexClassLoader
import org.lsposed.hiddenapibypass.HiddenApiBypass
import site.zbyte.root.sdk.Starter
import site.zbyte.root.sdk.ZRoot

class App:Application() {

    var zRoot:ZRoot?=null
    override fun onCreate() {
        super.onCreate()

        zRoot=Starter(this,0).startBlocked(5000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("");
        }
    }
}