package android.content;

import android.database.Cursor;
import android.os.Bundle;
import android.os.IInterface;

public interface IContentProvider extends IInterface {
    Bundle call(String callingPkg, String method,
                               String arg, Bundle extras);

    //10
    Bundle call(String callingPkg, String authority,String method,
                String arg, Bundle extras);

    //11
    Bundle call(String callingPkg,String attributionTag, String authority,String method,
                String arg, Bundle extras);

    //12
    Bundle call(AttributionSource attributionSource,String authority,String method,
                String arg, Bundle extras);
}
