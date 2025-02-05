
plugins {
    id("com.android.library")
}

android {
    compileSdk = 35

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
    namespace = "site.zbyte.root.runner"
}

dependencies {
    compileOnly(project(":hidden_api"))
}


tasks.register<Delete>("removeLibs") {
    delete(layout.buildDirectory.dir("/libs"))
}

tasks.register<Jar>("makeJar") {
    inputs.files(layout.projectDirectory.dir("runner/src"))
    outputs.file(layout.buildDirectory.file("libs/runner.jar"))

    dependsOn("removeLibs", "syncReleaseLibJars")
    from(
            zipTree("build/intermediates/aar_main_jar/release/syncReleaseLibJars/classes.jar")
    )
    exclude("**/*/BuildConfig.class")
    archiveFileName = "runner.jar"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}