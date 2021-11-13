package site.zbyte.root.sdk

import android.os.Parcel

interface ITransactor {
    fun obtain():Parcel
    fun transact(code:Int,data:Parcel,reply:Parcel?,flags:Int):Boolean
}