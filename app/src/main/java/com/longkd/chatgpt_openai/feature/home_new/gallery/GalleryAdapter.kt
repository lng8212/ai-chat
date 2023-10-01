package com.longkd.chatgpt_openai.feature.home_new.gallery

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemGalleryBinding
import com.longkd.chatgpt_openai.base.model.SummaryData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class GalleryAdapter(
    val list: MutableList<SummaryData>,
    val fromScreen: String = Constants.GALLERY_TYPE.OCR,
    val callback: ItemClickListener<SummaryData>,
) : BaseListAdapter<SummaryData>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: SummaryData, position: Int) {
        (holder as GalleryAdapterVH).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: SummaryData,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return GalleryAdapterVH(
            createBinding(parent, viewType) as ItemGalleryBinding,
            callback,
            fromScreen
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_gallery)
    }

    class GalleryAdapterVH(
        mBinding: ItemGalleryBinding,
        val callback: ItemClickListener<SummaryData>,
        val fromScreen: String
    ) : BaseViewHolder<ItemGalleryBinding>(mBinding) {
        fun bindData(des: SummaryData) {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            Glide.with(itemView.rootView)
                .load(des.uri)
                .apply(requestOptions)
                .placeholder(R.drawable.img_default_art)
                .into(binding.image)
            binding.root.setOnSingleClick {
                callback.onClick(des)
            }
            if (fromScreen == Constants.GALLERY_TYPE.SUMMARY) {
                binding.ivCheck.visibility = if (des.isSelect) View.VISIBLE else View.GONE
            }
        }
    }
}