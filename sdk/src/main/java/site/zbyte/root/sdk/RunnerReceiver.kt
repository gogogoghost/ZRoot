package site.zbyte.root.sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder

class RunnerReceiver(private val onReceive:(IBinder)->Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val binder = intent?.extras?.getBinder("runner")
        if (binder != null) {
            onReceive.invoke(binder)
        }
    }
}