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
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

buildscript{
    dependencies{
//        classpath("site.zbyte:zroot-builder:3.2.1")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}