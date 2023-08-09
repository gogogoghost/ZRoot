package site.zbyte.root.sdk

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class Provider:ContentProvider() {

    companion object{
        var receiver:((IBinder)->Unit)?=null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun call(
        authority: String, method: String,
        arg: String?, extras: Bundle?
    ): Bundle? {
        return call(method, arg, extras)
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if(method=="transfer"){
            receiver?.invoke(extras!!.getBinder("runner")!!)
            receiver=null
        }
        return null
    }
}