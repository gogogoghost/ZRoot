package site.zbyte.root.sdk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RunnerReceiver(private val zRoot: ZRoot) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val binder = intent?.extras?.getBinder("runner")
        if (binder != null) {
            zRoot.onReceive(binder)
        }
    }
}