import com.longkd.buildsrc.app.AppConfigs
import com.longkd.buildsrc.app.AppDependencies
import com.longkd.buildsrc.common.GroupDependencies

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = AppConfigs.appName
    compileSdk = AppConfigs.complieSDK

    defaultConfig {
        applicationId = AppConfigs.appName
        minSdk = AppConfigs.minSDK
        targetSdk = AppConfigs.targetSDK
        versionCode = AppConfigs.versionCode
        versionName = AppConfigs.versionName

        testInstrumentationRunner = AppConfigs.testInstrumentationRunner
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "API_KEY", "\"\"")
        buildConfigField("String", "BASE_URL", "\"https://reqres.in/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = AppConfigs.javaVersion
        targetCompatibility = AppConfigs.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfigs.jvmTarget
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildFeatures {
            buildConfig = true
        }
    }
}

dependencies {
    implementation(project(mapOf("path" to ":base-android")))
    GroupDependencies.android.forEach {
        implementation(it)
    }
    GroupDependencies.testImpl.forEach {
        androidTestImplementation(it)
    }

    AppDependencies.android.forEach {
        implementation(it)
    }

    implementation("com.github.donkingliang:ConsecutiveScroller:4.6.4")

    AppDependencies.kapt.forEach {
        kapt(it)
    }
}