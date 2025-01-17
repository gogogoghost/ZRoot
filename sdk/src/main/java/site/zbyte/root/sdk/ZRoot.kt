package site.zbyte.root.sdk

import android.content.Context
import android.os.*

class ZRoot(private val remote: IRemote) {

    private var worker=remote.worker

    private var deadCallback: (() -> Unit)? = null

    init {
        remote.asBinder().linkToDeath({
             deadCallback?.invoke()
        },0)
    }

    /**
     * 注册一个死亡回调
     */
    fun registerDeadCallback(callback: () -> Unit) {
        this.deadCallback = callback
    }

    /**
     * 获取用户自定义远程worker
     */
    fun getWorker(): IBinder? {
        return worker
    }

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

    /**
     * 经过远程对象创建一个新进程 同步
     */
    fun forkBlocked(context: Context, uid:Int,timeout:Long):ZRoot{
        if(remote.uid!=0){
            throw Exception("Only uid 0 process could fork")
        }
        val starter=Starter(context)
        return starter.startBlocked(timeout){
            val res=remote.forkProcess(uid)
            if(res!=0){
                throw Exception("Bad exit code: $res")
            }
        }
    }
}