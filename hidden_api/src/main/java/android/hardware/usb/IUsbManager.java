package android.hardware.usb;

import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public interface IUsbManager {

    public void grantDevicePermission(UsbDevice device, int uid);

    public void getDeviceList(Bundle devices);

    public static abstract class Stub extends Binder implements IUsbManager {
        public static IUsbManager asInterface(IBinder binder){
            return null;
        }
    }
}
