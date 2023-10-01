package com.longkd.chatgpt_openai.feature.art.vyro.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.ModelGenerateArt
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemStyleImageArtBinding

class ModelGenerateAdapter(
    val list: MutableList<ModelGenerateArt>,
    val callback: ItemClickListener<ModelGenerateArt>
) : BaseListAdapter<ModelGenerateArt>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: ModelGenerateArt, position: Int) {
        (holder as ModelGenerateViewHolder).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: ModelGenerateArt,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return ModelGenerateViewHolder(
            createBinding(parent, viewType) as ItemStyleImageArtBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_style_image_art)
    }

    class ModelGenerateViewHolder(
        mBinding: ItemStyleImageArtBinding,
        val callback: ItemClickListener<ModelGenerateArt>,
    ) : BaseViewHolder<ItemStyleImageArtBinding>(mBinding) {
        fun bindData(item: ModelGenerateArt) {
            binding.tvDescription.visibility = View.VISIBLE
            binding.tvDescription.text = item.modelDes
            binding.tvStyleArt.text = item.modelTitle
            binding.image.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.drawId))
        }
    }
}