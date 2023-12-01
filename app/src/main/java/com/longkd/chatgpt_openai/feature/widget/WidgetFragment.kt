package com.longkd.chatgpt_openai.feature.widget


import android.os.Bundle
import androidx.fragment.app.viewModels
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentWidgetBinding

class WidgetFragment : BaseFragment<FragmentWidgetBinding>(R.layout.fragment_widget) {

    private val viewModel: WidgetViewModel by viewModels()

    companion object {
        fun newInstance(): WidgetFragment {
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
            activity?.let { it1 -> viewModel.clickWidget4x1(it1) }
        }
        mBinding?.fmWidgetTvAddNoTopic?.setOnSingleClick {
            activity?.let { it1 -> viewModel.clickWidget4x2(it1) }
        }
        mBinding?.fmWidgetHeader?.setTitle(getStringRes(R.string.widget))
        mBinding?.fmWidgetHeader?.setLeftAction(leftAction = CommonAction {
            popBackStack()
        })
    }

    override var initBackAction: Boolean = true

    override fun initData() {
    }
}