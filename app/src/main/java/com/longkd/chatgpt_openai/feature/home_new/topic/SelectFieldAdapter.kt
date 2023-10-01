package com.longkd.chatgpt_openai.feature.home_new.topic

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSelectFieldTopicBinding

class SelectFieldAdapter(
    val list: MutableList<String>,
    var selectField: String,
    val callback: ItemClickListener<String>
) : BaseListAdapter<String>(list.toMutableList()) {
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
            createBinding(parent, viewType) as ItemSelectFieldTopicBinding,
            callback
        )
    }

    fun updateData(value: String) {
        this.selectField = value
        notifyDataSetChanged()
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_select_field_topic)
    }

    inner class SuggestTopicItemVH(
        mBinding: ItemSelectFieldTopicBinding,
        val callback: ItemClickListener<String>,
    ) : BaseViewHolder<ItemSelectFieldTopicBinding>(mBinding) {
        fun bindData(des: String) {
            binding.textTitle.text = des
            if (des != selectField) binding.icCheckBox.setBackgroundResource(R.drawable.checkbox)
            else binding.icCheckBox.setBackgroundResource(R.drawable.checkboxselected)
            binding.root.setOnSingleClick {
                callback.onClick(des)
            }
        }
    }
}