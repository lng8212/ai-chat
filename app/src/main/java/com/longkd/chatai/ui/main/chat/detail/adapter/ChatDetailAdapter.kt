package com.longkd.chatai.ui.main.chat.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.data.ChatDetailDto
import com.longkd.chatai.data.ChatType
import com.longkd.chatai.databinding.ItemChatLeftBinding
import com.longkd.chatai.databinding.ItemChatRightBinding

/**
 * @Author: longkd
 * @Since: 21:38 - 29/09/2023
 */
class ChatDetailAdapter(
    var mEnableAnimateText: Boolean,
    var itemClickListener: (chatDetailDto: ChatDetailDto, position: Int) -> Unit
) :
    BaseListAdapter<ChatDetailDto, BaseViewHolder<ChatDetailDto, ViewBinding>>() {
    var mRecyclerView: RecyclerView? = null
    var mOnAnimateFinished: (() -> Unit)? = null
    var onClickRegenerate: ((chatDto: ChatDetailDto) -> Unit)? = null
    var onClickSeeMore: (() -> Unit)? = null
    private var mLastChatItemLeftVH: ChatItemLeftViewHolder? = null

    fun stopAnimateText() {
        mLastChatItemLeftVH?.stopTextViewWriting()
    }

    fun updateText(text: String, index: Int) {
        mLastChatItemLeftVH?.updateText(text, index)
    }

    fun updateTextMore(text: String) {
        mLastChatItemLeftVH?.updateTextMore(text)
    }

    fun setCharacterDelay(millis: Long) {
        mLastChatItemLeftVH?.setCharacterDelay(millis)
    }

    fun updateData(list: List<ChatDetailDto>) {
        list.forEach { it.isLastItem = false }
        list.lastOrNull()?.isLastItem = true
        updateDataDiff(ArrayList(list))
    }

    fun disconnectDataLinked(array: List<ChatDetailDto>): List<ChatDetailDto> {
        val newList: java.util.ArrayList<ChatDetailDto> = arrayListOf()
        array.forEach { dto ->
            newList.add(
                ChatDetailDto(
                    dto.message,
                    dto.timeChat,
                    dto.timeChatString,
                    dto.isTyping,
                    dto.chatType,
                    dto.chatUserNane,
                    dto.parentId,
                    dto.isSeeMore,
                    dto.isLastItem
                ).apply {

                })
        }
        return newList
    }

    fun updateDataDiff(list: ArrayList<ChatDetailDto>) {
        val newRecipeList: MutableList<ChatDetailDto> = ArrayList()
        newRecipeList.addAll(disconnectDataLinked(list))
        currentList.clear()
        currentList.addAll(newRecipeList)
        val recyclerViewState = mRecyclerView?.layoutManager?.onSaveInstanceState()
        if (recyclerViewState != null) {
            mRecyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList.getOrNull(position)?.chatType ?: ChatType.SEND.value
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ChatDetailDto, *> {
        return when (viewType) {
            ChatType.RECEIVE.value -> {
                val mVH = ChatItemLeftViewHolder(
                    ItemChatLeftBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ),
                    mEnableAnimateText,
                    mOnAnimateFinished,
                    itemClickListener,
                    onClickRegenerate,
                    onClickSeeMore
                )
                mLastChatItemLeftVH = mVH
                mVH
            }

            ChatType.SEND.value ->
                ChatItemRightViewHolder(
                    ItemChatRightBinding.inflate(layoutInflater, parent, false)
                )

            else -> ChatItemRightViewHolder(
                ItemChatRightBinding.inflate(layoutInflater, parent, false)
            )
        }
    }
}