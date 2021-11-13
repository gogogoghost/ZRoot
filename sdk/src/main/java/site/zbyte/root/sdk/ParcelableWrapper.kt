package site.zbyte.root.sdk

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler

class ParcelableWrapper(val value: Parcelable?=null)

object ParcelableParceler: Parceler<ParcelableWrapper> {
    override fun create(parcel: Parcel): ParcelableWrapper {
        return ParcelableWrapper(null)
    }

    override fun ParcelableWrapper.write(parcel: Parcel, flags: Int) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            if(value==null){
                parcel.writeInt(0)
            }else{
                parcel.writeInt(1)
            }
        }
        value?.writeToParcel(parcel,0)
    }
}