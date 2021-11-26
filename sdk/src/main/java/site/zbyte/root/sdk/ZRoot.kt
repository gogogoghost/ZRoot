package site.zbyte.root.sdk

import android.content.*
import android.os.*
import java.io.*
import java.util.zip.ZipFile

class ZRoot private constructor(private val remote: IRemote) {

    class Starter(private val context: Context){
        private val handler = Handler(Looper.getMainLooper())

        private val subThread = HandlerThread("runner")
        private val subHandler: Handler

        private var runnerReceiver: BroadcastReceiver? = null

        private val lock = Object()

        init {
            subThread.start()
            subHandler= Handler(subThread.looper)
        }
        /**
         * 启动receiver
         */
        private fun startReceiver(cb:(IRemote)->Unit) {
            val filter = IntentFilter()
            filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
            filter.addAction(context.packageName+".TRANSFER")
            runnerReceiver = RunnerReceiver{
                val remote=IRemote.Stub.asInterface(it)
                remote.registerWatcher(Binder())
                cb(remote)
            }
            //使用一个不存在的权限 以便通过non-protected broadcast check
            context.registerReceiver(runnerReceiver, filter, "site.zbyte.root.permission.TRANSFER", subHandler)
        }

        /**
         * 注销receiver
         */
        private fun stopReceiver() {
            context.unregisterReceiver(runnerReceiver)
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

                val dexTmpPath = context.externalCacheDir!!.absolutePath + "/runner.tmp"
                val dexRealPath = "${dir}/runner.dex"

                val starterTmpPath = context.externalCacheDir!!.absolutePath + "/starter.tmp"
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
                            context.packageName +
                            "&&exit\n"
                )
                output.flush()
                return process.waitFor() == 0
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
    fun getWorker(): IBinder {
        return worker
    }

    /**
     * 获取一个使用起来比较简单的服务
     */
    fun getRemoteService(name: String): IBinder {
        val service = ServiceManager.getService(name)
        return getServiceWrapper(object : ITransactor {
            override fun obtain(): Parcel {
                val parcel = Parcel.obtain()
                parcel.writeStrongBinder(service)
                return parcel
            }

            override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
                return caller.transact(code, data, reply, flags)
            }
        })
    }

    /**
     * 调用远程去call ContentProvider
     */
    fun callContentProvider(contentProvider:IBinder,packageName:String,authority:String,methodName:String,key:String,data:Bundle):Bundle?{
        return remote.callContentProvider(contentProvider, packageName, authority, methodName, key, data)
    }

    companion object {
        /**
         * 返回一个服务包装器
         * 实现transactor中的特权调用
         * 将返回的binder放入android service接口中调用
         */
        fun getServiceWrapper(transactor: ITransactor): IBinder {
            return object : Binder() {
                override fun queryLocalInterface(descriptor: String): IInterface? {
                    return null
                }

                override fun onTransact(
                    code: Int,
                    data: Parcel,
                    reply: Parcel?,
                    flags: Int
                ): Boolean {
                    val dataProxy = transactor.obtain()

                    dataProxy.appendFrom(data, 0, data.dataSize())
                    try {
                        return transactor.transact(code, dataProxy, reply, flags)
                    } catch (e: Exception) {
                        throw e
                    } finally {
                        dataProxy.recycle()
                    }
                }
            }
        }
    }
}