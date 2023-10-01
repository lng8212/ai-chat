package com.longkd.chatgpt_openai.base.data

import android.os.Parcelable
import com.longkd.chatgpt_openai.R
import kotlinx.parcelize.Parcelize

@Parcelize
class TopicDto(val resID: Int, val topicType: Int, var title: String, val des: String, var icon : Int? = R.drawable.ic_topic_interview, var clicked : Boolean? = false) :
    Parcelable {
}