package com.longkd.chatgpt_openai.feature.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.data.TopicType
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.feature.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class WidgetTopic : AppWidgetProvider() {
    private var mContext: Context? = null

    @Inject
    lateinit var dataRepository: DataRepository

    companion object {
        val ACTION_UPDATE = "com.longkd.chatgpt_openai.feature.widget.ACTION_UPDATE"
        val TYPE_HISTORY = "TYPE_HISTORY"
        val ACTION = "widget_topic"
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        mContext = context
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        if (context == null)
            return
        if (ACTION_UPDATE == intent?.action) {
            try {
                val intent = Intent(context, WidgetTopic::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids: IntArray = AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(context, WidgetTopic::class.java)
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ids)
                onUpdate(context, AppWidgetManager.getInstance(context), ids)
            } catch (e: Exception) {

            }
        }
    }

    @SuppressLint("RemoteViewLayout")
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        context?.let { context ->
            appWidgetIds?.forEach {
                val views: RemoteViews =
                    RemoteViews(context.packageName, R.layout.layout_widget_topic).apply {
                        MainScope().launch(Dispatchers.Default) {
                            withContext(Dispatchers.Default) {
                                val dto = dataRepository?.getChatDto()
                                dto?.chatDetail?.lastOrNull()?.let {
                                    setTextViewText(R.id.layoutWidgetTvLastChat, it.message)
                                }

                                if (dto?.chatId != null) {
                                    val intent = Intent(context, SplashActivity::class.java)
                                    intent.action = Constants.KEY_WIDGET_CLICK
                                    intent.putExtra(Constants.KEY_WIDGET_CLICK, dto.chatId)
                                    intent.putExtra(TYPE_HISTORY, true)
                                    intent.putExtra("widget", WidgetTopic.ACTION)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    val pending =
                                        UtilsApp.getActivityIntent(context, 969696, intent)
                                    setOnClickPendingIntent(
                                        R.id.layoutWidgetTvLastChat,
                                        pending
                                    )
                                } else
                                    setOnClickPendingIntent(
                                        R.id.layoutWidgetTvLastChat,
                                        createPendingIntent(context)
                                    )
                            }
                            setOnClickPendingIntent(
                                R.id.widgetLlInterviewer,
                                createPendingIntent(context, TopicType.INTERVIEWER.value)
                            )
                            setOnClickPendingIntent(
                                R.id.widgetLlComposer,
                                createPendingIntent(context, TopicType.COMPOSER.value)
                            )
                            setOnClickPendingIntent(
                                R.id.widgetLlDeveloper,
                                createPendingIntent(context, TopicType.DEVELOPER.value)
                            )
                            setOnClickPendingIntent(
                                R.id.widgetLlAdvertiser,
                                createPendingIntent(context, TopicType.ADVERTISER.value)
                            )
                            setOnClickPendingIntent(
                                R.id.layoutWidgetRlChat,
                                createPendingIntent(context)
                            )
                            appWidgetManager?.updateAppWidget(it, this@apply)
                        }
                    }
            }
        }
    }

    private fun createPendingIntent(context: Context?, topicType: Int? = -1): PendingIntent {
        val intent = Intent(context, SplashActivity::class.java)
        intent.action = Constants.KEY_WIDGET_CLICK
        intent.putExtra("widget", WidgetTopic.ACTION)
        intent.putExtra(Constants.KEY_WIDGET_CLICK, topicType)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return UtilsApp.getActivityIntent(context, topicType ?: 66666, intent)
    }


}