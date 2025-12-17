import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.2.20"
}

kotlin {
    val ktorVersion = "3.2.3"

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
//    listOf(
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation("io.ktor:ktor-client-android:${ktorVersion}")
            implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
            implementation("androidx.activity:activity-compose:1.8.1")

            implementation("com.google.mlkit:text-recognition:16.0.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

            implementation(compose.material3) // already present in commonMain, can also be here
            implementation("com.google.android.material:material:1.10.0") // Add this for XML Material3 themes


            // Camera dependencies
            implementation("androidx.camera:camera-core:1.3.1")
            implementation("androidx.camera:camera-camera2:1.3.1")
            implementation("androidx.camera:camera-lifecycle:1.3.1")
            implementation("androidx.camera:camera-view:1.3.1")
            implementation("androidx.compose.ui:ui-viewbinding:1.5.4")

            // Permissions
            implementation("com.google.accompanist:accompanist-permissions:0.32.0")
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

            // Navigation for Compose Multiplatform
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")

            // Lifecycle for Compose
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

            // For TensorFlow Lite (when you implement the actual functionality)
            implementation("org.tensorflow:tensorflow-lite:2.17.0")

            // For file downloading and storage
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.scinforma.sharedvision.generated.resources"
    generateResClass = always
}

android {
    namespace = "com.scinforma.sharedvision"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.scinforma.sharedvision"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 4
        versionName = "1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            // Enables code-related app optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true

            proguardFiles(
                // Default file with automatically generated optimization rules.
                getDefaultProguardFile("proguard-android-optimize.txt"),
            )

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/androidMain/assets")  // ADD THIS LINE
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

