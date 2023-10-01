package com.longkd.chatgpt_openai.feature.suggest_topic.view_holder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.SuggestListItemBinding

class SuggestTopicAdapter(
    var listData: ArrayList<String>,
    val callback: ItemClickListener<String>
) :
    BaseListAdapter<String>(listData) {

    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: String, position: Int) {
        when (holder) {
            is SuggestTopicVH -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: String,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return SuggestTopicVH(
            createBinding(parent, viewType) as SuggestListItemBinding, callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.suggest_list_item)

    }

    fun updateData(list: ArrayList<String>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

}