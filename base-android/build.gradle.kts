import com.longkd.buildsrc.base.BaseConfigs
import com.longkd.buildsrc.common.GroupDependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = BaseConfigs.namspace
    compileSdk = BaseConfigs.complieSDK

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = BaseConfigs.javaVersion
        targetCompatibility = BaseConfigs.javaVersion
    }

    kotlinOptions {
        jvmTarget = BaseConfigs.jvmTarget
    }

}

dependencies {
    GroupDependencies.android.forEach {
        implementation(it)
    }
    GroupDependencies.testImpl.forEach {
        androidTestImplementation(it)
    }
}