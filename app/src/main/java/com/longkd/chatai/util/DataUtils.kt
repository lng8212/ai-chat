package com.longkd.chatai.util

import android.content.Context
import com.longkd.chatai.R
import com.longkd.chatai.data.Topic
import com.longkd.chatai.data.TopicDetail

/**
 * @Author: longkd
 * @Since: 09:47 - 24/09/2023
 */
object DataUtils {
    enum class ListTopic {
        ALL,
        BUSINESS_EXPERT,
        CONTENT_CREATOR,
        LIFESTYLE_BUDDY,
        LEARN_WITH_CHAT_AI,
        COOKING_MASTER,
        TRAVEL
    }

    fun getAllTopic(): List<ListTopic> {
        return listOf(
            ListTopic.ALL,
            ListTopic.BUSINESS_EXPERT,
            ListTopic.CONTENT_CREATOR,
            ListTopic.LIFESTYLE_BUDDY,
            ListTopic.LEARN_WITH_CHAT_AI,
            ListTopic.COOKING_MASTER,
            ListTopic.TRAVEL
        )
    }

    fun getTypeTopic(context: Context, listTopic: ListTopic): Any {
        when (listTopic) {
            ListTopic.ALL -> {
                return getTypeAll(context)
            }

            ListTopic.BUSINESS_EXPERT -> {
                return getBusinessExpertTopic(context)
            }

            ListTopic.CONTENT_CREATOR -> {
                return getContentCreatorTopic(context)
            }

            ListTopic.LIFESTYLE_BUDDY -> {
                return getLifestyleBuddyTopic(context)
            }

            ListTopic.LEARN_WITH_CHAT_AI -> {
                return getLearnWithChatAITopic(context)
            }

            ListTopic.COOKING_MASTER -> {
                return getCookingMasterTopic(context)
            }

            ListTopic.TRAVEL -> {
                return getTravelTopic(context)
            }
        }
    }

    private fun getBusinessExpertTopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_marketing_plan,
                context.getString(R.string.text_marketing_plan),
                context.getString(R.string.text_marketing_plan_detail)
            ),
            TopicDetail(
                R.drawable.ic_interview_support,
                context.getString(R.string.text_interview_support),
                context.getString(R.string.text_interview_support_detail)
            ),
            TopicDetail(
                R.drawable.ic_competitor_research,
                context.getString(R.string.text_competitor_research),
                context.getString(R.string.text_competitor_research_detail)
            ),
            TopicDetail(
                R.drawable.ic_customer_insight_research,
                context.getString(R.string.text_customer_insight),
                context.getString(R.string.text_customer_insight_detail)
            )
        )
    }

    private fun getContentCreatorTopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_write_a_paragraph,
                context.getString(R.string.text_write_a_paragraph),
                context.getString(R.string.text_write_a_paragraph_detail)
            ),
            TopicDetail(
                R.drawable.ic_write_a_song_lyric,
                context.getString(R.string.text_write_a_song_lyrics),
                context.getString(R.string.text_write_a_song_lyrics_detail)
            ),
            TopicDetail(
                R.drawable.ic_write_a_social_media_post,
                context.getString(R.string.text_write_a_social_media_post),
                context.getString(R.string.text_write_a_social_media_post_detail)
            ),
            TopicDetail(
                R.drawable.ic_write_email,
                context.getString(R.string.text_write_an_email),
                context.getString(R.string.text_write_an_email_detail)
            )
        )
    }

    private fun getLifestyleBuddyTopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_outfit_designer,
                context.getString(R.string.text_outfit_designer),
                context.getString(R.string.text_outfit_designer_detail)
            ),
            TopicDetail(
                R.drawable.ic_write_a_pick_up_line,
                context.getString(R.string.text_write_a_pick_up_line),
                context.getString(R.string.text_write_a_pick_up_line_detail)
            ),
            TopicDetail(
                R.drawable.ic_write_a_joke,
                context.getString(R.string.text_write_a_joke),
                context.getString(R.string.text_write_a_joke_detail)
            ),
            TopicDetail(
                R.drawable.ic_horoscope_reader,
                context.getString(R.string.text_horoscope_reader),
                context.getString(R.string.text_horoscope_reader_detail)
            )
        )
    }

    private fun getLearnWithChatAITopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_solve_math_problem,
                context.getString(R.string.text_solve_a_math_problem),
                context.getString(R.string.text_solve_a_math_problem_detail)
            ),
            TopicDetail(
                R.drawable.ic_history_event,
                context.getString(R.string.text_history_event),
                context.getString(R.string.text_history_event_detail)
            ),
            TopicDetail(
                R.drawable.ic_code_supporter,
                context.getString(R.string.text_code_supporter),
                context.getString(R.string.text_code_supporter_detail)
            )
        )
    }

    private fun getCookingMasterTopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_latino_dishes,
                context.getString(R.string.text_latino_dishes),
                context.getString(R.string.text_latino_dishes_detail)
            ),
            TopicDetail(
                R.drawable.ic_african_dishes,
                context.getString(R.string.text_african_dishes),
                context.getString(R.string.text_african_dishes_detail)
            ),
            TopicDetail(
                R.drawable.ic_asian_dishes,
                context.getString(R.string.text_asian_dishes),
                context.getString(R.string.text_asian_dishes_detail)
            ),
            TopicDetail(
                R.drawable.ic_western_dishes,
                context.getString(R.string.text_western_dishes),
                context.getString(R.string.text_western_dishes_detail)
            )
        )
    }

    private fun getTravelTopic(context: Context): List<TopicDetail> {
        return listOf(
            TopicDetail(
                R.drawable.ic_travel_destination,
                context.getString(R.string.text_travel_destination),
                context.getString(R.string.text_travel_destination_detail)
            ),
            TopicDetail(
                R.drawable.ic_travel_experience,
                context.getString(R.string.text_travel_experiences),
                context.getString(R.string.text_travel_experiences_detail)
            )
        )
    }

    private fun getTypeAll(context: Context): List<Topic> {
        return listOf(
            Topic(
                context.getString(R.string.text_business_expert),
                getBusinessExpertTopic(context),
                ListTopic.BUSINESS_EXPERT
            ),
            Topic(
                context.getString(R.string.text_content_creator),
                getContentCreatorTopic(context),
                ListTopic.CONTENT_CREATOR,
            ),
            Topic(
                context.getString(R.string.text_lifestyle_buddy),
                getLifestyleBuddyTopic(context),
                ListTopic.LIFESTYLE_BUDDY
            ),
            Topic(
                context.getString(R.string.text_learn_with_chat_ai),
                getLearnWithChatAITopic(context),
                ListTopic.LEARN_WITH_CHAT_AI
            ),
            Topic(
                context.getString(R.string.text_cooking_master),
                getCookingMasterTopic(context),
                ListTopic.COOKING_MASTER
            ),
            Topic(
                context.getString(R.string.text_travel),
                getTravelTopic(context),
                ListTopic.TRAVEL
            )
        )
    }
}