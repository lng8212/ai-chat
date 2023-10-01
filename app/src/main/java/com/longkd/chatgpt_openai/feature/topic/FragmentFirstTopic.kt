package com.longkd.chatgpt_openai.feature.topic

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.data.TopicType
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.TopicsUtils
import com.longkd.chatgpt_openai.databinding.FragmentFirstTopicBinding
import com.bumptech.glide.Glide


class FragmentFirstTopic : BaseFragment<FragmentFirstTopicBinding>(R.layout.fragment_first_topic) {
    companion object {
        fun newInstance(): FragmentFirstTopic {
            val args = Bundle()
            val fragment = FragmentFirstTopic()
            fragment.arguments = args
            return fragment
        }
    }

    private var topics: ArrayList<TopicDto>? = null
    override fun initViews() {
        topics = TopicsUtils.listTopic(activity)
        mBinding?.fmFirstTopicImg?.let { it1 ->
            Glide.with(this).load(R.drawable.img_first_topic).into(it1)
        }

    }


    override fun initActions() {
        mBinding?.fmFirstTopicConfirm?.setOnClickListener {
            val list = topics?.filter { it.clicked == true } as ArrayList<TopicDto>
            if (list.isEmpty())
                return@setOnClickListener
            CommonSharedPreferences.getInstance().setTopics(list)
            popBackstackAllFragment()
        }
        mBinding?.fmFirstTopicTvTopic1?.setOnClickListener {
            selectTopic(it, TopicType.INTERVIEWER)
        }
        mBinding?.fmFirstTopicTvTopic2?.setOnClickListener {
            selectTopic(it, TopicType.DEVELOPER)
        }
        mBinding?.fmFirstTopicTvTopic3?.setOnClickListener {
            selectTopic(it, TopicType.BUSINESS_ASSISTANT)
        }
        mBinding?.fmFirstTopicTvTopic4?.setOnClickListener {
            selectTopic(it, TopicType.ADVERTISER)
        }
        mBinding?.fmFirstTopicTvTopic5?.setOnClickListener {
            selectTopic(it, TopicType.LANGUAGE_EXPERT)
        }
        mBinding?.fmFirstTopicTvTopic6?.setOnClickListener {
            selectTopic(it, TopicType.CHEF)
        }
        mBinding?.fmFirstTopicTvTopic7?.setOnClickListener {
            selectTopic(it, TopicType.COMPOSER)
        }
        mBinding?.fmFirstTopicTvTopic8?.setOnClickListener {
            selectTopic(it, TopicType.MENTAL_HEALTH_AD)
        }
    }

    private fun setAlphaTopic(alpha: Float) {
        mBinding?.fmFirstTopicTvTopic1?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic2?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic3?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic4?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic5?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic6?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic7?.alpha = alpha
        mBinding?.fmFirstTopicTvTopic8?.alpha = alpha
    }

    private fun setAlphaHideTopic(topicType: Int) {
        when (topicType) {
            TopicType.INTERVIEWER.value -> {
                mBinding?.fmFirstTopicTvTopic1?.alpha = 1f
            }
            TopicType.DEVELOPER.value -> {
                mBinding?.fmFirstTopicTvTopic2?.alpha = 1f
            }
            TopicType.BUSINESS_ASSISTANT.value -> {
                mBinding?.fmFirstTopicTvTopic3?.alpha = 1f
            }
            TopicType.ADVERTISER.value -> {
                mBinding?.fmFirstTopicTvTopic4?.alpha = 1f
            }
            TopicType.LANGUAGE_EXPERT.value -> {
                mBinding?.fmFirstTopicTvTopic5?.alpha = 1f
            }
            TopicType.CHEF.value -> {
                mBinding?.fmFirstTopicTvTopic6?.alpha = 1f
            }
            TopicType.COMPOSER.value -> {
                mBinding?.fmFirstTopicTvTopic7?.alpha = 1f
            }
            TopicType.MENTAL_HEALTH_AD.value -> {
                mBinding?.fmFirstTopicTvTopic8?.alpha = 1f
            }
        }
    }

    private fun selectTopic(view: View?, topicType: TopicType) {
        activity?.let {
            val list = topics?.filter { topic ->
                topic.clicked == true
            }
            val topic = topics?.find { topic ->
                topic.topicType == topicType.value
            }
            if (topic?.clicked == true) {
                topic.clicked = false
                setAlphaTopic(1f)
                view?.background =
                    ContextCompat.getDrawable(it, R.drawable.ripper_btn_white10_round)
            } else {
                if (list?.size != null && list.size > 2) {
                    return
                }
                topic?.clicked = true
                view?.background =
                    ContextCompat.getDrawable(it, R.drawable.ripper_btn_green_base_round30)
            }

            val listNew = topics?.filter { topic ->
                topic.clicked == true
            }
            if (listNew?.size == 3) {
                setAlphaTopic(0.1f)
                listNew.forEach {
                    setAlphaHideTopic(it.topicType)
                }
            }
            mBinding?.fmFirstTopicConfirm?.alpha = if (listNew?.size == 0) 0.5f else 1f
            mBinding?.fmFirstTopicImvNext?.alpha = if (listNew?.size == 0) 0.5f else 1f
        }
    }

    override fun initData() {
    }

    override var initBackAction: Boolean = true
}