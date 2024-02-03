// IRemote.aidl
package site.zbyte.root.sdk;

// Declare any non-default types here with import statements

interface IRemote {
    //注册一个watcher 用来跟踪app是否死亡 然后runner退出
    void registerWatcher(IBinder binder);
    //获取worker
    IBinder getWorker();
    //获取Caller 专门接收transact的
    IBinder getCaller();
    //获取transact code
    int getTransactCode(String clsName,String fieldName);
    //content provider专用call
    Bundle callContentProvider(IBinder contentProvider,String packageName,String authority,String methodName,String key,in Bundle data);
    //start new remote process
    int forkProcess(int uid);
    //获取当前进程uid
    int getUid();
}