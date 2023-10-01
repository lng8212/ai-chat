package com.longkd.chatgpt_openai.feature.summary.history

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.DateUtils
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.dp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemHistorySummaryBinding

class HistorySummaryAdapter(
    val list: MutableList<SummaryHistoryDto>
) : BaseListAdapter<SummaryHistoryDto>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: SummaryHistoryDto, position: Int) {
        (holder as SummaryHistoryViewHolder).bindData(item)
    }

    private var isDelete: Boolean = false

    var onClickItem: ((summaryHistoryDto: SummaryHistoryDto) -> Unit) ? = null

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: SummaryHistoryDto,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return SummaryHistoryViewHolder(
            createBinding(parent, viewType) as ItemHistorySummaryBinding
        )
    }

    fun setShowRemove(isRemove: Boolean) {
        this.isDelete = isRemove
        notifyDataSetChanged()
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_history_summary)
    }

    inner class SummaryHistoryViewHolder(
        mBinding: ItemHistorySummaryBinding
    ) : BaseViewHolder<ItemHistorySummaryBinding>(mBinding) {
        fun bindData(des: SummaryHistoryDto) {
            val strFileSummary = itemView.context.getString(R.string.str_file_summary)
            val spanTextSumary = SpannableString(des.chatDetail.firstOrNull()?.message)
            spanTextSumary.setSpan(ForegroundColorSpan(Color.WHITE), 0, strFileSummary.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.tvTextSummary.text = spanTextSumary
            binding.tvFileName.text = des.fileName ?: "${des.filePaths.size} images"
            binding.tvTimeChat.text = DateUtils.getFormattedDate(des.lastTimeUpdate)
            binding.cbDelete.visibility = if (isDelete) View.VISIBLE else View.GONE
            if (isDelete) {
                (binding.containerView.layoutParams as ConstraintLayout.LayoutParams).marginEnd = (-24f).dp
            } else (binding.containerView.layoutParams as ConstraintLayout.LayoutParams).marginEnd = (24f).dp
            binding.root.setOnSingleClick {
                onClickItem?.invoke(des)
                if (isDelete) {
                    binding.cbDelete.isChecked = !binding.cbDelete.isChecked
                }
            }
        }
    }
}