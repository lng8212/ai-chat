/*
 * Created by longkd on 12/1/23, 9:45 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 12/1/23, 9:45 PM
 */

package com.longkd.chatgpt_openai.feature.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.longkd.chatgpt_openai.R

/**
 * @Author: longkd
 * @Since: 21:45 - 01/12/2023
 */
class WidgetViewModel : ViewModel() {
    fun clickWidget4x1(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(activity)
            val myProvider =
                activity.let { it1 ->
                    ComponentName(
                        it1,
                        WidgetTopic::class.java
                    )
                }
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        activity,
                        6666,
                        Intent(),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        activity,
                        6666,
                        Intent(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                if (myProvider != null) {
                    appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                }
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.device_not_support),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                activity,
                activity.getString(R.string.text_des_guide_widget),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun clickWidget4x2(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(activity)
            val myProvider =
                activity.let { it1 ->
                    ComponentName(
                        it1,
                        WidgetNoTopic::class.java
                    )
                }
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        activity,
                        6666,
                        Intent(),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        activity,
                        6666,
                        Intent(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                if (myProvider != null) {
                    appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                }
            } else {
                Toast.makeText(
                    activity,
                    activity.getString(R.string.device_not_support),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                activity,
                activity.getString(R.string.text_des_guide_widget),
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}