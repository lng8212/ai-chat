package com.longkd.buildsrc.app

/**
 * @Author: longkd
 * @Date: 5:17 PM - 8/24/2023
 */
object AppDependencies {
    val android = listOf(
        AppLibraries.hilt,
        AppLibraries.glide,
        AppLibraries.lottie
    )

    val kapt = listOf(
        AppLibraries.hiltCompiler
    )
}