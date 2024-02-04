import java.util.Properties
import java.io.FileInputStream
import java.util.Locale

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}


android {
    compileSdk = 34

    defaultConfig {
        minSdk = 25
        lint{
            targetSdk = 34
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main").assets.srcDirs(buildDir.path+"/assets")
    }
    ndkVersion = "25.0.8775105"

    externalNativeBuild {
        cmake {
            path("CMakeLists.txt")
        }
    }
    namespace = "site.zbyte.root.sdk"
    buildFeatures {
        aidl = true
    }
//    applicationVariants.all { variant ->
//        variant.outputs.all {
//            outputFile.name = "root-driver" + "-${buildType.name}" + ".aar"
//        }
//        false
//    }
}

dependencies {
//    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    compileOnly(project(":hidden_api"))
}

var isWindows = false
if (System.getProperty("os.name").lowercase(Locale.getDefault()).contains("windows")) {
    isWindows = true
}

val localProperties = File(project.rootDir, "local.properties")

val sdkDir = if(localProperties.exists()){
    val properties = Properties()
    properties.load(FileInputStream(localProperties))
    properties.getProperty("sdk.dir")
}else{
    System.getenv()["ANDROID_HOME"]
}

val buildToolVersion = android.buildToolsVersion

tasks.register<Exec>("compileRunner"){
    dependsOn(":runner:makeJar")
    val d8File=sdkDir + "/build-tools/" + buildToolVersion + "/d8" + (if (isWindows)".bat" else "")
    val srcFile = rootDir.path + "/runner/build/libs/runner.jar"
    val outDir=rootDir.path + "/runner/build/libs/"

    executable(d8File)
    args("--release","--output",outDir,srcFile)
}

tasks.register<Copy>("copyRunner") {
    dependsOn("compileRunner")
    doFirst {
        File(buildDir.path + "/assets").mkdirs()
    }
    from(rootDir.path + "/runner/build/libs/classes.dex")
    into(buildDir.path+"/assets/")
    rename("classes.dex","runner.dex")
}

tasks.configureEach {
    if (name == "generateDebugAssets" || name == "generateReleaseAssets") {
        dependsOn("copyRunner")
    }
}

tasks.register<Copy>("copyAIDL2runner") {
    dependsOn(":sdk:compileReleaseAidl")
    from(buildDir.path + "/generated/aidl_source_output_dir/release/out/")
    into(rootDir.path + "/runner/src/main/java/")
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release"){
                from(components["release"])
                groupId = "site.zbyte"
                artifactId = "zroot"
                version = "2.11"
            }
        }
    }
}