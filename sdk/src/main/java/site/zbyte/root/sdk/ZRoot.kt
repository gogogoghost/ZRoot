package site.zbyte.root.sdk

import android.content.*
import android.os.*
import java.io.*
import java.util.zip.ZipFile

class ZRoot private constructor(private val remote: IRemote) {

    class Starter(private val context: Context){
        private val handler = Handler(Looper.getMainLooper())

        private val lock = Object()

        /**
         * 启动receiver
         */
        private fun startReceiver(cb:(IRemote)->Unit) {
            Provider.receiver={
                val remote=IRemote.Stub.asInterface(it)
                remote.registerWatcher(Binder())
                cb(remote)
            }
        }

        /**
         * 注销receiver
         */
        private fun stopReceiver() {
            Provider.receiver=null
        }

        /**
         * dex写入到cache目录
         */
        private fun writeDex(path: String) {
            //写入dex
            val dex = context.assets.open("runner.dex")
            val dexTmp = File(path)
            dexTmp.writeBytes(dex.readBytes())
        }

        /**
         * starter写入到cache目录
         */
        private fun writeStarter(path: String) {
            //写入starter
            val fos = FileOutputStream(path)
            val apk = ZipFile(context.applicationInfo.sourceDir)
            val entries = apk.entries()
            var found=false
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement() ?: break
                if(entry.name.startsWith("lib/")){
                    for(abi in Build.SUPPORTED_ABIS){
                        if(entry.name=="lib/$abi/libstarter.so"){
                            apk.getInputStream(entry).copyTo(fos)
                            fos.flush()
                            fos.close()
                            found=true
                            break
                        }
                    }
                    if(found)
                        break
                }
            }
            apk.close()
            if(!found)
                throw Exception("No support starter for current device")
        }

        /**
         * 启动runner进程
         */
        private fun startProcess(): Boolean {
            try {
                //此处无权限可能会抛出异常
                val process = Runtime.getRuntime().exec("su")

                val dir = "/data/local/tmp/${context.packageName}.root-driver"

                val dexTmpPath = context.cacheDir!!.absolutePath + "/runner.tmp"
                val dexRealPath = "${dir}/runner.dex"

                val starterTmpPath = context.cacheDir!!.absolutePath + "/starter.tmp"
                val starterRealPath = "${dir}/starter"


                writeDex(dexTmpPath)
                writeStarter(starterTmpPath)

                val output = OutputStreamWriter(process.outputStream)
                //重命名
                output.write("rm -rf $dir\n")
                output.write("mkdir ${dir}\n")
                output.write("mv $dexTmpPath $dexRealPath\n")
                output.write("mv $starterTmpPath ${starterRealPath}\n")
                output.write("chown -R shell:shell ${dir}\n")
                output.write("chmod +x ${starterRealPath}\n")
                output.write(
                    "$starterRealPath " +
                            "$dexRealPath " +
                            context.packageName +"\n"
                )
                output.write("exit\n");
                output.flush()
                val res=process.waitFor()
                if(res!=0)
                    throw Exception("Result code not zero:$res")
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        /**
         * 异步start
         */
        @Synchronized
        fun start(timeout: Long, callback: (ZRoot?) -> Unit) {
            var obj:ZRoot?=null
            startReceiver{
                obj= ZRoot(it)
                synchronized(lock) {
                    lock.notify()
                }
            }
            if (!startProcess()) {
                stopReceiver()
                callback.invoke(null)
                return
            }

            //开启等待超时线程
            Thread {
                synchronized(lock) {
                    lock.wait(timeout)
                }
                stopReceiver()
                //没被打断 超时了
                handler.post {
                    callback.invoke(obj)
                }
            }.start()
        }

        /**
         * 同步start
         */
        @Synchronized
        fun startBlocked(timeout: Long): ZRoot? {
            var obj:ZRoot?=null
            startReceiver{
                obj= ZRoot(it)
                synchronized(lock) {
                    lock.notify()
                }
            }
            if (!startProcess()) {
                stopReceiver()
                return null
            }
            //等待
            synchronized(lock) {
                lock.wait(timeout)
            }
            stopReceiver()
            return obj
        }
    }

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