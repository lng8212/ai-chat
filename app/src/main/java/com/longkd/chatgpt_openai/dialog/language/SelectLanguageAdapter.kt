package com.longkd.chatgpt_openai.dialog.language

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSelectLanguageBinding


class SelectLanguageAdapter(
    val listData: ArrayList<LanguageDto>,
    val callback: ItemClickListener<LanguageDto>
) :
    BaseListAdapter<LanguageDto>(listData) {
    companion object {
        const val PAYLOAD_UPDATE_CHECKBOX = "PAYLOAD_UPDATE_CHECKBOX"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: LanguageDto,
        position: Int
    ) {
        when (holder) {
            is LanguageItemVH -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    fun updateData(list: ArrayList<LanguageDto>) {
        updateDataDiff(list)
        listData.clear()
        list.forEach {
            listData.add(LanguageDto(it.data, it.isSelected))
        }
    }

    fun updateDataDiff(list: ArrayList<LanguageDto>) {
        val newRecipeList: MutableList<LanguageDto> = ArrayList()
        newRecipeList.addAll(list)

        val diffUtil = DiffUtil.calculateDiff(SelectLanguageDiffCallback(listData, list))
        diffUtil.dispatchUpdatesTo(this)
        listData.clear()
        newRecipeList.forEach {
            listData.add(LanguageDto(it.data, it.isSelected))
        }
    }

    class SelectLanguageDiffCallback(
        private val oldDataFileCloudDtoList: ArrayList<LanguageDto>,
        private val newDataFileCloudDtoList: ArrayList<LanguageDto>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataFileCloudDtoList.size

        override fun getNewListSize(): Int = newDataFileCloudDtoList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.data == newItem.data
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.data == newItem.data && oldItem.isSelected == newItem.isSelected
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return if (oldItem.isSelected != newItem.isSelected)
                PAYLOAD_UPDATE_CHECKBOX else
                null
        }
    }

    var mRecyclerView: RecyclerView? = null


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_select_language)

    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return LanguageItemVH(
            createBinding(parent, viewType) as ItemSelectLanguageBinding,
            callback,
        )
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: LanguageDto,
        position: Int,
        payload: Any
    ) {
        if (payload == PAYLOAD_UPDATE_CHECKBOX) {
            when (holder) {
                is LanguageItemVH -> {
                    holder.updateCheckBox(item)
                }
            }
        } else {
            bind(holder, item, position)
        }
    }

}