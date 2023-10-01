package com.longkd.chatgpt_openai.feature.home.viewholder

import androidx.constraintlayout.widget.ConstraintLayout
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.HistoryDto
import com.longkd.chatgpt_openai.base.util.dp
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemChatHistoryBinding

class ChatItemHistoryVH(
    mBinding: ItemChatHistoryBinding,
    var clickListener: ItemClickListener<HistoryDto>,
    var click: CheckBoxListener,

    ) : BaseViewHolder<ItemChatHistoryBinding>(mBinding) {

    fun bindData(data: HistoryDto, isDeleteMode: Boolean) {
        binding.itChatHistoryUserChat.text = data.chatDetailDto.chatUserNane
        binding.itChatHistoryBotChat.text = data.chatDetailDto.message.trim()
        binding.itChatHistoryCb.isChecked = data.isSelected
        binding.root.setOnSingleClick {
            if (isDeleteMode) {
                if (data.isSelected) {
                    data.isSelected = false
                    binding.itChatHistoryCb.isChecked = false
                } else {
                    data.isSelected = true
                    binding.itChatHistoryCb.isChecked = true
                }
                click.onCheckChange()
            } else {
                clickListener.onClick(data)
            }
        }
        binding.itChatHistoryCb.setOnClickListener {
            if (data.isSelected) {
                data.isSelected = false
                binding.itChatHistoryCb.isChecked = false
            } else {
                data.isSelected = true
                binding.itChatHistoryCb.isChecked = true
            }
            click.onCheckChange()
        }

        if (isDeleteMode) {
            binding.itChatHistoryCb.visible()
            (binding.itChatHistoryCt.layoutParams as ConstraintLayout.LayoutParams).marginEnd =
                (-20f).dp
        } else {
            binding.itChatHistoryCb.gone()
            (binding.itChatHistoryCt.layoutParams as ConstraintLayout.LayoutParams).marginEnd =
                16f.dp
        }


    }
    interface CheckBoxListener {
        fun onCheckChange()
    }
}