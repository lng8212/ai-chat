package com.longkd.chatgpt_openai.feature.topic

import android.os.Bundle
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.data.TopicType
import com.longkd.chatgpt_openai.base.model.ChatMode
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentTopicBinding
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.topic.viewholder.TopicAdapter

class FragmentTopic : BaseFragment<FragmentTopicBinding>(R.layout.fragment_topic) {

    private var mAdapter: TopicAdapter? = null
    override var initBackAction: Boolean = false

    companion object {
        fun newInstance(): FragmentTopic {
            val args = Bundle()

            val fragment = FragmentTopic()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        val array = arrayListOf(
            TopicDto(
                R.drawable.img_topic_interview,
                TopicType.INTERVIEWER.value,
                getStringRes(R.string.txt_topic_interview_title),
                getStringRes(R.string.txt_topic_interview)
            ), TopicDto(
                R.drawable.img_topic_business,
                TopicType.BUSINESS_ASSISTANT.value,
                getStringRes(R.string.txt_topic_business_title),
                getStringRes(R.string.txt_topic_business)
            ), TopicDto(
                R.drawable.img_topic_developer,
                TopicType.DEVELOPER.value,
                getStringRes(R.string.txt_topic_develop_title),
                getStringRes(R.string.txt_topic_develop)
            ), TopicDto(
                R.drawable.img_topic_ad,
                TopicType.ADVERTISER.value,
                getStringRes(R.string.txt_topic_ad_title),
                getStringRes(R.string.txt_topic_ad)
            ), TopicDto(
                R.drawable.img_topic_language,
                TopicType.LANGUAGE_EXPERT.value,
                getStringRes(R.string.txt_topic_language_title),
                getStringRes(R.string.txt_topic_language)
            ), TopicDto(
                R.drawable.img_topic_chef,
                TopicType.CHEF.value,
                getStringRes(R.string.txt_topic_chef_title),
                getStringRes(R.string.txt_topic_chef)
            ), TopicDto(
                R.drawable.img_topic_composer,
                TopicType.COMPOSER.value,
                getStringRes(R.string.txt_topic_composer_title),
                getStringRes(R.string.txt_topic_composer)
            ), TopicDto(
                R.drawable.img_topic_mental,
                TopicType.MENTAL_HEALTH_AD.value,
                getStringRes(R.string.txt_topic_mental_title),
                getStringRes(R.string.txt_topic_mental)
            )
        )
        mAdapter = TopicAdapter(array, object : ItemClickListener<TopicDto> {
            override fun onClick(item: TopicDto?, position: Int) {

                        mainFragment?.pushScreenWithAnimate(
                            ChatDetailFragment.newInstance(
                                chatMode = ChatMode.D.value,
                                topicType = item
                            ), ChatDetailFragment::class.java.name
                        )
            }
        })

        mBinding?.recyclerView?.adapter = mAdapter
    }

    override fun initActions() {
    }

    override fun initData() {
    }


}