package com.longkd.chatgpt_openai.feature.language.view_holder


import android.view.View
import androidx.core.content.ContextCompat
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemFirstLanguageBinding

class LanguageFirstItemVH(
    mBinding: ItemFirstLanguageBinding,
    val callback: ItemClickListener<LanguageDto>,
) :
    BaseViewHolder<ItemFirstLanguageBinding>(mBinding) {
    fun bindData(data: LanguageDto) {
        binding.apply {
            itFirstLanguageTvName.text = data.data.value
            if (data.isSelected) {
                itFirstLanguageImvCheck.visible()
            } else
                itFirstLanguageImvCheck.gone()
            itFirstLanguageImv.setImageDrawable(ContextCompat.getDrawable(mContext,data.data.flag))
            itemView.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View?) {
                    callback.onClick(data, layoutPosition)
                }
            })
        }
    }

    fun updateCheckBox(data: LanguageDto){
        if (data.isSelected) {
            binding.itFirstLanguageImvCheck.visible()
        } else
            binding.itFirstLanguageImvCheck.gone()
    }
}