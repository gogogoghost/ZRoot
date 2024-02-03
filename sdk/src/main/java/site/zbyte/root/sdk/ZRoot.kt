package site.zbyte.root.sdk

import android.content.Context
import android.os.*
import android.util.Log

class ZRoot(private val remote: IRemote) {

    private var worker=remote.worker
    private var caller=remote.caller
    private val transactor=RootTransactor(this)

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
     * 获取一个代理的ServiceManager中的服务binder
     */
    fun getRemoteService(name: String): IBinder {
        val service = ServiceManager.getService(name)
        return BinderProxy(service,transactor)
    }

    /**
     * 获取一个自定义binder的root代理
     */
    fun makeBinderProxy(origin:IBinder):IBinder{
        return BinderProxy(origin,transactor)
    }

    /**
     * 调用远程去call ContentProvider
     */
    fun callContentProvider(contentProvider:IBinder,packageName:String,authority:String,methodName:String,arg:String,data:Bundle):Bundle?{
        return remote.callContentProvider(contentProvider, packageName, authority, methodName, arg, data)
    }

    /**
     * 经过远程对象创建一个新进程 同步
     */
    fun forkBlocked(context: Context, uid:Int,timeout:Long):ZRoot?{
        val starter=Starter(context)
        return starter.startBlocked(timeout){
            remote.forkProcess(uid)==0
        }
    }

    /**
     * 经过远程对象创建一个新进程 异步
     */
//    fun fork(context: Context, uid:Int,timeout:Long,cb:(ZRoot?)->Unit){
//        val starter=Starter(context)
//        return starter.start(timeout,cb){
//            remote.forkProcess(uid)==0
//        }
//    }

    /**
     * 自定义transact转发
     */
    class RootTransactor(private val zRoot:ZRoot):ITransactor{
        override fun obtain(oBinder: IBinder,oCode:Int, oData: Parcel, flags:Int): Parcel {
            val dataProxy=Parcel.obtain()
            //先写入origin
            dataProxy.writeStrongBinder(oBinder)
            //再写入data
            dataProxy.appendFrom(oData, 0, oData.dataSize())
            return dataProxy
        }

        override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            return zRoot.caller.transact(code, data, reply, flags)
        }
    }
}