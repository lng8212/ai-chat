package com.longkd.chatgpt_openai.feature.home.viewholder

import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.CharacterDto
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemListCharacterBinding
import com.bumptech.glide.Glide

class CharacterGridVH(
    mBinding: ItemListCharacterBinding,
    val callback: ItemClickListener<CharacterDto>
) : BaseViewHolder<ItemListCharacterBinding>(mBinding) {
    fun bindData(data: CharacterDto) {
        binding.listItemCharacterImg.let {
            Glide.with(it).load(data.resID).into(binding.listItemCharacterImg)
        }
        binding.root.setOnSingleClick {
            callback.onClick(data, adapterPosition)
        }
        binding.listItemCharacterContainer?.isSelected = data.isSelected
    }

    fun updateSelected(isSelected: Boolean) {
        binding.listItemCharacterContainer?.isSelected = isSelected
    }

}