// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    ext.kotlin_version = '1.6.21'
//    repositories {
//        maven {
//            url "https://maven.aliyun.com/repository/google"
//        }
//        maven {
//            url "https://maven.aliyun.com/repository/public"
//        }
//        maven { url 'https://jitpack.io' }
//        google()
//        maven {
//            url uri(rootDir.path + '../ZRoot-Builder/build/repo')
//        }
//    }
//    dependencies {
//        classpath 'com.android.tools.build:gradle:8.2.2'
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//
//        classpath "site.zbyte:zroot-builder:3.0.0"
//    }
//}
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

buildscript{
    dependencies{
        classpath("site.zbyte:zroot-builder:3.0.0")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register<Copy>("copySDK") {
    dependsOn(":sdk:bundleReleaseAar")
    from(rootDir.path + "/sdk/build/outputs/aar/root-driver-release.aar")
    into(rootDir.path + "/build")
    rename("root-driver-release.aar", "root-driver.aar")
}

tasks.register("export") {
    dependsOn("copySDK")
}