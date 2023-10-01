
package com.longkd.chatgpt_openai.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentWidgetBinding

class WidgetFragment : BaseFragment<FragmentWidgetBinding>(R.layout.fragment_widget) {


    companion object {
        fun newInstance(): WidgetFragment{
            val args = Bundle()

            val fragment = WidgetFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        CommonSharedPreferences.getInstance().setShowDirector(true)
    }

    override fun initActions() {
        mBinding?.fmWidgetTvAddTopic?.setOnSingleClick {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val  appWidgetManager = AppWidgetManager.getInstance(activity)
                val myProvider =
                    activity?.let { it1 ->
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
                    Toast.makeText(activity, getStringRes(R.string.device_not_support), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, getStringRes(R.string.text_des_guide_widget), Toast.LENGTH_SHORT).show()
            }
        }
        mBinding?.fmWidgetTvAddNoTopic?.setOnSingleClick {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val  appWidgetManager = AppWidgetManager.getInstance(activity)
                val myProvider =
                    activity?.let { it1 ->
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
                    Toast.makeText(activity, getStringRes(R.string.device_not_support), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, getStringRes(R.string.text_des_guide_widget), Toast.LENGTH_SHORT).show()
            }
        }
        mBinding?.fmWidgetHeader?.setTitle(getStringRes(R.string.widget))
        mBinding?.fmWidgetHeader?.setLeftAction(leftAction = CommonAction{
            popBackStack()
        })
    }

    override var initBackAction: Boolean = true

    override fun initData() {
    }
}