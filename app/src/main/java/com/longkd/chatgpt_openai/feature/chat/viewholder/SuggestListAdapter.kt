package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSuggestQuestionBinding

class SuggestListAdapter(
    val list: MutableList<String>,
    val callback: ItemClickListener<String>
) : BaseListAdapter<String>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: String, position: Int) {
        (holder as SuggestListItemVH).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: String,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return SuggestListItemVH(
            createBinding(parent, viewType) as ItemSuggestQuestionBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_suggest_question)
    }

    fun updateView(data: String) {
        val arrSuggest = ArrayList(this.listDto)
        arrSuggest.remove(data)
        this.listDto.clear()
        this.listDto.addAll(arrSuggest)
        notifyDataSetChanged()
    }

    class SuggestListItemVH(
        mBinding: ItemSuggestQuestionBinding,
        val callback: ItemClickListener<String>,
    ) : BaseViewHolder<ItemSuggestQuestionBinding>(mBinding) {
        fun bindData(item: String) {
            binding.chatItemListRightText.text = item
            binding.chatItemListRightText.setOnSingleClick {
                callback.onClick(item)
            }
        }
    }
}