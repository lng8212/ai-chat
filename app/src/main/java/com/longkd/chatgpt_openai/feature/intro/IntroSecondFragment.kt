package com.longkd.chatgpt_openai.feature.intro

import android.os.Bundle
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.databinding.FragmentIntroSecondBinding
class IntroSecondFragment :
    BaseFragment<FragmentIntroSecondBinding>(R.layout.fragment_intro_second) {

    companion object {
        fun newInstance(): IntroSecondFragment {
            val args = Bundle()

            val fragment = IntroSecondFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
    }

    override fun initActions() {
    }

    override fun initData() {

    }
    fun playAnimation(){
        mBinding?.fmIntroSecondImg?.playAnimation()
    }
    fun stopAnimation(){
        mBinding?.fmIntroSecondImg?.cancelAnimation()
    }



    override fun handleOnBackPress(): Boolean {
        activity?.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        UtilsApp.hideKeyboard(activity)
    }

    override var initBackAction: Boolean = false
}