package com.longkd.chatgpt_openai.feature.suggest_topic.view_holder

import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.SuggestListItemBinding

class SuggestTopicVH(
    mBinding: SuggestListItemBinding,
    val callback: ItemClickListener<String>
) : BaseViewHolder<SuggestListItemBinding>(mBinding) {

    fun bindData(data: String) {
        binding.suggestListItemText.text = data
        binding.suggestListItemText.setOnSingleClick {
            callback.onClick(data, layoutPosition)
        }
    }

}