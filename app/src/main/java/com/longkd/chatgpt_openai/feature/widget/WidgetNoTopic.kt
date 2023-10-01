package com.longkd.chatgpt_openai.feature.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.feature.splash.SplashActivity


class WidgetNoTopic : AppWidgetProvider() {
    private var mContext: Context? = null

    companion object {
        val ACTION_UPDATE = "com.longkd.chatgpt_openai.feature.widget.widgetnotopic.ACTION_UPDATE"
        val ACTION = "widget_no_topic"
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (context == null)
            return
        if (ACTION_UPDATE == intent?.action) {
            try {
                val intent = Intent(context, WidgetNoTopic::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids: IntArray = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(context, WidgetNoTopic::class.java)
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids)
                onUpdate(context, AppWidgetManager.getInstance(context), ids)
            } catch (e: Exception) {

            }
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context?.let { context ->
            appWidgetIds?.forEach {
                val intent = Intent(context, SplashActivity::class.java)
                intent.action = Constants.KEY_WIDGET_CLICK
                intent.putExtra("widget", WidgetNoTopic.ACTION)
                intent.putExtra(Constants.KEY_WIDGET_CLICK, -1)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                val views: RemoteViews =
                    RemoteViews(context.packageName, R.layout.layout_widget_no_topic).apply {
                        val chatPendingIntent =
                            UtilsApp.getActivityIntent(context, 6666, intent)
                        setOnClickPendingIntent(R.id.layoutWidgetChat, chatPendingIntent)
                        appWidgetManager?.updateAppWidget(it, this@apply)
                    }
            }
        }
    }


}