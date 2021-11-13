package android.os;

import android.content.pm.UserInfo;

import java.util.List;

public interface IUserManager {

    List<UserInfo> getUsers( boolean excludeDying);

    List<UserInfo> getUsers(boolean excludePartial, boolean excludeDying,
                            boolean excludePreCreated);

    public static abstract class Stub extends Binder implements IUserManager {
        public static IUserManager asInterface(IBinder binder){
            return null;
        }
    }
}
