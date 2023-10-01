package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopicQuestionData(
    @SerializedName("topic")
    val topic: String ? = null,
    @SerializedName("question_topic")
    val questionTopic: String ? = null,
    @SerializedName("question_user")
    val questionUser: String ? = null,
    @SerializedName("list_option")
    val listOption: List<String>
): Parcelable