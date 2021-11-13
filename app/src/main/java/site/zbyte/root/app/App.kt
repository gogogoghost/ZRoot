package site.zbyte.root.app

import android.app.Application
import android.os.Build
import dalvik.system.BaseDexClassLoader
import org.lsposed.hiddenapibypass.HiddenApiBypass

class App:Application() {


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("");
        }
    }
}