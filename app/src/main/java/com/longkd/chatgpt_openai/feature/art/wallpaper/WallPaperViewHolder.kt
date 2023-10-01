package com.longkd.chatgpt_openai.feature.art.wallpaper

import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemGeneratePhotoBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class WallPaperViewHolder(
    mBinding: ItemGeneratePhotoBinding,
    val callback: ItemClickListener<WallPaperData>,
) : BaseViewHolder<ItemGeneratePhotoBinding>(mBinding) {
    fun bindData(des: WallPaperData) {
        des.url?.let {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            Glide.with(itemView.rootView)
                .load(it)
                .apply(requestOptions)
                .placeholder(R.drawable.img_default_art)
                .into(binding.image)
        }
        binding.root.setOnSingleClick {
            callback.onClick(des)
        }
    }
}