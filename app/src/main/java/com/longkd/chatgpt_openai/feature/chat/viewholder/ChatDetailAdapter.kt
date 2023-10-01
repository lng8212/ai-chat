package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ChatType
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.base.widget.DefaultDiffCallback
import com.longkd.chatgpt_openai.databinding.ChatItemListLeftBinding
import com.longkd.chatgpt_openai.databinding.ChatItemListRightBinding

class ChatDetailAdapter(
    var listData: ArrayList<ChatDetailDto>, var mEnableAnimateText: Boolean,
    private val themeColorMode: Int,
    var itemClickListener: ItemClickListener<ChatDetailDto>
) : BaseListAdapter<ChatDetailDto>(listData) {
    var mRecyclerView: RecyclerView? = null
    var mLastChatItemLeftVH: ChatItemLeftVH? = null
    var mOnAnimateFinished: (() -> Unit)? = null
    var onClickRegenerate: ((chatDto: ChatDetailDto) -> Unit) ? = null
    var onClickSeeMore: (() -> Unit) ? = null

    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: ChatDetailDto, position: Int) {
        when (holder) {
            is ChatItemLeftVH -> {
                if (listData.size > position) {
                    holder.enableAnimateText = mEnableAnimateText
                    holder.bindData(listData[position])
                }
                mLastChatItemLeftVH = holder
            }
            is ChatItemRightVH -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: ChatDetailDto,
        position: Int,
        payload: Any
    ) {}

    override fun getItemViewType(position: Int): Int {
        return listData.getOrNull(position)?.chatType ?: ChatType.SEND.value
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return when (viewType) {
            ChatType.RECEIVE.value -> {
                val mVH = ChatItemLeftVH(
                    createBinding(parent, viewType) as ChatItemListLeftBinding,
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
                ChatItemRightVH(
                    createBinding(parent, viewType) as ChatItemListRightBinding,
                    themeColorMode
                )
            else -> ChatItemRightVH(
                createBinding(parent, viewType) as ChatItemListRightBinding,
                themeColorMode
            )
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when (viewType) {
            ChatType.RECEIVE.value ->
                parent.bindingInflate(R.layout.chat_item_list_left)
            ChatType.SEND.value ->
                parent.bindingInflate(R.layout.chat_item_list_right)
            else -> parent.bindingInflate(R.layout.chat_item_list_right)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun updateData(list: List<ChatDetailDto>) {
        list.forEach { it.isLastItem = false }
        list.lastOrNull()?.isLastItem = true
        updateDataDiff(ArrayList(list))
    }

    fun updateDataDiff(list: ArrayList<ChatDetailDto>) {
        val newRecipeList: MutableList<ChatDetailDto> = ArrayList()
        newRecipeList.addAll(disconnectDataLinked(list))
        val diffUtil = DiffUtil.calculateDiff(DefaultDiffCallback(listData, list))
        diffUtil.dispatchUpdatesTo(this)
        listData.clear()
        listData.addAll(newRecipeList)
        val recyclerViewState = mRecyclerView?.layoutManager?.onSaveInstanceState()
        if (recyclerViewState != null) {
            mRecyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }

    fun disconnectDataLinked(array: List<ChatDetailDto>): List<ChatDetailDto> {
        val newList: java.util.ArrayList<ChatDetailDto> = arrayListOf()
        array.forEach { dto ->
            newList.add(
                ChatDetailDto(
                    dto.message ?: "",
                    dto.timeChat ?: 0,
                    dto.timeChatString ?: "",
                    dto.isTyping ?: false,
                    dto.chatType ?: -1,
                    dto.chatUserNane ?: "",
                    dto.parentId?:0,
                    dto.isSeeMore,
                    dto.isLastItem
                ).apply {

                })
        }
        return newList
    }

    fun stopAnimateText() {
        mLastChatItemLeftVH?.stopTextViewWriting()
    }
    fun updateText(text : String, index : Int) {
        mLastChatItemLeftVH?.updateText(text,index)
    }
    fun updateTextMore(text : String) {
        mLastChatItemLeftVH?.updateTextMore(text)
    }

    fun setCharacterDelay(millis: Long) {
        mLastChatItemLeftVH?.setCharacterDelay(millis)
    }}