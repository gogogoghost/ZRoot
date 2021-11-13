package android.content;

import android.os.Binder;
import android.os.IBinder;

public abstract class ContentProviderNative extends Binder implements IContentProvider{
    static public IContentProvider asInterface(IBinder obj){
        return null;
    }
}
