package site.zbyte.root.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class AutoKillService :AccessibilityService(){
    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        println("run!!!!!!!!!")
    }
}