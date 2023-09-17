package com.longkd.chatai

import com.longkd.base_android.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

/**
 * @Author: longkd
 * @Since: 10:06 - 12/08/2023
 */

@HiltAndroidApp
class MyApp : BaseApplication() {

    companion object {
        lateinit var instance: com.longkd.chatai.MyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        com.longkd.chatai.MyApp.Companion.instance = this
    }
}