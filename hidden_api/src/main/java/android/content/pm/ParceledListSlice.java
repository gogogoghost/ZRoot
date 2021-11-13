package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParceledListSlice<T>{

    public List<T> getList(){
        return null;
    }

    public static final Parcelable.ClassLoaderCreator<ParceledListSlice> CREATOR=new Parcelable.ClassLoaderCreator<ParceledListSlice>() {
        @Override
        public ParceledListSlice createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return null;
        }
        @Override
        public ParceledListSlice createFromParcel(Parcel parcel) {
            return null;
        }
        @Override
        public ParceledListSlice[] newArray(int i) {
            return new ParceledListSlice[0];
        }
    };
}
