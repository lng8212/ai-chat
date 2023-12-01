/*
 * Created by longkd on 12/1/23, 8:54 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 12/1/23, 8:54 PM
 */

package com.longkd.chatgpt_openai.feature.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import com.longkd.chatgpt_openai.BuildConfig
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.bubble.FloatingBubbleService
import com.longkd.chatgpt_openai.base.bubble.isDrawOverlaysPermissionGranted
import com.longkd.chatgpt_openai.feature.language.LanguageActivity
import com.longkd.chatgpt_openai.service.BubbleService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 20:54 - 01/12/2023
 */

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    fun openLanguage(activity: Activity) {
        val mIntent = Intent(activity, LanguageActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        mIntent.data = activity.intent?.data
        mIntent.action = activity.intent?.action
        activity.intent?.extras?.let { mIntent.putExtras(it) }
        activity.startActivity(mIntent)

    }

    fun openBubble(
        activity: Activity,
        isEnable: Boolean,
        resultLauncherOverlay: ActivityResultLauncher<Intent>,
    ) {
        if (isEnable) {
            if (activity.isDrawOverlaysPermissionGranted()) {
                val intent = Intent(activity, BubbleService::class.java)
                if (!FloatingBubbleService.isRunning()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity.startForegroundService(intent)
                    } else {
                        activity.startService(intent)
                    }
                }
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.packageName)
                )
                resultLauncherOverlay.launch(intent)
            }

        } else {
            if (FloatingBubbleService.isRunning()) {
                val intent = Intent(activity, BubbleService::class.java)
                activity.stopService(intent)
            }
        }
    }

    fun getVersion(context: Context): String {
        return context.resources?.getString(R.string.text_version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}