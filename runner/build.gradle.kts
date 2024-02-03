
plugins {
    id("com.android.library")
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 25
        lint{
            targetSdk = 34
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles "consumer-rules.pro"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "site.zbyte.root.runner"
}

dependencies {
    compileOnly(project(":hidden_api"))
}


tasks.register<Delete>("removeLibs") {
    delete(buildDir.path + "/libs")
}

tasks.register<Jar>("makeJar") {
    dependsOn("removeLibs", "syncReleaseLibJars")
    from(
            zipTree("build/intermediates/aar_main_jar/release/classes.jar")
    )
    exclude("**/*/BuildConfig.class")
    archiveFileName = "runner.jar"
//    archiveName = "runner.jar"
}