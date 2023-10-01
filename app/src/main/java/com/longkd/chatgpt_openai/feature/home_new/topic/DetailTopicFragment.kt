package com.longkd.chatgpt_openai.feature.home_new.topic

import android.os.Bundle
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.customview.CustomTopicView
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.orZero
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentDetailTopicBinding
import com.longkd.chatgpt_openai.dialog.ChatWithTopicDialog
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.chat.ChatWithTopicFragment
import com.google.android.flexbox.FlexboxLayout

class DetailTopicFragment: BaseFragment<FragmentDetailTopicBinding>(R.layout.fragment_detail_topic) {
    override fun initViews() {
        mBinding?.baseHeaderTvTitle?.text = arguments?.getString(KEY_TITLE)
    }

    override fun initActions() {
        mBinding?.baseHeaderBtnLeft?.setOnSingleClick {
            handleOnBackPress()
        }

        mBinding?.topicFmBtnContinue?.setOnSingleClick {
            handleClickContinue()
        }
    }

    private fun handleClickContinue() {
        UtilsApp.hideKeyboard(activity)
        var ques = getString(arguments?.getInt(KEY_QUESTION).orZero())
        for (i in 0 until mBinding?.topicFmFlexbox?.childCount.orZero()) {
            val view = mBinding?.topicFmFlexbox?.getChildAt(i) as? CustomTopicView
            if (view?.getDataField().isNullOrEmpty()) {
                showDialogError("Please input ${view?.getTitle()}")
                return
            } else {
                ques = ques.replaceFirst("%s", view?.getDataField().toString())
            }
        }
        ques = ques.replace("[", "")
        ques = ques.replace("]", "")
        showChatTopic(ques)
    }

    private fun showChatTopic(question: String) {
                    mainFragment?.pushScreenWithAnimate(
                        ChatWithTopicFragment.newInstance(question, mBinding?.baseHeaderTvTitle?.text.toString()),
                        ChatWithTopicFragment::class.java.name
                    )
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        (activity as? MainActivity)?.apply {
                onBackPressed()
        }
        return true
    }

    override fun initData() {
        val arrData = arguments?.getParcelableArrayList<DataFieldTopic>(KEY_DATA)
        arrData?.forEach { data ->
            mBinding?.topicFmFlexbox?.addView(CustomTopicView(requireContext()).apply {
                this.updateView(data)
                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.MATCH_PARENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams = params
                setOnClickSelectListener {
                    handleDisplayBottomViewSelect(this, data)
                }
            })
        }
    }

    private fun handleDisplayBottomViewSelect(view: CustomTopicView, dataFieldTopic: DataFieldTopic) {
        val dialog = ChatWithTopicDialog.show(childFragmentManager, dataFieldTopic, view.getDataField())
        dialog.onClickItem = {
            view.setFieldInputText(it ?: "")
            dialog.dismissAllowingStateLoss()
        }
    }

    companion object {
        private const val KEY_DATA = "KEY_DATA"
        private const val KEY_QUESTION = "KEY_QUESTION"
        private const val KEY_TITLE = "KEY_TITLE"
        fun newInstance(arr: ArrayList<DataFieldTopic>, ques: Int, title: String): DetailTopicFragment{
            val args = Bundle()
            args.putParcelableArrayList(KEY_DATA, arr)
            args.putInt(KEY_QUESTION, ques)
            args.putString(KEY_TITLE, title)
            val fragment = DetailTopicFragment()
            fragment.arguments = args
            return fragment
        }
    }
}