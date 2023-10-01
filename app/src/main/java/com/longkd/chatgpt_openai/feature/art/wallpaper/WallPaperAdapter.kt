package com.longkd.chatgpt_openai.feature.art.wallpaper

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemGeneratePhotoBinding

class WallPaperAdapter(
    val list: MutableList<WallPaperData>,
    val callback: ItemClickListener<WallPaperData>
) : BaseListAdapter<WallPaperData>(list.toMutableList()) {
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: WallPaperData, position: Int) {
        (holder as WallPaperViewHolder).bindData(item)
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: WallPaperData,
        position: Int,
        payload: Any
    ) {
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return WallPaperViewHolder(
            createBinding(parent, viewType) as ItemGeneratePhotoBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_generate_photo)
    }
}