package com.longkd.chatai.ui.main.chat.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.data.Topic
import com.longkd.chatai.data.TopicDetail
import com.longkd.chatai.databinding.ItemDetailSubTopicBinding
import com.longkd.chatai.util.DataUtils

/**
 * @Author: longkd
 * @Since: 11:49 - 24/09/2023
 */
class AllTopicAdapter(
    private val context: Context,
    private val onClickNext: (topic: Topic) -> Unit
) :
    BaseListAdapter<Topic, AllTopicAdapter.AllTopicViewHolder>() {
    @Suppress("UNCHECKED_CAST")
    inner class AllTopicViewHolder(binding: ItemDetailSubTopicBinding) :
        BaseViewHolder<Topic, ItemDetailSubTopicBinding>(binding) {
        override fun onBind(item: Topic) {
            binding.txtTitle.text = item.name
            binding.btnNext.setOnClickListener {
                onClickNext.invoke(item)
            }
            val adapter = DetailTopicMainAdapter()
            binding.rclView.adapter = adapter
            adapter.submitList(DataUtils.getTypeTopic(context, item.type) as List<TopicDetail>)
        }

    }

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Topic, *> {
        return AllTopicViewHolder(ItemDetailSubTopicBinding.inflate(layoutInflater, parent, false))
    }
}