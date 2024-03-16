# ZRoot
[![](https://jitpack.io/v/site.zbyte/zroot.svg)](https://jitpack.io/#site.zbyte/zroot)

ZRoot is a library that makes it easy to use root on Android, such as calling system service with root privilege.

## Usage

See [sample](https://github.com/gogogoghost/ZRoot/blob/master/app/src/main/java/site/zbyte/root/app/MainActivity.kt) or user guide below.

#### Hidden API

Before using ZRoot, your project needs a hidden_api module to provide some hidden API.

Create a module named hidden_api, add classes that you need into the module, like IActivityManager ServiceManager.

Add dependency to your application module: 
```groovy
compileOnly project(path: ':hidden_api')
```

#### Add Dependency

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency	 		

```groovy
dependencies {
    implementation 'site.zbyte:zroot:${version}'
}
```

Add content provider into Application tag of **AndroidManifest.xml**

```xml
<provider
    android:name="site.zbyte.root.sdk.Provider"
    android:authorities="${applicationId}.zroot"
    android:multiprocess="false"
    android:enabled="true"
    android:exported="true"
    android:permission="android.permission.INTERACT_ACROSS_USERS_FULL"/>
```

#### Start ZRoot

```kotlin
/**
 * start by blocked api with 5000ms timeout
 */
val zRoot = Starter(
        //context
        ctx,
        //remote process uid: 0 for root or 2000 for shell
        0
).startBlocked(
        //timeout
        5000
)
```

#### Call system service

```kotlin
/**
 * get remote service binder of activity
 * it is a proxy with root to ServiceManager.getService("activity")
 */
val service = zRoot.getRemoteService("activity")

/**
 * convert to IActivityManager
 */
val mAm = IActivityManager.Stub.asInterface(service)

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
zRoot.callContentProvider(
    holder.provider.asBinder(),
    "android",
    authority,
    "PUT_secure",
    "accessibility_enabled",
    bundle
)
```

#### Use an existing binder with root
```kotlin
/**
 * proxy an existing local binder
 */
val existingBinder=ServiceManager.getService("activity")

/**
 * get a proxy
 */
val proxyBinder=zRoot.makeBinderProxy(existingBinder)

/**
 * convert to IActivityManager
 */
val mAm = IActivityManager.Stub.asInterface(service)

/**
 * call remote api
 * mAm.broadcastIntent...
 * mAm.startActivity...
 */
```

#### Run custom code(service) on remote root process

See [ZRoot-builder](https://github.com/gogogoghost/ZRoot-builder).

## AndroidHiddenApiBypass

We need to use hidden APIs like IActivityManager, ServiceManager, etc.
This kind of operation will be blocked from Android 9(P), using [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass) to solve it

## Run the demo app

1. Download source code

    ``` git clone --recurse-submodules git@github.com:gogogoghost/ZRoot.git ```

    Be sure the submodule(`builder`) was downloaded.

2. Enter the `ZRoot/builder` directory and run the following command.

    For Linux: 
    ``` ./gradlew uploadArchives  ```

    For Windows:
    ``` gradlew.bat uploadArchives ```


3. Open the project with Android Studio, and you can run the `app` module on your device that had root privilege.

## Contribute
There are 4 modules in the project: `app`, `hidden_api`, `runner`, `sdk`.

`app`: It's a demo application to show the sample code.

`hidden_api`: If you often code with some root privilege, you probably know what a hidden API is. Otherwise, ignore it, and good luck.

`runner`: This module is used to help `sdk` to run remote code with root privilege.

`sdk`: Here is the core of project.

Waiting to be added more details...

