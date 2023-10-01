package com.longkd.chatgpt_openai.base.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.longkd.chatgpt_openai.base.model.ChatDetailConverter
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ListStringConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "summary_file_dto")
data class SummaryHistoryDto(
    @PrimaryKey()
    val md5: String = "",
    val fileName: String ? = null,
    @field:TypeConverters(ListStringConverter::class)
    val filePaths: List<String>,
    @field:TypeConverters(ChatDetailConverter::class)
    var chatDetail: List<ChatDetailDto>,
    @field:TypeConverters(ListStringConverter::class)
    var suggestList: List<String> ?,
    var lastTimeUpdate: Long = 0,
    val summaryContent: String ? = null
) : Parcelable