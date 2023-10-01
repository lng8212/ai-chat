
package com.longkd.chatgpt_openai.base.util

import android.content.Context
import androidx.annotation.StringRes
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.data.TopicType

object TopicsUtils {
     fun getSuggestString(context: Context?,type: Int): ArrayList<String> {
        val result = ArrayList<String>()
        when (type) {
            TopicType.INTERVIEWER.value -> {
                result.add(getStringRes(context, R.string.txt_topic_interview_q1))
                result.add(getStringRes(context, R.string.txt_topic_interview_q2))
                result.add(getStringRes(context, R.string.txt_topic_interview_q3))
                result.add(getStringRes(context, R.string.txt_topic_interview_q4))
                result.add(getStringRes(context, R.string.txt_topic_interview_q5))
                result.add(getStringRes(context, R.string.txt_topic_interview_q6))
                result.add(getStringRes(context, R.string.txt_topic_interview_q7))
                result.add(getStringRes(context, R.string.txt_topic_interview_q8))
                result.add(getStringRes(context, R.string.txt_topic_interview_q9))
                result.add(getStringRes(context, R.string.txt_topic_interview_q10))
            }
            TopicType.DEVELOPER.value -> {
                result.add(getStringRes(context, R.string.txt_topic_develop_q1))
                result.add(getStringRes(context, R.string.txt_topic_develop_q2))
                result.add(getStringRes(context, R.string.txt_topic_develop_q3))
                result.add(getStringRes(context, R.string.txt_topic_develop_q4))
                result.add(getStringRes(context, R.string.txt_topic_develop_q5))
                result.add(getStringRes(context, R.string.txt_topic_develop_q6))
                result.add(getStringRes(context, R.string.txt_topic_develop_q7))
                result.add(getStringRes(context, R.string.txt_topic_develop_q8))
                result.add(getStringRes(context, R.string.txt_topic_develop_q9))
                result.add(getStringRes(context, R.string.txt_topic_develop_q10))
            }
            TopicType.BUSINESS_ASSISTANT.value -> {
                result.add(getStringRes(context, R.string.txt_topic_business_q1))
                result.add(getStringRes(context, R.string.txt_topic_business_q2))
                result.add(getStringRes(context, R.string.txt_topic_business_q3))
                result.add(getStringRes(context, R.string.txt_topic_business_q4))
                result.add(getStringRes(context, R.string.txt_topic_business_q5))
                result.add(getStringRes(context, R.string.txt_topic_business_q6))
                result.add(getStringRes(context, R.string.txt_topic_business_q7))
                result.add(getStringRes(context, R.string.txt_topic_business_q8))
                result.add(getStringRes(context, R.string.txt_topic_business_q9))
                result.add(getStringRes(context, R.string.txt_topic_business_q10))
            }
            TopicType.ADVERTISER.value -> {
                result.add(getStringRes(context, R.string.txt_topic_ad_q1))
                result.add(getStringRes(context, R.string.txt_topic_ad_q2))
                result.add(getStringRes(context, R.string.txt_topic_ad_q3))
                result.add(getStringRes(context, R.string.txt_topic_ad_q4))
                result.add(getStringRes(context, R.string.txt_topic_ad_q5))
                result.add(getStringRes(context, R.string.txt_topic_ad_q6))
                result.add(getStringRes(context, R.string.txt_topic_ad_q7))
                result.add(getStringRes(context, R.string.txt_topic_ad_q8))
                result.add(getStringRes(context, R.string.txt_topic_ad_q9))
                result.add(getStringRes(context, R.string.txt_topic_ad_q10))
            }
            TopicType.LANGUAGE_EXPERT.value -> {
                result.add(getStringRes(context, R.string.txt_topic_language_q1))
                result.add(getStringRes(context, R.string.txt_topic_language_q2))
                result.add(getStringRes(context, R.string.txt_topic_language_q3))
                result.add(getStringRes(context, R.string.txt_topic_language_q4))
                result.add(getStringRes(context, R.string.txt_topic_language_q5))
                result.add(getStringRes(context, R.string.txt_topic_language_q6))
                result.add(getStringRes(context, R.string.txt_topic_language_q7))
                result.add(getStringRes(context, R.string.txt_topic_language_q8))
                result.add(getStringRes(context, R.string.txt_topic_language_q9))
                result.add(getStringRes(context, R.string.txt_topic_language_q10))
            }
            TopicType.CHEF.value -> {
                result.add(getStringRes(context, R.string.txt_topic_chef_q1))
                result.add(getStringRes(context, R.string.txt_topic_chef_q2))
                result.add(getStringRes(context, R.string.txt_topic_chef_q3))
                result.add(getStringRes(context, R.string.txt_topic_chef_q4))
                result.add(getStringRes(context, R.string.txt_topic_chef_q5))
                result.add(getStringRes(context, R.string.txt_topic_chef_q6))
                result.add(getStringRes(context, R.string.txt_topic_chef_q7))
                result.add(getStringRes(context, R.string.txt_topic_chef_q8))
                result.add(getStringRes(context, R.string.txt_topic_chef_q9))
                result.add(getStringRes(context, R.string.txt_topic_chef_q10))
            }
            TopicType.COMPOSER.value -> {
                result.add(getStringRes(context, R.string.txt_topic_composer_q1))
                result.add(getStringRes(context, R.string.txt_topic_composer_q2))
                result.add(getStringRes(context, R.string.txt_topic_composer_q3))
                result.add(getStringRes(context, R.string.txt_topic_composer_q4))
                result.add(getStringRes(context, R.string.txt_topic_composer_q5))
                result.add(getStringRes(context, R.string.txt_topic_composer_q6))
                result.add(getStringRes(context, R.string.txt_topic_composer_q7))
                result.add(getStringRes(context, R.string.txt_topic_composer_q8))
                result.add(getStringRes(context, R.string.txt_topic_composer_q9))
                result.add(getStringRes(context, R.string.txt_topic_composer_q10))
            }
            TopicType.MENTAL_HEALTH_AD.value -> {
                result.add(getStringRes(context, R.string.txt_topic_mental_q1))
                result.add(getStringRes(context, R.string.txt_topic_mental_q2))
                result.add(getStringRes(context, R.string.txt_topic_mental_q3))
                result.add(getStringRes(context, R.string.txt_topic_mental_q4))
                result.add(getStringRes(context, R.string.txt_topic_mental_q5))
                result.add(getStringRes(context, R.string.txt_topic_mental_q6))
                result.add(getStringRes(context, R.string.txt_topic_mental_q7))
                result.add(getStringRes(context, R.string.txt_topic_mental_q8))
                result.add(getStringRes(context, R.string.txt_topic_mental_q9))
                result.add(getStringRes(context, R.string.txt_topic_mental_q10))
            }
        }

        return result
    }
    fun listTopic(context: Context?) : ArrayList<TopicDto>{
        return arrayListOf(
            TopicDto(
                R.drawable.img_topic_interview,
                TopicType.INTERVIEWER.value,
                getStringRes(context, R.string.txt_topic_interview_title),
                getStringRes(context, R.string.txt_topic_interview),
                R.drawable.ic_topic_interview
            ), TopicDto(
                R.drawable.img_topic_business,
                TopicType.BUSINESS_ASSISTANT.value,
                getStringRes(context, R.string.txt_topic_business_title),
                getStringRes(context, R.string.txt_topic_business),
                R.drawable.ic_topic_businiss
            ), TopicDto(
                R.drawable.img_topic_developer,
                TopicType.DEVELOPER.value,
                getStringRes(context, R.string.txt_topic_develop_title),
                getStringRes(context, R.string.txt_topic_develop),
                R.drawable.ic_topic_developer
            ), TopicDto(
                R.drawable.img_topic_ad,
                TopicType.ADVERTISER.value,
                getStringRes(context, R.string.txt_topic_ad_title),
                getStringRes(context, R.string.txt_topic_ad),
                R.drawable.ic_topic_advetiser
            ), TopicDto(
                R.drawable.img_topic_language,
                TopicType.LANGUAGE_EXPERT.value,
                getStringRes(context, R.string.txt_topic_language_title),
                getStringRes(context, R.string.txt_topic_language),
                R.drawable.ic_topic_language
            ), TopicDto(
                R.drawable.img_topic_chef,
                TopicType.CHEF.value,
                getStringRes(context, R.string.txt_topic_chef_title),
                getStringRes(context, R.string.txt_topic_chef),
                R.drawable.ic_topic_chef
            ), TopicDto(
                R.drawable.img_topic_composer,
                TopicType.COMPOSER.value,
                getStringRes(context, R.string.txt_topic_composer_title),
                getStringRes(context, R.string.txt_topic_composer),
                R.drawable.ic_topic_composer
            ), TopicDto(
                R.drawable.img_topic_mental,
                TopicType.MENTAL_HEALTH_AD.value,
                getStringRes(context, R.string.txt_topic_mental_title),
                getStringRes(context, R.string.txt_topic_mental),
                R.drawable.ic_topic_mental
            )
        )
    }
    private fun getStringRes(context: Context?, @StringRes res: Int): String {
        return try {
            context?.resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }
}