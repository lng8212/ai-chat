package com.longkd.chatgpt_openai.base.widget

import androidx.recyclerview.widget.DiffUtil

class DefaultDiffCallback<T>(
    private val oldHomeSubListDtoList: ArrayList<T>,
    private val newHomeSubListDtoList: ArrayList<T>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldHomeSubListDtoList.size

    override fun getNewListSize(): Int = newHomeSubListDtoList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldHomeSubListDtoList[oldItemPosition]
        val newItem = newHomeSubListDtoList[newItemPosition]
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldHomeSubListDtoList[oldItemPosition]
        val newItem = newHomeSubListDtoList[newItemPosition]
        return oldItem == newItem
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}