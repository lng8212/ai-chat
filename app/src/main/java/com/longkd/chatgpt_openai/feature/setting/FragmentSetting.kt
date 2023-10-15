/*
 * Created by longkd on 10/1/23, 11:50 AM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/1/23, 11:46 AM
 */

package com.longkd.chatgpt_openai.feature.setting

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import com.longkd.chatgpt_openai.BuildConfig
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.bubble.FloatingBubbleService
import com.longkd.chatgpt_openai.base.bubble.isDrawOverlaysPermissionGranted
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.SettingFragmentBinding
import com.longkd.chatgpt_openai.feature.language.LanguageActivity
import com.longkd.chatgpt_openai.service.BubbleService

class FragmentSetting : BaseFragment<SettingFragmentBinding>(R.layout.setting_fragment) {
    override var initBackAction: Boolean = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Constants.SERVICE_DESTROY) {
                mBinding?.fmSettingBubble?.setSwitchEnable(false)
            }
        }
    }
    companion object {
        fun newInstance(): FragmentSetting {
            val args = Bundle()

            val fragment = FragmentSetting()
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("SetTextI18n", "UnspecifiedRegisterReceiverFlag")
    override fun initViews() {
        mBinding?.fmSettingUpdateTvVersion?.text =
            getStringRes(R.string.text_version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        val enableAnimateText =
            CommonSharedPreferences.getInstance().getBoolean(Constants.ENABLE_ANIMATE_TEXT, false)
        mBinding?.fmSettingUpdateItemGenerating?.setSwitchEnable(enableAnimateText)
        val filter = IntentFilter(Constants.SERVICE_DESTROY)
        activity?.registerReceiver(receiver, filter)
    }


    override fun initActions() {
        mBinding?.apply {
            fmSettingUpdateItemPrivacy.setOnSingleClick {
                UtilsApp.openBrowser(context, Constants.URL_POLICY)
            }
            fmSettingUpdateItemTemp.setOnSingleClick {
                UtilsApp.openBrowser(context, Constants.URL_TERM_OF_SERVICE)
            }
            fmSettingUpdateItemRate.setOnSingleClick {
                activity?.supportFragmentManager?.let { it1 ->
                }
            }
            fmSettingUpdateItemFeedBack.setOnSingleClick {
                activity?.let { it1 ->
                    UtilsApp.sendFeedBack(
                        it1, getStringRes(R.string.app_name)
                    )
                }
            }
            fmSettingUpdateItemLanguage.setOnSingleClick {
                val mIntent = Intent(context, LanguageActivity::class.java)
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                mIntent.data = activity?.intent?.data
                mIntent.action = activity?.intent?.action
                activity?.intent?.extras?.let { mIntent.putExtras(it) }
                startActivity(mIntent)
            }
            fmSettingBubble.setSwitchEnable(FloatingBubbleService.isRunning())
            fmSettingBubble.onSwitchChange {
                if (it) {
                    if (context?.isDrawOverlaysPermissionGranted() == true) {
                        val intent = Intent(activity, BubbleService::class.java)
                        if (!FloatingBubbleService.isRunning()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                activity?.startForegroundService(intent)
                            } else {
                                activity?.startService(intent)
                            }
                        }
                    } else {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity?.packageName)
                        )
                        resultLauncherOverlay.launch(intent)
                    }

                } else {
                    if (FloatingBubbleService.isRunning()){
                        val intent = Intent(activity, BubbleService::class.java)
                        activity?.stopService(intent)
                    }
                }
            }
        }
    }

    private var resultLauncherOverlay =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (context?.isDrawOverlaysPermissionGranted() == true) {
                val intent = Intent(activity, BubbleService::class.java)
                if (!FloatingBubbleService.isRunning()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        activity?.startForegroundService(intent)
                    } else {
                        activity?.startService(intent)
                    }
                }
            } else {
            mBinding?.fmSettingBubble?.setSwitchEnable(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        kotlin.runCatching {
            activity?.unregisterReceiver(receiver)
        }
    }
    override fun initData() {
    }


}