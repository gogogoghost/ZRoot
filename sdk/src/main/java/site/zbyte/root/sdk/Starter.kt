package site.zbyte.root.sdk

import android.content.Context
import android.os.Binder
import java.io.File
import java.io.OutputStreamWriter


class Starter(private val context: Context){

    private val lock = Object()

    private var process:Process?=null

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
     * 启动runner进程
     */
    private fun startProcess(uid:Int) {
        val output:OutputStreamWriter
        val depPath="/data/local/tmp/rd_${context.packageName}.dex"
        if(process==null){
            process=Runtime.getRuntime().exec("su")
            output=OutputStreamWriter(process!!.outputStream)
            val runnerDex = File(context.cacheDir, "runner.dex")
            runnerDex.writeBytes(context.assets.open("runner.dex").readBytes())
            output.write("cp $runnerDex $depPath\n")
            output.write("chown 2000:2000 $depPath\n")
        }else{
            output=OutputStreamWriter(process!!.outputStream)
        }
        val cmd="(${if(uid==0) "" else "su $uid -c "}CLASSPATH=$depPath /system/bin/app_process /system/bin --nice-name=rd_runner site.zbyte.root.Runner ${context.packageName} > /dev/null 2>&1) &\n"
        output.write(cmd)
        output.flush()
    }

    /**
     * 同步start
     */
    @Synchronized
    fun start(timeout: Long,uid:Int=0): ZRoot {
        var obj:ZRoot?=null
        startReceiver{
            obj= ZRoot(it)
            synchronized(lock) {
                lock.notify()
            }
        }
        try{
            startProcess(uid)
            synchronized(lock) {
                lock.wait(timeout)
            }
        }catch (e:Exception){
            throw StartProcessException(e)
        }finally {
            stopReceiver()
        }
        return obj?:throw TimeoutException()
    }
}