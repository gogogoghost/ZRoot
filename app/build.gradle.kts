//plugins {
//    id "com.android.application"
//    id "kotlin-android"
////    id "zroot-builder"
//}
plugins {
    id("com.android.application")
    id("kotlin-android")
//    id("zroot-builder")
}

//zRoot{
//    filter(
//        arrayListOf(
//            "site/zbyte/root/app/remote/.*",
//            "site/zbyte/root/app/IWorker.*"
//        ))
//    mainClass("site.zbyte.root.app.remote.Worker")
//    debug(":runner")
//}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "site.zbyte.root.app"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    sourceSets {
        getByName("main").assets.srcDirs(layout.buildDirectory.dir("/assets"))
    }
    namespace = "site.zbyte.root.app"
    buildFeatures {
        aidl = true
    }
}

dependencies {

//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
//    implementation "androidx.core:core-ktx:1.7.0"
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation(project(":sdk"))

    compileOnly(project(":hidden_api"))

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
}