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
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.bubble.FloatingBubbleService
import com.longkd.chatgpt_openai.base.bubble.isDrawOverlaysPermissionGranted
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.SettingFragmentBinding
import com.longkd.chatgpt_openai.service.BubbleService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSetting : BaseFragment<SettingFragmentBinding>(R.layout.setting_fragment) {

    private val viewModel: SettingViewModel by viewModels()
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
            context?.let { viewModel.getVersion(it) }
        val enableAnimateText =
            CommonSharedPreferences.getInstance().getBoolean(Constants.ENABLE_ANIMATE_TEXT, false)
        mBinding?.fmSettingUpdateItemGenerating?.setSwitchEnable(enableAnimateText)
        val filter = IntentFilter(Constants.SERVICE_DESTROY)
        activity?.registerReceiver(receiver, filter)
    }


    override fun initActions() {
        mBinding?.apply {
            fmSettingUpdateItemLanguage.setOnSingleClick {
                activity?.let { it1 -> viewModel.openLanguage(it1) }
            }
            fmSettingBubble.setSwitchEnable(FloatingBubbleService.isRunning())
            fmSettingBubble.onSwitchChange {
                activity?.let { it1 -> viewModel.openBubble(it1, it, resultLauncherOverlay) }
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