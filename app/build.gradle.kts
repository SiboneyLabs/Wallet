import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

// Load the keystore properties from the keystore.properties file
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

// AdMob IDs are kept outside the repo: local.properties (gitignored) or environment variables.
// Debug builds fall back to Google's public test IDs; release builds require the real ones.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) FileInputStream(file).use { load(it) }
}
fun secret(key: String, envName: String): String? =
    (localProperties.getProperty(key) ?: System.getenv(envName))?.takeIf { it.isNotBlank() }

val testAdmobAppId = "ca-app-pub-3940256099942544~3347511713"
val testInterstitialId = "ca-app-pub-3940256099942544/1033173712"
val releaseAdmobAppId = secret("admob.appId", "ADMOB_APP_ID")
val releaseInterstitialId = secret("admob.interstitialId", "ADMOB_INTERSTITIAL_ID")

gradle.taskGraph.whenReady {
    val releaseRequested = allTasks.any { it.project == project && it.name.contains("Release") }
    if (releaseRequested && (releaseAdmobAppId == null || releaseInterstitialId == null)) {
        throw GradleException(
            "Missing AdMob IDs for a release build. Set admob.appId and admob.interstitialId " +
                "in local.properties (or ADMOB_APP_ID / ADMOB_INTERSTITIAL_ID env vars)."
        )
    }
}

configurations.all {
    resolutionStrategy {
        force(libs.androidx.activity.compose)
        force("androidx.fragment:fragment:1.9.1")
    }
}

android {
    namespace = "com.tainoapp.wallet"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    defaultConfig {
        applicationId = "com.tainoapp.wallet"
        minSdk = 24
        targetSdk = 36
        versionCode = 22
        versionName = "2.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            manifestPlaceholders["admobAppId"] = testAdmobAppId
            buildConfigField("String", "ADMOB_INTERSTITIAL_ID", "\"$testInterstitialId\"")
        }
        release {
            manifestPlaceholders["admobAppId"] = releaseAdmobAppId ?: testAdmobAppId
            buildConfigField(
                "String", "ADMOB_INTERSTITIAL_ID", "\"${releaseInterstitialId ?: testInterstitialId}\""
            )
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            ndk.debugSymbolLevel = "FULL" // Include native debug symbols
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// FIX: Replaced deprecated kotlinOptions with the new compilerOptions DSL
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.fragment:fragment:1.9.1")
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.play.services.ads)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
