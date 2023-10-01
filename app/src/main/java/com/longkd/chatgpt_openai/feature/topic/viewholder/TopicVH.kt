package com.longkd.chatgpt_openai.feature.topic.viewholder

import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.TopicListItemBinding
import com.bumptech.glide.Glide

class TopicVH(
    mBinding: TopicListItemBinding,
    val callback: ItemClickListener<TopicDto>
) : BaseViewHolder<TopicListItemBinding>(mBinding) {
    fun bindData(data: TopicDto) {
        binding.topicItemImg.let {
            Glide.with(it).load(data.resID).into(it)
        }
        binding.topicItemTvChat.setOnSingleClick {
            callback.onClick(data, adapterPosition)
        }
        binding.topicItemTvTitle?.text = data.title
        binding.topicItemTvDes?.text = data.des
    }

}