package site.zbyte.root.app.remote;

import android.content.Intent;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbDevice;
import android.os.Binder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;

import site.zbyte.root.app.IWorker;

public class Worker extends IWorker.Stub {
    @Override
    public String work() throws RemoteException {
        return "My uid is:"+Process.myUid();
    }
}
