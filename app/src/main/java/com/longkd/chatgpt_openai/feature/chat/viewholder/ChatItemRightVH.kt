package com.longkd.chatgpt_openai.feature.chat.viewholder

import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ChatItemListRightBinding

class ChatItemRightVH(
    mBinding: ChatItemListRightBinding,
    private val themeColorMode: Int
) : BaseViewHolder<ChatItemListRightBinding>(mBinding) {

    fun bindData(data: ChatDetailDto) {
        binding.chatItemListRightTextTime.text = data.timeChatString
        binding.chatItemListRightText.text = data.message.trim()
    }

}