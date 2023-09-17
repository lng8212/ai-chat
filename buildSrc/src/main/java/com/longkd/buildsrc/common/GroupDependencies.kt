package com.longkd.buildsrc.common

object GroupDependencies {
    val android = listOf(
        Libraries.androidCore,
        Libraries.lifecycle,
        Libraries.appCompat,
        Libraries.material,
        Libraries.constraintLayout,
        Libraries.jUnit,
        Libraries.fragment,
        Libraries.navigationUi,
        Libraries.coroutines,
        Libraries.gson,
        Libraries.retrofit,
        Libraries.gsonConvert,
        Libraries.loggingInterception,
        Libraries.okhttp
    )

    val testImpl = listOf(
        Libraries.testJUnit,
        Libraries.espresso
    )
}

