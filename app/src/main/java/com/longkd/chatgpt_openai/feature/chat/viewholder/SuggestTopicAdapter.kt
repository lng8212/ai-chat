package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSuggestTopicBinding

class SuggestTopicAdapter(val list: ArrayList<String>, val callback: ItemClickListener<String>) :
    BaseListAdapter<String>(list) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: String, position: Int) {
        (holder as SuggestTopicItemVH).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: String,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return SuggestTopicItemVH(
            createBinding(parent, viewType) as ItemSuggestTopicBinding,
            callback)
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_suggest_topic)
    }

    class SuggestTopicItemVH(
        mBinding: ItemSuggestTopicBinding,
        val callback: ItemClickListener<String>,
    ) : BaseViewHolder<ItemSuggestTopicBinding>(mBinding) {
        fun bindData(des: String) {
            binding.tvDescription.text = des
            binding.root.setOnSingleClick {
                callback.onClick(des)
            }
        }
    }
}