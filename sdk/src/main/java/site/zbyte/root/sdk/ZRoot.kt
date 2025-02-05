package site.zbyte.root.sdk

import android.os.*

class ZRoot(private val remote: IRemote) {

    /**
     * 获取远程进程uid
     */
    fun getUid():Int{
        return remote.uid
    }

    /**
     * 获取一个代理的ServiceManager中的服务binder
     */
    fun getRemoteService(name: String): IBinder {
        val binder= ServiceManager.getService(name) ?: throw Exception("No such service")
        return remote.obtainBinderProxy(binder)
    }

    /**
     * 获取一个自定义binder的root代理
     */
    fun makeBinderProxy(origin:IBinder):IBinder{
        return remote.obtainBinderProxy(origin)
    }
}