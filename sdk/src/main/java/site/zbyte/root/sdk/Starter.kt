package site.zbyte.root.sdk

import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.zip.ZipFile

class Starter(private val context: Context,private val startupUid:Int=0){

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
                        "${context.packageName} "+
                        startupUid+
                        "\n"
            )
            output.write("exit\n");
            output.flush()
            val res=process.waitFor()
            if(res!=0)
                throw Exception("Bad exit code:$res")
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 异步start
     */
//    @Synchronized
//    fun start(timeout: Long, callback: (ZRoot?) -> Unit,customStarter:((()->Boolean)?)=null) {
//        var obj:ZRoot?=null
//        startReceiver{
//            obj= ZRoot(it)
//            synchronized(lock) {
//                lock.notify()
//            }
//        }
//        if (!(if(customStarter!=null){
//                customStarter()
//            }else{
//                startProcess()
//            })){
//            if (!startProcess()) {
//                stopReceiver()
//                callback.invoke(null)
//                return
//            }
//        }
//
//        //开启等待超时线程
//        Thread {
//            synchronized(lock) {
//                lock.wait(timeout)
//            }
//            stopReceiver()
//            //没被打断 超时了
//            callback.invoke(obj)
//        }.start()
//    }

    /**
     * 同步start
     */
    @Synchronized
    fun startBlocked(timeout: Long,customStarter:(()->Boolean)?=null): ZRoot? {
        var obj:ZRoot?=null
        startReceiver{
            obj= ZRoot(it)
            synchronized(lock) {
                lock.notify()
            }
        }
        if (!(if(customStarter!=null){
            customStarter()
        }else{
            startProcess()
        })) {
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