package com.longkd.chatgpt_openai.open.client

import android.os.Build
import com.longkd.chatgpt_openai.BuildConfig
import com.google.android.gms.common.annotation.KeepName

object OpenAiUtils {
    @KeepName
    @JvmStatic
    fun padStart(value: String, length: Int, padChar: Char = ' '): String {
        return value.padStart(length, padChar)
    }
    val userAgent: String =
        "VersionApp:${BuildConfig.VERSION_NAME}/" +
                "AppId:${BuildConfig.APPLICATION_ID}/" +
                "Build:${BuildConfig.VERSION_CODE}/" +
                "VersionSDK:${Build.VERSION.SDK_INT}/" +
                "OS:Android/" +
                "UserAgent:${System.getProperty("http.agent")}"
}