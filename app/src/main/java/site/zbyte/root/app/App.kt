package site.zbyte.root.app

import android.app.Application
import android.os.Build
import dalvik.system.BaseDexClassLoader
import org.lsposed.hiddenapibypass.HiddenApiBypass
import site.zbyte.root.sdk.Starter
import site.zbyte.root.sdk.ZRoot

class App:Application() {

    val zRootStarter=Starter(this)
    var zRoot:ZRoot?=null
    var err:Throwable?=null

    override fun onCreate() {
        super.onCreate()

        try{
            zRoot=zRootStarter.start(5000)
        }catch (e:Exception){
            err=e
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("");
        }
    }
}