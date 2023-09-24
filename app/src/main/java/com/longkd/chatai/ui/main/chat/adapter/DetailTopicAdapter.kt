package com.longkd.chatai.ui.main.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.data.TopicDetail
import com.longkd.chatai.databinding.ItemDetailTopicBinding

/**
 * @Author: longkd
 * @Since: 12:11 - 24/09/2023
 */
class DetailTopicAdapter :
    BaseListAdapter<TopicDetail, DetailTopicAdapter.DetailTopicViewHolder>() {

    inner class DetailTopicViewHolder(binding: ItemDetailTopicBinding) :
        BaseViewHolder<TopicDetail, ItemDetailTopicBinding>(binding) {
        override fun onBind(item: TopicDetail) {
            binding.run {
                iconTopic.setImageResource(item.image)
                tvSubTitle.text = item.detail
                binding.tvTitle.text = item.name
            }
        }

    }

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<TopicDetail, *> {
        return DetailTopicViewHolder(ItemDetailTopicBinding.inflate(layoutInflater, parent, false))
    }
}