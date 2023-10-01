package com.longkd.chatgpt_openai.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.feature.splash.SplashActivity


class RemindAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFY_REMIND_RECENT = 8089
        const val ACTION_DAILY = "com.longkd.chatgpt_openai.DAILY_NOTIFY_ACTION"
    }

    fun getStringRes(context: Context?, @StringRes res: Int): String? {
        return try {
            context?.resources?.getString(res)
        } catch (e: Exception) {
            null
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            ACTION_DAILY -> {
                val random = (1..6).random()
                var title = context.getString(R.string.title_notify_daily_1)
                var content = context.getString(R.string.title_content_daily_1)
                when(random){
                    1 -> {
                        title = context.getString(R.string.title_notify_daily_1)
                        content = context.getString(R.string.title_content_daily_1)
                    }
                    2 -> {
                        title = context.getString(R.string.app_name)
                        content = context.getString(R.string.title_content_daily_2)
                    }
                    3 -> {
                        title = context.getString(R.string.title_notify_daily_3)
                        content = context.getString(R.string.title_content_daily_3)
                    }
                    4 -> {
                        title = context.getString(R.string.title_notify_daily_4)
                        content = context.getString(R.string.title_content_daily_4)
                    }
                    5 -> {
                        title = context.getString(R.string.title_notify_daily_5)
                        content = context.getString(R.string.title_content_daily_5)
                    }
                    6 -> {
                        title = context.getString(R.string.app_name)
                        content = context.getString(R.string.title_content_daily_6)
                    }
                }
                kotlin.runCatching {
                    sendNotificationNew(context, title, content, ACTION_DAILY)
                }
            }
        }
    }


    @SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
    fun sendNotificationNew(
        context: Context, title: String, messageBody: CharSequence, action : String
    ) {

        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = action

        val resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                NOTIFY_REMIND_RECENT,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context, NOTIFY_REMIND_RECENT, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        val channelId = NOTIFY_REMIND_RECENT.toString()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFY_REMIND_RECENT)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(messageBody)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setSmallIcon(R.drawable.ic_notify_small)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setWhen(System.currentTimeMillis()).setFullScreenIntent(
                PendingIntent.getBroadcast(
                    context,
                    898989,
                    Intent("RemindRecentAlarmReceiver"),
                    PendingIntent.FLAG_UPDATE_CURRENT
                ), true
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, ACTION_DAILY, NotificationManager.IMPORTANCE_HIGH
            )
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.description = ACTION_DAILY
            notificationManager.createNotificationChannel(channel)
        }

        kotlin.runCatching {
            notificationManager.notify(
                NOTIFY_REMIND_RECENT, notificationBuilder.build()
            )
        }
    }
}