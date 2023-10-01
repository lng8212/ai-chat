package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "chat_base_dto")
data class ChatBaseDto(
    @PrimaryKey(autoGenerate = true)
    val chatId: Long = 0,
    @field:TypeConverters(ChatDetailConverter::class)
    var chatDetail: List<ChatDetailDto>,
    var lastTimeUpdate: Long = 0,
    var topicType: Int = -1
) : Parcelable
