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
    compileSdk = 34

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
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main").assets.srcDirs(buildDir.path+"/assets")
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