package com.longkd.buildsrc.common

/**
 * @Author: longkd
 * @Date: 3:43 PM - 8/24/2023
 */
object Libraries {

    const val androidCore = "androidx.core:core-ktx:${Versions.androidCore}"
    const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val jUnit = "junit:junit:${Versions.jUnit}"

    //  androidTestImplementation()
    const val testJUnit = "androidx.test.ext:junit:${Versions.testJUnit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"


    const val fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.fragment}"
    const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigationUi}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    const val gsonConvert = "com.squareup.retrofit2:converter-gson:${Versions.gsonConvert}"
    const val loggingInterception =
        "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterception}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
}

