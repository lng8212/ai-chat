package com.longkd.chatgpt_openai.feature.suggest_topic

import android.os.Bundle
import android.view.LayoutInflater
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicType
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogSuggestTopicBinding
import com.longkd.chatgpt_openai.feature.suggest_topic.view_holder.SuggestTopicAdapter


class DialogSuggestTopic : BaseDialog<DialogSuggestTopicBinding>() {
    private var action: ((value: String) -> Unit)? = null
    private var mTopicType: Int = 1
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window

            val windowParams = window?.attributes
            windowParams?.windowAnimations = R.style.BottomDialog
        }
    }

    override fun initView() {
        mBinding?.dialogSuggestTopicTvCancel?.setOnClickListener {
            closeDialog()
        }
        mTopicType = arguments?.getInt(TOPIC_TYPE, mTopicType) ?: mTopicType
        val adapter = SuggestTopicAdapter(arrayListOf(), object : ItemClickListener<String> {
            override fun onClick(item: String?, position: Int) {
                action?.invoke(item ?: "")
                closeDialog()
            }
        })
        mBinding?.dialogSuggestTopicRcv?.adapter = adapter
        adapter?.updateData(getSuggestString(mTopicType))
    }


    companion object {
        const val TOPIC_TYPE = "TOPIC_TYPE"
        fun newInstance(
            actionExit: ((value: String) -> Unit)?,
            topicType: Int
        ): DialogSuggestTopic {
            val dialog = DialogSuggestTopic()
            val args = Bundle()
            args.putInt(TOPIC_TYPE, topicType)
            dialog.action = actionExit
            dialog.arguments = args
            dialog.mTopicType = topicType
            return dialog
        }

    }

    override fun initBinding(): DialogSuggestTopicBinding {
        return DialogSuggestTopicBinding.inflate(LayoutInflater.from(context))
    }

    private fun getSuggestString(type: Int): ArrayList<String> {
        val result = ArrayList<String>()
        when (type) {
            TopicType.INTERVIEWER.value -> {
                result.add(getStringRes(R.string.txt_topic_interview_q1))
                result.add(getStringRes(R.string.txt_topic_interview_q2))
                result.add(getStringRes(R.string.txt_topic_interview_q3))
                result.add(getStringRes(R.string.txt_topic_interview_q4))
                result.add(getStringRes(R.string.txt_topic_interview_q5))
                result.add(getStringRes(R.string.txt_topic_interview_q6))
                result.add(getStringRes(R.string.txt_topic_interview_q7))
                result.add(getStringRes(R.string.txt_topic_interview_q8))
                result.add(getStringRes(R.string.txt_topic_interview_q9))
                result.add(getStringRes(R.string.txt_topic_interview_q10))
            }
            TopicType.DEVELOPER.value -> {
                result.add(getStringRes(R.string.txt_topic_develop_q1))
                result.add(getStringRes(R.string.txt_topic_develop_q2))
                result.add(getStringRes(R.string.txt_topic_develop_q3))
                result.add(getStringRes(R.string.txt_topic_develop_q4))
                result.add(getStringRes(R.string.txt_topic_develop_q5))
                result.add(getStringRes(R.string.txt_topic_develop_q6))
                result.add(getStringRes(R.string.txt_topic_develop_q7))
                result.add(getStringRes(R.string.txt_topic_develop_q8))
                result.add(getStringRes(R.string.txt_topic_develop_q9))
                result.add(getStringRes(R.string.txt_topic_develop_q10))
            }
            TopicType.BUSINESS_ASSISTANT.value -> {
                result.add(getStringRes(R.string.txt_topic_business_q1))
                result.add(getStringRes(R.string.txt_topic_business_q2))
                result.add(getStringRes(R.string.txt_topic_business_q3))
                result.add(getStringRes(R.string.txt_topic_business_q4))
                result.add(getStringRes(R.string.txt_topic_business_q5))
                result.add(getStringRes(R.string.txt_topic_business_q6))
                result.add(getStringRes(R.string.txt_topic_business_q7))
                result.add(getStringRes(R.string.txt_topic_business_q8))
                result.add(getStringRes(R.string.txt_topic_business_q9))
                result.add(getStringRes(R.string.txt_topic_business_q10))
            }
            TopicType.ADVERTISER.value -> {
                result.add(getStringRes(R.string.txt_topic_ad_q1))
                result.add(getStringRes(R.string.txt_topic_ad_q2))
                result.add(getStringRes(R.string.txt_topic_ad_q3))
                result.add(getStringRes(R.string.txt_topic_ad_q4))
                result.add(getStringRes(R.string.txt_topic_ad_q5))
                result.add(getStringRes(R.string.txt_topic_ad_q6))
                result.add(getStringRes(R.string.txt_topic_ad_q7))
                result.add(getStringRes(R.string.txt_topic_ad_q8))
                result.add(getStringRes(R.string.txt_topic_ad_q9))
                result.add(getStringRes(R.string.txt_topic_ad_q10))
            }
            TopicType.LANGUAGE_EXPERT.value -> {
                result.add(getStringRes(R.string.txt_topic_language_q1))
                result.add(getStringRes(R.string.txt_topic_language_q2))
                result.add(getStringRes(R.string.txt_topic_language_q3))
                result.add(getStringRes(R.string.txt_topic_language_q4))
                result.add(getStringRes(R.string.txt_topic_language_q5))
                result.add(getStringRes(R.string.txt_topic_language_q6))
                result.add(getStringRes(R.string.txt_topic_language_q7))
                result.add(getStringRes(R.string.txt_topic_language_q8))
                result.add(getStringRes(R.string.txt_topic_language_q9))
                result.add(getStringRes(R.string.txt_topic_language_q10))
            }
            TopicType.CHEF.value -> {
                result.add(getStringRes(R.string.txt_topic_chef_q1))
                result.add(getStringRes(R.string.txt_topic_chef_q2))
                result.add(getStringRes(R.string.txt_topic_chef_q3))
                result.add(getStringRes(R.string.txt_topic_chef_q4))
                result.add(getStringRes(R.string.txt_topic_chef_q5))
                result.add(getStringRes(R.string.txt_topic_chef_q6))
                result.add(getStringRes(R.string.txt_topic_chef_q7))
                result.add(getStringRes(R.string.txt_topic_chef_q8))
                result.add(getStringRes(R.string.txt_topic_chef_q9))
                result.add(getStringRes(R.string.txt_topic_chef_q10))
            }
            TopicType.COMPOSER.value -> {
                result.add(getStringRes(R.string.txt_topic_composer_q1))
                result.add(getStringRes(R.string.txt_topic_composer_q2))
                result.add(getStringRes(R.string.txt_topic_composer_q3))
                result.add(getStringRes(R.string.txt_topic_composer_q4))
                result.add(getStringRes(R.string.txt_topic_composer_q5))
                result.add(getStringRes(R.string.txt_topic_composer_q6))
                result.add(getStringRes(R.string.txt_topic_composer_q7))
                result.add(getStringRes(R.string.txt_topic_composer_q8))
                result.add(getStringRes(R.string.txt_topic_composer_q9))
                result.add(getStringRes(R.string.txt_topic_composer_q10))
            }
            TopicType.MENTAL_HEALTH_AD.value -> {
                result.add(getStringRes(R.string.txt_topic_mental_q1))
                result.add(getStringRes(R.string.txt_topic_mental_q2))
                result.add(getStringRes(R.string.txt_topic_mental_q3))
                result.add(getStringRes(R.string.txt_topic_mental_q4))
                result.add(getStringRes(R.string.txt_topic_mental_q5))
                result.add(getStringRes(R.string.txt_topic_mental_q6))
                result.add(getStringRes(R.string.txt_topic_mental_q7))
                result.add(getStringRes(R.string.txt_topic_mental_q8))
                result.add(getStringRes(R.string.txt_topic_mental_q9))
                result.add(getStringRes(R.string.txt_topic_mental_q10))
            }
        }

        return result
    }
}