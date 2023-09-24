package com.longkd.chatai.data

import com.longkd.chatai.util.DataUtils

/**
 * @Author: longkd
 * @Since: 09:55 - 24/09/2023
 */
data class Topic(
    val name: String,
    val topicDetail: List<TopicDetail>,
    val type: DataUtils.ListTopic
)

