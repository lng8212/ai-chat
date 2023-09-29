package com.longkd.chatai.data

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

/**
 * @Author: longkd
 * @Since: 21:39 - 29/09/2023
 */
@Parcelize
data class ChatDetailDto(
    @Expose
    var message: String = "",
    @Expose
    val timeChat: Long = 0,
    var timeChatString: String = "",
    var isTyping: Boolean = false,
    @Expose
    val chatType: Int,
    @Expose
    var chatUserNane: String = "",
    var parentId: Long = 0,
    var isSeeMore: Boolean = false,
    var isLastItem: Boolean = false
) : Parcelable
