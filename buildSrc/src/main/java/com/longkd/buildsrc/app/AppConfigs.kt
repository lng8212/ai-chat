package com.longkd.buildsrc.app

import org.gradle.api.JavaVersion

/**
 * @Author: longkd
 * @Date: 5:11 PM - 8/24/2023
 */
object AppConfigs {
    const val appName = "com.longkd.chatai"
    const val minSDK = 24
    const val complieSDK = 33
    const val targetSDK = 33
    const val versionCode = 1
    const val versionName = "1.0"
    const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    val javaVersion = JavaVersion.VERSION_17
    const val jvmTarget = "17"
}