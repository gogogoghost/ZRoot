import java.util.Properties
import java.io.FileInputStream
import java.util.Locale

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}


android {
    compileSdk = 35

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    sourceSets {
        getByName("main").assets.srcDirs(layout.buildDirectory.dir("assets"))
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

val compileRunner=tasks.register<Exec>("compileRunner"){
    inputs.file(File(rootDir,"runner/build/libs/runner.jar"))
    outputs.file(File(rootDir,"runner/build/libs/classes.dex"))

    dependsOn(":runner:makeJar")
    val d8File=sdkDir + "/build-tools/" + buildToolVersion + "/d8" + (if (isWindows)".bat" else "")
    val srcFile = rootDir.path + "/runner/build/libs/runner.jar"
    val outDir=rootDir.path + "/runner/build/libs/"

    executable(d8File)
    args("--no-desugaring","--release","--output",outDir,srcFile)
}

val copyRunner=tasks.register<Copy>("copyRunner") {
    inputs.file(File(rootDir,"runner/build/libs/classes.dex"))
    outputs.file(layout.buildDirectory.file("assets/runner.dex"))

    dependsOn(compileRunner)
    doFirst {
        layout.buildDirectory.dir("assets").get().asFile.mkdirs()
    }
    from(rootDir.path + "/runner/build/libs/classes.dex")
    into(layout.buildDirectory.dir("assets"))
    rename("classes.dex","runner.dex")
}



tasks.register<Copy>("copyAIDL2runner") {
    dependsOn(":sdk:compileReleaseAidl")
    from(layout.buildDirectory.dir("/generated/aidl_source_output_dir/release/out/"))
    into(rootDir.path + "/runner/src/main/java/")
}


afterEvaluate {
    tasks.named("generateDebugAssets"){
        dependsOn(copyRunner)
    }
    tasks.named("generateReleaseAssets"){
        dependsOn(copyRunner)
    }
    // I don't know why should I add these
    tasks.named("lintVitalAnalyzeRelease"){
        dependsOn(copyRunner)
    }
    tasks.named("generateReleaseLintVitalModel"){
        dependsOn(copyRunner)
    }
    tasks.named("generateReleaseLintModel"){
        dependsOn(copyRunner)
    }
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