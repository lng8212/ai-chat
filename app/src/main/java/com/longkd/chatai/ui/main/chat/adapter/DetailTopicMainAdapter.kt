package com.longkd.chatai.ui.main.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.data.TopicDetail
import com.longkd.chatai.databinding.ItemDetailTopicMainBinding

/**
 * @Author: longkd
 * @Since: 12:50 - 24/09/2023
 */
class DetailTopicMainAdapter :
    BaseListAdapter<TopicDetail, DetailTopicMainAdapter.DetailTopicViewHolder>() {

    inner class DetailTopicViewHolder(binding: ItemDetailTopicMainBinding) :
        BaseViewHolder<TopicDetail, ItemDetailTopicMainBinding>(binding) {
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
        return DetailTopicViewHolder(
            ItemDetailTopicMainBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }
}