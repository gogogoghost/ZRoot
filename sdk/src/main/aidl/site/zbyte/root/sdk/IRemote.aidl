// IRemote.aidl
package site.zbyte.root.sdk;

// Declare any non-default types here with import statements

interface IRemote {
    //注册一个watcher 用来跟踪app是否死亡 然后runner退出
    void registerWatcher(IBinder binder);
    //将binder发到远程返回一个代理
    IBinder obtainBinderProxy(IBinder src);
    //获取transact code
    int getTransactCode(String clsName,String fieldName);
    //start new remote process
//    int forkProcess(int uid);
    //获取当前进程uid
    int getUid();
}