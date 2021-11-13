package site.zbyte.root.sdk

import android.content.*
import android.os.*
import android.util.Log
import dalvik.system.PathClassLoader
import site.zbyte.root.aidl.IRemote
import java.io.*
import java.util.zip.ZipFile
import kotlin.random.Random

class ZRoot(private val context: Context) {

    private var asyncStarting = false

    private val handler = Handler(Looper.getMainLooper())

    private val subThread = HandlerThread("runner")
    private val subHandler: Handler

    private val lock = Object()

    private var rawRemote:IRemote?=null
    private var worker: IBinder? = null
    private var caller: IBinder? = null

    private var runnerReceiver: BroadcastReceiver? = null

    private var randomString = ""

    private val serviceMap = hashMapOf<String, IRemoteService>()

    private var deadCallback: (() -> Unit)? = null

    init {
        subThread.start()
        subHandler = Handler(subThread.looper)
    }

    fun registerDeadCallback(callback: () -> Unit) {
        this.deadCallback = callback
    }

    /**
     * 更新随机字符串
     */
    private fun updateRandomString() {
        randomString = getRandomString(6)
    }

    /**
     * 启动receiver
     */
    private fun startReceiver() {
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction("site.zbyte.root.SEND_RUNNER.${randomString}")
        runnerReceiver = RunnerReceiver(this)
        context.registerReceiver(runnerReceiver, filter, null, subHandler)
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
        val so = "lib/${Build.SUPPORTED_ABIS[0]}/libstarter.so"

        val ai = context.applicationInfo

        val fos = FileOutputStream(path)
        val apk = ZipFile(ai.sourceDir)
        val entries = apk.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement() ?: break
            if (entry.name != so) continue
            apk.getInputStream(entry).copyTo(fos)
            fos.flush()
            fos.close()
            break
        }
        apk.close()
    }

    /**
     * 启动runner进程
     */
    private fun startProcess(): Boolean {
        try {
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
                        randomString +
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
    fun start(
        timeout: Long,
        callback: (Boolean) -> Unit
    ) {
        if (asyncStarting) {
            throw Exception("Already under starting")
        }
        asyncStarting = true

        if (worker != null) {
            //主线程 直接发送
            callback.invoke(true)
            asyncStarting = false
            return
        }

        updateRandomString()

        startReceiver()

        if (!startProcess()) {
            stopReceiver()
            callback.invoke(false)
            asyncStarting = false
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
                callback.invoke(worker != null)
            }
            asyncStarting = false
        }.start()
    }

    /**
     * 同步start
     */
    @Synchronized
    fun startBlocked(timeout: Long): Boolean {
        if (asyncStarting) {
            throw Exception("Already under getting")
        }
        if (worker != null) {
            return true
        }

        updateRandomString()
        startReceiver()
        if (!startProcess()) {
            stopReceiver()
            return false
        }
        //等待
        synchronized(lock) {
            lock.wait(timeout)
        }
        stopReceiver()
        return worker != null
    }

    fun onReceive(binder: IBinder) {
        val raw = IRemote.Stub.asInterface(binder)
        raw.asBinder().linkToDeath({
            rawRemote=null
            worker = null
            caller = null
            handler.post {
                deadCallback?.invoke()
            }
        }, 0)
        raw.registerWatcher(Binder())
        rawRemote=raw
        worker = raw.worker
        caller = raw.caller
        synchronized(lock) {
            lock.notify()
        }
    }

    private fun getRandomString(length: Int): String {
        val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number: Int = Random.nextInt(str.length)
            sb.append(str[number])
        }
        return sb.toString()
    }

    /**
     * 获取远程binder
     */
    fun getExecutor():IRemote?{
        return rawRemote
    }

    /**
     * 获取用户自定义远程worker
     */
    fun getWorker(): IBinder? {
        return worker
    }

    /**
     * 获取一个使用起来比较麻烦的服务
     */
    fun getService(name: String, interfaceToken: String): IRemoteService? {
        if (serviceMap.containsKey(name))
            return serviceMap[name]
        //初始化服务
        val binder = ServiceManager.getService(name) ?: return null
        val service = object : IRemoteService {
            override fun transact(transactCode: Int, data: Parcel, result: Parcel) {
                caller!!.transact(transactCode, data, result, 0)
            }

            override fun createApi(interfaceName: String, codeName: String): RemoteApi {
                val code = rawRemote!!.getTransactCode(interfaceName, codeName)
                if (code == -1)
                    throw Exception("Cannot find this code")
                return RemoteApi(this, binder, interfaceToken, code)
            }

            override fun createApi(methodName: String): RemoteApi {
                return createApi("$interfaceToken\$Stub", "TRANSACTION_$methodName")
            }

            override fun getInterfaceToken(): String {
                return interfaceToken
            }
        }
        serviceMap[name] = service
        return service
    }

    /**
     * 获取一个使用起来比较简单的服务
     */
    fun getRemoteService(name:String):IBinder{
        val service=ServiceManager.getService(name)
        return getServiceWrapper(object :ITransactor{
            override fun obtain(): Parcel {
                val parcel=Parcel.obtain()
                parcel.writeStrongBinder(service)
                return parcel
            }
            override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
                return caller!!.transact(code,data,reply,flags)
            }
        })
    }

    companion object{
        /**
         * 返回一个服务包装器
         * 实现transactor中的特权调用
         * 将返回的binder放入android service接口中调用
         */
        fun getServiceWrapper(transactor:ITransactor):IBinder{
            return object :Binder(){
                override fun queryLocalInterface(descriptor: String): IInterface? {
                    return null
                }
                override fun onTransact(
                    code: Int,
                    data: Parcel,
                    reply: Parcel?,
                    flags: Int
                ): Boolean {
                    val dataProxy=transactor.obtain()

                    dataProxy.appendFrom(data,0,data.dataSize())
                    try{
                        return transactor.transact(code,dataProxy,reply,flags)
                    }catch (e:Exception){
                        throw e
                    }finally {
                        dataProxy.recycle()
                    }
                }
            }
        }
    }
}