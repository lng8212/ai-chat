package com.longkd.chatgpt_openai.base.model

import androidx.annotation.DrawableRes
import com.longkd.chatgpt_openai.R
import java.lang.IllegalArgumentException

enum class DetailTopicData(
    val title: Int,
    val subTitle: Int,
    val question: Int,
    @DrawableRes val icon: Int,
    val topicId: Int
) {
    MARKETING_PLAN(R.string.str_marketing_plan, R.string.str_marketing_plan_detail, R.string.str_marketing_plan_ques, R.drawable.img_marketing_plan,1),
    INTERVIEWER_SUPPORT(R.string.str_interviewer_support, R.string.str_interviewer_support_detail, R.string.str_interviewer_support_ques, R.drawable.img_interview_support ,1),
    COMPETITORS_RESEARCH(R.string.str_competitors_research, R.string.str_competitors_research_detail, R.string.str_competitors_research_ques, R.drawable.img_competitor_research, 1),
    CUSTOMERS_INSIGHTS_RESEARCH(R.string.str_customers_insights_research, R.string.str_customers_insights_research_detail, R.string.str_customers_insights_research_ques, R.drawable.img_customer_insight_research, 1),
    WRITE_PARAGRAPH(R.string.str_write_paragraph, R.string.str_write_paragraph_detail, R.string.str_write_paragraph_ques, R.drawable.img_write_a_paragraph, 2),
    WRITE_SONG_LYRICS(R.string.str_write_song_lyrics, R.string.str_write_song_lyrics_detail, R.string.str_write_song_lyrics_ques, R.drawable.img_write_a_song, 2),
    WRITE_MEDIA_POST(R.string.str_write_social_media_post, R.string.str_str_write_social_media_post_detail, R.string.str_str_write_social_media_post_ques, R.drawable.img_write_media_post,2),
    WRITE_EMAIL(R.string.str_write_email, R.string.str_write_email_detail, R.string.str_write_email_ques, R.drawable.img_write_gmail,2),
    OUTFIT_DESIGNER(R.string.str_outfit_designer, R.string.str_outfit_designer_detail, R.string.str_outfit_designer_ques, R.drawable.img_outfit_designer, 3),
    WRITE_PICKUP_LINE(R.string.str_write_pickup_line, R.string.str_write_pickup_line_detail, R.string.str_write_pickup_line_ques, R.drawable.img_write_pickup_line,  3),
    WRITE_JOKE(R.string.str_write_joke, R.string.str_write_joke_detail, R.string.str_write_joke_ques, R.drawable.img_write_joke,  3),
    HOROSCOPE_READER(R.string.str_horoscope_reader, R.string.str_horoscope_reader_detail, R.string.str_horoscope_reader_ques, R.drawable.img_horoscope_reader, 3),
    SOLVE_MATH_PROBLEM(R.string.str_solve_math_problem, R.string.str_solve_math_problem_detail, R.string.str_solve_math_problem_ques, R.drawable.img_solve_math_problem, 4),
    HISTORY_EVENTS(R.string.str_history_events, R.string.str_history_events_detail, R.string.str_history_events_ques, R.drawable.img_history_events, 4),
    CODE_SUPPORTER(R.string.str_code_supporter, R.string.str_code_supporter_detail, R.string.str_code_supporter_ques, R.drawable.img_code_supporter, 4),
    LATINO_DISHES(R.string.str_latino_dishes, R.string.str_latino_dishes_detail, R.string.str_latino_dishes_ques, R.drawable.img_latino_dishes, 5),
    AFRICAN_DISHES(R.string.str_african_dishes, R.string.str_african_dishes_detail, R.string.str_african_dishes_ques , R.drawable.img_african_dishes,5),
    ASIAN_DISHES(R.string.str_asian_dishes, R.string.str_asian_dishes_detail, R.string.str_asian_dishes_ques , R.drawable.img_asian_dishes,5),
    WESTERN_DISHES(R.string.str_western_dishes, R.string.str_western_dishes_detail, R.string.str_western_dishes_ques, R.drawable.img_western_dishes, 5),
    TRAVEL_DESTINATION(R.string.str_travel_destination, R.string.str_travel_destination_detail,R.string.str_travel_destination_ques, R.drawable.img_travel_destination, 6),
    TRAVEL_EXPERIENCES(R.string.str_travel_experience, R.string.str_travel_experience_detail,R.string.str_travel_experience_ques, R.drawable.img_travel_experience, 6);

    companion object {
        fun fromString(value: String?): DetailTopicData? {
            return try {
                value?.let { valueOf(value) }
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
