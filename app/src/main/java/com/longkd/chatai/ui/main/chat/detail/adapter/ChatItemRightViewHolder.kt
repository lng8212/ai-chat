package com.longkd.chatai.ui.main.chat.detail.adapter

import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.data.ChatDetailDto
import com.longkd.chatai.databinding.ItemChatRightBinding

/**
 * @Author: longkd
 * @Since: 22:37 - 29/09/2023
 */
class ChatItemRightViewHolder(binding: ItemChatRightBinding) :
    BaseViewHolder<ChatDetailDto, ItemChatRightBinding>(binding) {
    override fun onBind(item: ChatDetailDto) {
        binding.chatItemListRightTextTime.text = item.timeChatString
        binding.chatItemListRightText.text = item.message.trim()
    }

}