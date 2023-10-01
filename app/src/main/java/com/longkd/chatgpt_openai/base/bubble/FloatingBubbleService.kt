package com.longkd.chatgpt_openai.base.bubble

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.longkd.chatgpt_openai.R


abstract class FloatingBubbleService : FloatingBubbleServiceConfig() {


    companion object {

        private const val REQUEST_CODE_STOP_PENDING_INTENT = 1

        private var isRunning = false

        /**
         * this function works as expected if only one bubble showed at a time, multiple bubbles may cause unexpected results
         * */
        @JvmStatic
        fun isRunning() = isRunning

    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showBubble()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }


    // overridable func ----------------------------------------------------------------------------

    open fun channelId() = "bubble_service"
    open fun channelName() = "floating bubble"

    open fun notificationId() = 101

    private fun showNotification() {

        val channelId = if (isHigherThanAndroid8()) {
            createNotificationChannel(channelId(), channelName())
        } else {
            // In earlier version, channel ID is not necessary
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            ""
        }
        val notification = setupNotificationBuilder(channelId)

        startForeground(notificationId(), notification)
    }


    open fun setupNotificationBuilder(channelId: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_rounded_blue_diamond)
            .setContentTitle(getString(R.string.ask_ai_chat_anything))
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    open fun showBubble() {

        if (isDrawOverlaysPermissionGranted()) {

            setupViewAppearance(BUBBLE)

            if (isHigherThanAndroid8()) {
                showNotification()
            }

        } else {
            try {
                throw PermissionDeniedException()
            } catch (e: Exception) { }

        }
    }

    fun showExpandableView() {

        if (isDrawOverlaysPermissionGranted()) {

            setupViewAppearance(EXPANDABLE_VIEW)

            if (isHigherThanAndroid8()) {
                showNotification()
            }

        } else throw PermissionDeniedException()
    }

    // helper --------------------------------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String,
        channelName: String
    ): String {
        val channel = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    private fun isHigherThanAndroid8() = Build.VERSION.SDK_INT >= AndroidVersions.`8`

}