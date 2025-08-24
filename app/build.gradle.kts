import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

// Initialize Properties
val properties = Properties()
try {
    // Load keystore
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    properties.load(FileInputStream(keystorePropertiesFile))
} catch (e: IOException) {
    // We don't have release keys, ignoring
    e.printStackTrace()
}

// Release key path, password, alias
val releaseKeyStorePath: String? = properties.getProperty("RELEASE_KEY_STORE_PATH")
val releaseKeyStorePathPassword: String? = properties.getProperty("RELEASE_KEY_STORE_PATH_PASSWORD")
val releaseKeyStorePathAlias: String? = properties.getProperty("RELEASE_KEY_STORE_PATH_ALIAS")

// Debug key path, password, alias
val debugKeyStorePath: String? = properties.getProperty("DEBUG_KEY_STORE_PATH")
val debugKeyStorePathPassword: String? = properties.getProperty("DEBUG_KEY_STORE_PATH_PASSWORD")
val debugKeyStorePathAlias: String? = properties.getProperty("DEBUG_KEY_STORE_PATH_ALIAS")

android {
    namespace = "com.app.fyra"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.app.fyra"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (releaseKeyStorePath != null) {
                keyAlias = releaseKeyStorePathAlias
                keyPassword = releaseKeyStorePathPassword
                storeFile = file(releaseKeyStorePath)
                storePassword = releaseKeyStorePathPassword
            } else {
                println("Release key store path is null. Signing configuration cannot be created.")
            }
        }

        create("debug2") {
            if (debugKeyStorePath != null) {
                keyAlias = debugKeyStorePathAlias
                keyPassword = debugKeyStorePathPassword
                storeFile = file(debugKeyStorePath)
                storePassword = debugKeyStorePathPassword
            } else {
                println("Debug key store path is null. Signing configuration cannot be created.")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Signing configuration for release build
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            // Signing configuration for debug build
            signingConfig = signingConfigs.getByName("debug2")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.glide)
    implementation(libs.konfetti.xml)

    implementation(libs.flexbox)
}