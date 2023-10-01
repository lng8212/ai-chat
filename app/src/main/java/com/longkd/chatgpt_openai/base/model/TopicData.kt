package com.longkd.chatgpt_openai.base.model

data class TopicData(
    val topicId: Int ? = 0,
    val title: String ? = null,
    var isSelect: Boolean = false
)
