package com.longkd.chatgpt_openai.feature.art

import android.view.View
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemStyleArtBinding
import com.bumptech.glide.Glide

class StyleViewHolder(
    mBinding: ItemStyleArtBinding,
    val callback: ItemClickListener<StyleArtDto>
) : BaseViewHolder<ItemStyleArtBinding>(mBinding) {
    fun bindData(data: StyleArtDto) {
        binding.itStyleArtImv.let {
            Glide.with(it).load(data.resID).into(binding.itStyleArtImv)
        }
        binding.itStyleArtTvName.text = data.name
        binding.itStyleArtTvNameSelect.text = data.name
        updateSelected(data)
        binding.root.setOnSingleClick {
            callback.onClick(data, adapterPosition)
        }

    }

    fun updateSelected(item : StyleArtDto) {
        binding.itStyleArtRlBg.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE
        if (item.isSelected){
            binding.itStyleArtTvName.gone()
            binding.itStyleArtTvNameSelect.visible()
        } else {
            binding.itStyleArtTvName.visible()
            binding.itStyleArtTvNameSelect.gone()
        }
    }

}