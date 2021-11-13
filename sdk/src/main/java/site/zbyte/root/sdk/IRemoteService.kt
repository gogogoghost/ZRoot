package site.zbyte.root.sdk

import android.os.Parcel

interface IRemoteService {

    fun transact(transactCode:Int,data:Parcel,result:Parcel)

    fun createApi(interfaceName:String,codeName:String):RemoteApi

    fun createApi(methodName:String):RemoteApi

    fun getInterfaceToken():String
}