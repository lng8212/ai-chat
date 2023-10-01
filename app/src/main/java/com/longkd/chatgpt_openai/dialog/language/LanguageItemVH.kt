package com.longkd.chatgpt_openai.dialog.language


import android.view.View
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemSelectLanguageBinding

class LanguageItemVH(
    mBinding: ItemSelectLanguageBinding,
    val callback: ItemClickListener<LanguageDto>,
) :
    BaseViewHolder<ItemSelectLanguageBinding>(mBinding) {
    fun bindData(data: LanguageDto) {
        binding.apply {
            itemSelectLanguageRb.text = data.data.value
            itemSelectLanguageRb.isChecked = data.isSelected
            itemSelectLanguageActionView.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    callback.onClick(data, layoutPosition)
                }
            })
        }
    }

    fun updateCheckBox(data: LanguageDto){
        binding.itemSelectLanguageRb.isChecked = data.isSelected
    }
}