package com.longkd.chatgpt_openai.feature.art

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.Size
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemAspectRatioBinding

class AspectRatioAdapter(
    var listData: MutableList<Size>,
    private var clickListener: ItemClickListener<Size>,
) : BaseListAdapter<Size>(listData) {
    companion object {
        const val PAYLOAD_UPDATE = "PAYLOAD_UPDATE"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: Size,
        position: Int
    ) {
        when (holder) {
            is AspectRatioViewholder -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    fun updateData(list: List<Size>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    class DiffCallback(
        private val oldDataFileCloudDtoList: MutableList<Size>,
        private val newDataFileCloudDtoList: MutableList<Size>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataFileCloudDtoList.size

        override fun getNewListSize(): Int = newDataFileCloudDtoList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.size == newItem.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.size == newItem.size && oldItem.isSelected == newItem.isSelected
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return if (oldItem.isSelected != newItem.isSelected)
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
        return parent.bindingInflate(R.layout.item_aspect_ratio)

    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return AspectRatioViewholder(
            createBinding(parent, viewType) as ItemAspectRatioBinding,
            clickListener,
        )
    }

    override fun bindPayload(

        holder: BaseViewHolder<ViewDataBinding>,
        item: Size,
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
        mBinding: ItemAspectRatioBinding,
        val callback: ItemClickListener<Size>
    ) : BaseViewHolder<ItemAspectRatioBinding>(mBinding) {
        fun bindData(data: Size) {
            binding.tvAspectRadio.text = data.size
            binding.root.setOnSingleClick {
                callback.onClick(data, adapterPosition)
            }
            updateSelected(data)
        }

        fun updateSelected(data: Size) {
            if (data.isSelected) {
                binding.llnAspectRadio.setBackgroundResource(R.drawable.bg_border_choose_version_art)
            } else {
                binding.llnAspectRadio.setBackgroundResource(R.drawable.bg_border_background_tab_layout)
            }
        }
    }
}

data class AspectRatioData(
    val ratio: String? = "1:1",
    var isSelect: Boolean? = false
)