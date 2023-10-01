package com.longkd.chatgpt_openai.feature.art.vyro.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.ImageStyleData
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemStyleImageArtBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class StyleGenerateAdapter(
    var listData: MutableList<ImageStyleData>,
    var clickListener: ItemClickListener<ImageStyleData>,
) : BaseListAdapter<ImageStyleData>(listData) {
    companion object {
        const val PAYLOAD_UPDATE = "PAYLOAD_UPDATE"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: ImageStyleData,
        position: Int
    ) {
        when (holder) {
            is AspectRatioViewholder -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    fun updateData(list: ArrayList<ImageStyleData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateDataDiff(list: ArrayList<ImageStyleData>) {
        val newRecipeList: MutableList<ImageStyleData> = ArrayList()
        list.forEach {
            newRecipeList.add(it)
        }
        val diffUtil = DiffUtil.calculateDiff(DiffCallback(listData, list))
        diffUtil.dispatchUpdatesTo(this)
        listData.clear()
        newRecipeList.forEach {
            listData.add(it)
        }
    }

    class DiffCallback(
        private val oldDataFileCloudDtoList: MutableList<ImageStyleData>,
        private val newDataFileCloudDtoList: MutableList<ImageStyleData>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataFileCloudDtoList.size

        override fun getNewListSize(): Int = newDataFileCloudDtoList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.url == newItem.url && oldItem.name == newItem.name
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return if (oldItem.isSelect != newItem.isSelect)
                PAYLOAD_UPDATE else
                null
        }
    }

    var mRecyclerView: RecyclerView? = null


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_style_image_art)

    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return AspectRatioViewholder(
            createBinding(parent, viewType) as ItemStyleImageArtBinding,
            clickListener,
        )
    }

    override fun bindPayload(

        holder: BaseViewHolder<ViewDataBinding>,
        item: ImageStyleData,
        position: Int,
        payload: Any
    ) {
        if (payload == PAYLOAD_UPDATE) {
            when (holder) {
                is AspectRatioViewholder -> {
                    holder.updateSelected(item)
                }
            }
        } else {
            bind(holder, item, position)
        }
    }

    class AspectRatioViewholder(
        mBinding: ItemStyleImageArtBinding,
        val callback: ItemClickListener<ImageStyleData>
    ) : BaseViewHolder<ItemStyleImageArtBinding>(mBinding) {
        fun bindData(data: ImageStyleData) {
            binding.root.setOnSingleClick {
                callback.onClick(data, adapterPosition)
            }
            binding.tvStyleArt.text = data.convertNameStyle()
            data.url?.let {
                val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                Glide.with(itemView.rootView)
                    .load(it)
                    .apply(requestOptions)
                    .placeholder(R.drawable.img_default_art)
                    .into(binding.image)
            }
            updateSelected(data)
        }

        fun updateSelected(data: ImageStyleData) {
            if (data.isSelect == true) {
                binding.root.setBackgroundResource(R.drawable.bg_border_choose_version_art)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_border_background_tab_layout)
            }
        }
    }
}