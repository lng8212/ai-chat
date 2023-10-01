package com.longkd.chatgpt_openai.dialog

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.NetworkUtil
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogLostInternetBinding


class DialogLostInternet : BaseDialog<DialogLostInternetBinding>() {
    private var mCheckType: StatusCheckType = StatusCheckType.S_ALL
    private var mOpenSetting: (() -> Unit)? = null

    enum class StatusCheckType {
        S_ALL, S_WIFI
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun initView() {
        when (mCheckType) {
            StatusCheckType.S_ALL -> {
                mBinding?.dialogLostInternetTvDes?.text = getStringRes(R.string.txt_check_internet)
                mBinding?.dialogLostInternetTvTitle?.text = getStringRes(R.string.txt_no_internet)
            }
            StatusCheckType.S_WIFI -> {
                mBinding?.dialogLostInternetTvDes?.text = getStringRes(R.string.txt_check_wifi)
                mBinding?.dialogLostInternetTvTitle?.text = getStringRes(R.string.txt_no_wifi)
            }
        }
        mBinding?.dialogLostInternetTvSubmit?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                try {
                    when (mCheckType) {
                        StatusCheckType.S_ALL -> {
                            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        }
                        StatusCheckType.S_WIFI -> {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        }
                        else -> {
                            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                        }
                    }

                    mOpenSetting?.invoke()
                    dismissAllowingStateLoss()
                } catch (_: Exception) {
                }
            }
        })
        mBinding?.dialogLostInternetTvCancel?.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }


    companion object {
        fun newInstance(
            checkType: StatusCheckType, openSetting: (() -> Unit)?
        ): DialogLostInternet {
            val dialog = DialogLostInternet()
            dialog.mCheckType = checkType
            dialog.mOpenSetting = openSetting
            return dialog
        }

        fun checkInternetStatus(
            checkType: StatusCheckType,
            context: Context?,
            manage: FragmentManager?,
            openSetting: (() -> Unit)? = null
        ): Boolean {
            when (checkType) {
                StatusCheckType.S_ALL -> {
                    return if (NetworkUtil.isInternetAvailable(context!!)) {
                        true
                    } else {
                        newInstance(checkType, openSetting).show(manage)
                        false
                    }
                }
                StatusCheckType.S_WIFI -> {
                    return if (UtilsApp.isConnectWifi(context)) {
                        true
                    } else {
                        newInstance(checkType, openSetting).show(manage)
                        false
                    }
                }
            }
        }
    }

    override fun initBinding(): DialogLostInternetBinding {
        return DialogLostInternetBinding.inflate(LayoutInflater.from(context))
    }
}