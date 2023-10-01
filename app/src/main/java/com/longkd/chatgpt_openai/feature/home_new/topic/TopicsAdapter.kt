package com.longkd.chatgpt_openai.feature.home_new.topic

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.TopicData
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemTopicBinding

class TopicsAdapter(
    val list: MutableList<TopicData>,
    val callback: ItemClickListener<TopicData>
) : BaseListAdapter<TopicData>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: TopicData, position: Int) {
        (holder as SuggestTopicItemVH).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: TopicData,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return SuggestTopicItemVH(
            createBinding(parent, viewType) as ItemTopicBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_topic)
    }

    fun updateView(item: TopicData) {
        list.firstOrNull { it.isSelect }?.isSelect = false
        list.firstOrNull { it.topicId == item.topicId }?.isSelect = true
        notifyDataSetChanged()
    }

    class SuggestTopicItemVH(
        mBinding: ItemTopicBinding,
        val callback: ItemClickListener<TopicData>,
    ) : BaseViewHolder<ItemTopicBinding>(mBinding) {
        fun bindData(item: TopicData) {
            binding.tvTitle.text = item.title
            binding.llnView.setBackgroundResource(if (item.isSelect) R.drawable.bg_boder18_green else R.drawable.bg_border_start_chat)
            binding.tvTitle.setTextColor(if(item.isSelect) ContextCompat.getColor(itemView.context, R.color.color_white) else ContextCompat.getColor(itemView.context, R.color.color_green_base))
            binding.tvTitle.setOnSingleClick {
                callback.onClick(item)
            }

        }
    }
}