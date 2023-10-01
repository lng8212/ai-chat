package com.longkd.chatgpt_openai.feature.summary

import android.graphics.Bitmap
import android.os.Parcelable
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.SummaryData
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSummaryFileBinding
import com.bumptech.glide.Glide

class SummaryFileAdapter(
    val list: MutableList<Parcelable>
) : BaseListAdapter<Parcelable>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: Parcelable, position: Int) {
        (holder as GalleryAdapterVH).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: Parcelable,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return GalleryAdapterVH(
            createBinding(parent, viewType) as ItemSummaryFileBinding
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_summary_file)
    }

    class GalleryAdapterVH(
        mBinding: ItemSummaryFileBinding
    ) : BaseViewHolder<ItemSummaryFileBinding>(mBinding) {
        fun bindData(des: Parcelable) {
            when(des) {
                is SummaryData -> {
                    binding.image.visible()
                    Glide.with(itemView.rootView)
                        .load(des.uri)
                        .placeholder(R.drawable.img_default_art)
                        .into(binding.image)
                }
                is Bitmap -> {
                    binding.pdfPage.visible()
                    binding.pdfPage.setImageBitmap(des)
                }
                is SummaryHistoryDto -> {
                    binding.tvTextSummary.text = des.summaryContent
                    binding.tvTextSummary.visible()
                    binding.image.gone()
                }
            }
        }
    }
}