package site.zbyte.root.sdk

import android.os.IBinder
import android.os.Parcel

class RemoteApi(
    private val remote: IRemoteService,
    private val rawBinder: IBinder,
    private val interfaceToken: String,
    private val transactCode: Int
) {

    fun obtain(): Parcel {
        val parcel = Parcel.obtain()
        parcel.writeStrongBinder(rawBinder)
        parcel.writeInterfaceToken(interfaceToken)
        return parcel
    }

    fun transact(data: Parcel, result: Parcel) {
        remote.transact(transactCode, data, result)
    }
}