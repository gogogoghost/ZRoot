package site.zbyte.root.sdk

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel

/**
 * 将origin中的transact一律转发到transactor
 */
class BinderProxy(private val origin: IBinder, private val transactor:ITransactor): Binder() {

    override fun queryLocalInterface(descriptor: String): IInterface? {
        return null
    }

    override fun onTransact(
        code: Int,
        data: Parcel,
        reply: Parcel?,
        flags: Int
    ): Boolean {
        val dataProxy = transactor.obtain(origin,code,data)
        try {
            return transactor.transact(code, dataProxy, reply, flags)
        } catch (e: Exception) {
            throw e
        } finally {
            dataProxy.recycle()
        }
    }
}