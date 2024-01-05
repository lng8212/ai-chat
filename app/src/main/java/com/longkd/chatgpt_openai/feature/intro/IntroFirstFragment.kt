package com.longkd.chatgpt_openai.feature.intro

import android.os.Bundle
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.databinding.FragmentIntroFirstBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFirstFragment :
    BaseFragment<FragmentIntroFirstBinding>(R.layout.fragment_intro_first) {
    companion object {
        fun newInstance(): IntroFirstFragment {
            val args = Bundle()

            val fragment = IntroFirstFragment()
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
        mBinding?.fmIntroFirstLat?.playAnimation()
    }

    fun stopAnimation(){
        mBinding?.fmIntroFirstLat?.cancelAnimation()
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