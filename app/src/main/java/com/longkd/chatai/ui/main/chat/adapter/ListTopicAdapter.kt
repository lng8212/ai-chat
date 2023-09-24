package com.longkd.chatai.ui.main.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.R
import com.longkd.chatai.databinding.ItemTopicBinding
import com.longkd.chatai.util.DataUtils

/**
 * @Author: longkd
 * @Since: 20:49 - 13/08/2023
 */
class ListTopicAdapter(
    private val context: Context,
    private val onClickItem: (type: DataUtils.ListTopic) -> Unit
) :
    BaseListAdapter<DataUtils.ListTopic, ListTopicAdapter.ListTopicViewHolder>() {


    private var currentSelected = 0
    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<DataUtils.ListTopic, *> {
        return ListTopicViewHolder(ItemTopicBinding.inflate(layoutInflater, parent, false))
    }

    inner class ListTopicViewHolder(binding: ItemTopicBinding) :
        BaseViewHolder<DataUtils.ListTopic, ItemTopicBinding>(binding) {
        @SuppressLint("SetTextI18n")
        override fun onBind(item: DataUtils.ListTopic) {
            when (item) {
                DataUtils.ListTopic.ALL -> {
                    binding.tvTitle.text = context.getString(R.string.text_all)
                }

                DataUtils.ListTopic.BUSINESS_EXPERT -> {
                    binding.tvTitle.text = context.getString(R.string.text_business_expert)
                }

                DataUtils.ListTopic.CONTENT_CREATOR -> {
                    binding.tvTitle.text = context.getString(R.string.text_content_creator)
                }

                DataUtils.ListTopic.LIFESTYLE_BUDDY -> {
                    binding.tvTitle.text = context.getString(R.string.text_lifestyle_buddy)
                }

                DataUtils.ListTopic.LEARN_WITH_CHAT_AI -> {
                    binding.tvTitle.text = context.getString(R.string.text_learn_with_chat_ai)
                }

                DataUtils.ListTopic.COOKING_MASTER -> {
                    binding.tvTitle.text = context.getString(R.string.text_cooking_master)
                }

                DataUtils.ListTopic.TRAVEL -> {
                    binding.tvTitle.text = context.getString(R.string.text_travel)
                }
            }
            if (currentSelected == adapterPosition) {
                binding.llnView.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_primary_16)
                binding.tvTitle.setTextColor(context.getColor(R.color.color_white))
            } else {
                binding.llnView.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_border_start_chat)
                binding.tvTitle.setTextColor(context.getColor(R.color.color_green_base))
            }
            binding.root.setOnClickListener {
                onClickItem.invoke(item)
                val oldPos = currentSelected
                currentSelected = adapterPosition
                notifyItemChanged(oldPos)
                notifyItemChanged(currentSelected)
            }
        }
    }
}