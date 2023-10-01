package com.longkd.chatgpt_openai.feature.home.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.HistoryDto
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.base.widget.DefaultDiffCallback
import com.longkd.chatgpt_openai.databinding.ItemChatHistoryBinding

class ChatHistoryAdapter(
    var listData: ArrayList<HistoryDto>,
    var clickListener: ItemClickListener<HistoryDto>,
    var onCheckChange : ChatItemHistoryVH.CheckBoxListener
) : BaseListAdapter<HistoryDto>(listData) {
    var mRecyclerView: RecyclerView? = null
    var isShow : Boolean = false
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: HistoryDto, position: Int) {
        when (holder) {
            is ChatItemHistoryVH -> {
                if (listData.size > position) {
                    holder.bindData(listData[position], isShow)
                }
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: HistoryDto,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return ChatItemHistoryVH(
            createBinding(parent, viewType) as ItemChatHistoryBinding, clickListener, onCheckChange
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_chat_history)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun updateData(list: List<HistoryDto>) {
        updateDataDiff(ArrayList(list))
    }
    fun setShowRemove(isShow : Boolean){
        this.isShow = isShow
        notifyDataSetChanged()
    }
    fun updateDataDiff(list: ArrayList<HistoryDto>) {
        val newRecipeList: MutableList<HistoryDto> = ArrayList()
        newRecipeList.addAll(list)

        val diffUtil = DiffUtil.calculateDiff(DefaultDiffCallback(listData, list))
        diffUtil.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newRecipeList)
        val recyclerViewState = mRecyclerView?.layoutManager?.onSaveInstanceState()
        if (recyclerViewState != null) {
            mRecyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }
}