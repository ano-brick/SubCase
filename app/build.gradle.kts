import java.io.ByteArrayOutputStream


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("org.lsposed.lsplugin.apksign") version "1.4"
}

apksign {
    storeFileProperty = "KEYSTORE_FILE"
    storePasswordProperty = "KEYSTORE_PASSWORD"
    keyAliasProperty = "KEY_ALIAS"
    keyPasswordProperty = "KEY_PASSWORD"
}

val verCode: Int? by rootProject
val verName: String? by rootProject

val androidStoreFile: String? by rootProject
val androidStorePassword: String? by rootProject
val androidKeyAlias: String? by rootProject
val androidKeyPassword: String? by rootProject

android {
    namespace = "ano.subcase"
    compileSdk = 35

    defaultConfig {
        applicationId = "ano.subcase"
        minSdk = 26
        targetSdk = 35
        versionCode = verCode
        versionName = verName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.addAll(
                arrayOf(
                    //"armeabi-v7a",
                    "arm64-v8a",
                    // "x86", "x86_64"
                )
            )
        }

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }

    buildTypes {
            debug {}
            release {
                isMinifyEnabled = true
                isShrinkResources = true

                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
            setProperty("archivesBaseName", "SubCase-$verName-$verCode")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    sourceSets["main"].jniLibs.srcDir("jniLibs")
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.timber)

    implementation(libs.okhttp)

    //com.google.accompanist:accompanist-systemuicontroller
    implementation(libs.accompanist.systemuicontroller)
}

// BUILD SCRIPT
afterEvaluate {
    val bc = tasks.register("buildCoreTask") {
        doLast {
            println("buildCore task is being executed")
            buildCore()
        }
    }

    for (task in tasks) {
        if (task.name.contains("configureCMake")) {
            task.dependsOn(bc)
        }
    }
}

fun buildCore() {
    val ndkVersion = project.android.ndkVersion
    val ndkPath = project.android.ndkDirectory
    println("[*] NDK Version: $ndkVersion")
    println("[*] NDK Path: $ndkPath")

    val toolchainRoot = ndkPath.resolve("toolchains/llvm/prebuilt/${getOsType()}/bin")
    println("[*] Toolchain Root: $toolchainRoot")


    val jnilibs = project.rootDir.resolve("app/src/main/jniLibs")

    val abis = arrayOf(
        "armeabi-v7a", "arm64-v8a",
        //  "x86", "x86_64"
    )
    abis.forEach { abi ->
        println("[*] Building for $abi")

        val outputStream = ByteArrayOutputStream()
        exec {
            workingDir = File("../corespace/ffi")

            environment["GOARCH"] = getGoArch(abi)
            environment["CC"] = toolchainRoot.resolve(getCC(abi)).absolutePath
            environment["GOOS"] = "android"
            environment["CGO_ENABLED"] = "1"

            commandLine = listOf(
                "go",
                "build",
                "-o",
                "$jnilibs/$abi/libcore.so",
                "-trimpath",
                "-ldflags",
                "-w -s -buildid=",
                "-buildmode=c-shared",
                "-buildvcs=false"
            )

            standardOutput = outputStream
        }
        print(outputStream.toString())
    }
}

fun getOsType(): String {
    val osName = System.getProperty("os.name").lowercase()

    return when {
        "windows" in osName -> "windows-x86_64"
        "mac" in osName -> "darwin-x86_64"
        "linux" in osName -> "linux-x86_64"
        else -> throw GradleException("Unsupported Build OS ${System.getProperty("os.name")}")
    }
}

fun getGoArch(abi: String): String {
    return when (abi) {
        "armeabi-v7a" -> "arm"
        "arm64-v8a" -> "arm64"
        "x86" -> "386"
        "x86_64" -> "amd64"
        else -> throw GradleException("Unsupported arch $abi")
    }
}

fun getCC(abi: String): String {
    return when (abi) {
        "armeabi-v7a" -> "armv7a-linux-androideabi26-clang"
        "arm64-v8a" -> "aarch64-linux-android26-clang"
        "x86" -> "i686-linux-android26-clang"
        "x86_64" -> "x86_64-linux-android26-clang"
        else -> throw GradleException("Unsupported arch $abi")
    }
}