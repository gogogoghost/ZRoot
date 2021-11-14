# ZRoot
Easy to use root on Android. For calling system service and more.

## Usage

See some [sample](https://github.com/gogogoghost/ZRoot/blob/master/app/src/main/java/site/zbyte/root/app/MainActivity.kt) or user guide below.

#### Hidden api

Before using ZRoot
Your project need a hidden_api module to provide some api hidden

Create a module named hidden_api
Add classes what you need into module, like:IActivityManager ServiceManager

Add dependency to your application module
```groovy
compileOnly project(path: ':hidden_api')
```

#### Add Dependency

```groovy
implementation 'wait for publish...'
```

#### Start ZRoot

```kotlin
/**
 * create a instance
 */
val zRoot=ZRoot(this)

/**
 * start by blocked api with 5000ms timeout
 * return true if succeed
 */
val succeed=zRoot.startBlock(5000)

/**
 * start by async api with 5000ms timeout
 */
zRoot.start(5000){it->
    //it will be true if succeed
}
```

#### Call system service

```kotlin
/**
 * get remote service binder of activity
 * it is a proxy with root to ServiceManager.getService("activity")
 */
val service=zRoot.getRemoteService("activity")

/**
 * convert to IActivityManager
 */
val mAm=IActivityManager.Stub.asInterface(service)

/**
 * call remote api
 * mAm.broadcastIntent...
 * mAm.startActivity...
 */
```

#### Call ContentProvider

```kotlin
/*
 * get ContentProvider from activity service via root
 */
val authority = "settings"
val holder = mAm.getContentProviderExternal(authority, 0, null,null)

/**
 * it equivalent to:
 *     settings put secure accessibility_enabled 1
 * via root user
 */
val bundle = Bundle()
bundle.putString("value", "1")
zRoot.getExecutor()!!.callContentProvider(
    holder.provider.asBinder(),
    "android",
    authority,
    "PUT_secure",
    "accessibility_enabled",
    bundle
)
```

#### Run custom code(service) on remote root process

See [ZRoot-builder](https://github.com/gogogoghost/ZRoot-builder)

## AndroidHiddenApiBypass

We need use hidden api like: IActivityManager,ServiceManager
This kind of operation will be blocked from Android 9(P)
Use [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass) to solve it