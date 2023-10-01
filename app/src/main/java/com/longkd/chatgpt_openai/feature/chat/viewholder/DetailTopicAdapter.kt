package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.DetailTopicData
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.orZero
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemDetailTopicBinding


class DetailTopicAdapter(
    val list: MutableList<DetailTopicData>,
    val callback: ItemClickListener<DetailTopicData>,
    val isFixWidth: Boolean = false
) : BaseListAdapter<DetailTopicData>(list.toMutableList()) {
    var highestHeightSubTitle = 0
    var highestHeightTitle = 0
    var mRecyclerView: RecyclerView? = null
    var mCurrentPosition = 0
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: DetailTopicData, position: Int) {
        (holder as SuggestTopicItemVH).bindData(item)
        if (isFixWidth) {
            holder.binding.tvSubTitle.height = highestHeightSubTitle
            holder.binding.tvTitle.height = highestHeightTitle
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: DetailTopicData,
        position: Int,
        payload: Any
    ) {
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        val holderView = SuggestTopicItemVH(
            createBinding(parent, viewType) as ItemDetailTopicBinding,
            callback
        )
        list.forEach {
            val currentItemHeight = getHeightOfLargestDescription(holderView.itemView.context, holderView.itemView.context.getString(it.subTitle), holderView.binding.tvSubTitle)
            if (currentItemHeight > highestHeightSubTitle) {
                highestHeightSubTitle = currentItemHeight
            }
            val currentTitleHeight = getHeightOfLargestDescription(holderView.itemView.context, holderView.itemView.context.getString(it.title), holderView.binding.tvTitle)
            if (currentTitleHeight > highestHeightTitle) {
                highestHeightTitle = currentTitleHeight
            }
        }
        return holderView
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_detail_topic)
    }

    inner class SuggestTopicItemVH(
        mBinding: ItemDetailTopicBinding,
        val callback: ItemClickListener<DetailTopicData>,
    ) : BaseViewHolder<ItemDetailTopicBinding>(mBinding) {
        fun bindData(item: DetailTopicData) {
            if (isFixWidth) {
                itemView.layoutParams.width = (itemView.context.resources.displayMetrics.widthPixels / 2.3).toInt()
            }
            binding.tvTitle.text = itemView.context.getString(item.title)
            binding.tvSubTitle.text = itemView.context.getString(item.subTitle)
            binding.iconTopic.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.icon))
            binding.root.setOnSingleClick {
                callback.onClick(item)
            }
        }
    }

    private fun getHeightOfLargestDescription(
        context: Context,
        text: CharSequence?,
        textView: TextView
    ): Int {
        val deviceWidth: Int = (context.resources.displayMetrics.widthPixels / 2.5).toInt()
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources?.getDimension(R.dimen._13sdp).orZero())
        textView.setText(text, TextView.BufferType.SPANNABLE)
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }
}