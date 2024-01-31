import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}


android {
    compileSdk = 34

    defaultConfig {
        minSdk = 25
        targetSdk = 34
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
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    isWindows = true
}

var sdkDir:String? = ""

val localProperties = File(project.rootDir, "local.properties")

if(localProperties.exists()){
    val properties = Properties()
    properties.load(FileInputStream(localProperties))
    sdkDir = properties.getProperty("sdk.dir")
}else{
    sdkDir=System.getenv()["ANDROID_HOME"]
}

tasks.register<Exec>("copyRunner") {
    dependsOn(":runner:makeJar")
    doFirst {
        File(project.buildDir.path + "/assets").mkdirs()
    }
    val buildToolVersion = android.buildToolsVersion
    val dexFile = sdkDir + "/build-tools/" + buildToolVersion + "/dx" + (if (isWindows)".bat" else "")

    val srcFile = rootDir.path + "/runner/build/libs/runner.jar"
    val outFile = project.buildDir.path + "/assets/runner.dex"

    val assetsDir = File(outFile).parentFile
    if (!assetsDir.exists()) {
        assetsDir.mkdirs()
    }

    executable(dexFile)
    args(arrayOf<String>("--dex", "--output", outFile, srcFile))
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