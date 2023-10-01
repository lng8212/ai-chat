
package com.longkd.chatgpt_openai.feature.art

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemStyleArtBinding

class StyleArtAdapter(
    var listData: ArrayList<StyleArtDto>,
    var clickListener: ItemClickListener<StyleArtDto>,
) : BaseListAdapter<StyleArtDto>(listData) {
    companion object {
        const val PAYLOAD_UPDATE = "PAYLOAD_UPDATE"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: StyleArtDto,
        position: Int
    ) {
        when (holder) {
            is StyleViewHolder -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    fun updateData(list: ArrayList<StyleArtDto>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateDataDiff(list: ArrayList<StyleArtDto>) {
        val newRecipeList: MutableList<StyleArtDto> = ArrayList()
        list.forEach {
            newRecipeList.add(it)
        }
        val diffUtil = DiffUtil.calculateDiff(DiffCallback(listData, list))
        diffUtil.dispatchUpdatesTo(this)
        listData.clear()
        newRecipeList.forEach {
            listData.add(it)
        }
    }

    class DiffCallback(
        private val oldDataFileCloudDtoList: ArrayList<StyleArtDto>,
        private val newDataFileCloudDtoList: ArrayList<StyleArtDto>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataFileCloudDtoList.size

        override fun getNewListSize(): Int = newDataFileCloudDtoList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.name == newItem.name && oldItem.isSelected == newItem.isSelected
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return if (oldItem.isSelected != newItem.isSelected)
                PAYLOAD_UPDATE else
                null
        }
    }

    var mRecyclerView: RecyclerView? = null


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_style_art)

    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return StyleViewHolder(
            createBinding(parent, viewType) as ItemStyleArtBinding,
            clickListener,
        )
    }

    override fun bindPayload(

        holder: BaseViewHolder<ViewDataBinding>,
        item: StyleArtDto,
        position: Int,
        payload: Any
    ) {
        if (payload == PAYLOAD_UPDATE) {
            when (holder) {
                is StyleViewHolder -> {
                    holder.updateSelected(item)
                }
            }
        } else {
            bind(holder, item, position)
        }
    }

}