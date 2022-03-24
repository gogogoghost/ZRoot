package site.zbyte.root.sdk

import android.os.IBinder
import android.os.Parcel

/**
 * 该接口用于自定义了一个binder代理功能
 */
interface ITransactor {
    /**
     * obtain一个parcel，并预处理要发送的数据结构
     */
    fun obtain(oBinder:IBinder,oCode:Int,oData:Parcel):Parcel

    /**
     * 完成自定义transact，一般是发送到指定远程进程
     * 无需自行try catch
     */
    fun transact(code:Int,data:Parcel,reply:Parcel?,flags:Int):Boolean
}