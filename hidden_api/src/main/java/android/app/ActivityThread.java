package android.app;

import android.os.IBinder;

public class ActivityThread {
    public static ActivityThread systemMain() {
        return null;
    }

    public ApplicationThread getApplicationThread()
    {
        return null;
    }

    private void attach(boolean system, long startSeq) {

    }

    public static void main(String[] args) {}

    private class ApplicationThread extends IApplicationThread.Stub{

        @Override
        public IBinder asBinder() {
            return null;
        }
    }
}
