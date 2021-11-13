package android.content;

import android.app.IActivityManager;
import android.os.Binder;
import android.os.Bundle;

public interface IIntentReceiver {
    public static abstract class Stub extends Binder implements IIntentReceiver {
        public void performReceive(Intent intent, int resultCode, String data, Bundle extras,
                                   boolean ordered, boolean sticky, int sendingUser){};
    }
}
