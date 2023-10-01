package com.longkd.chatgpt_openai.feature.connect

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentLostConnectBinding

class FragmentLostInternet :
    BaseFragment<FragmentLostConnectBinding>(R.layout.fragment_lost_connect) {

    companion object {
        fun newInstance(): FragmentLostInternet {
            val args = Bundle()

            val fragment = FragmentLostInternet()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
    }

    override fun initActions() {
        mBinding?.fmLostInternetTvCancel?.setOnSingleClick {
            activity?.onBackPressed()
        }
        mBinding?.fmLostInternetTvSubmit?.setOnSingleClick {
            startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }

    override fun initData() {

    }


    override fun handleOnBackPress(): Boolean {
        activity?.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        UtilsApp.hideKeyboard(activity)
    }

    override var initBackAction: Boolean = true
}