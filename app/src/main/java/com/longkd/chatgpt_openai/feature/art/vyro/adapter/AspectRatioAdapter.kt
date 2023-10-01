package com.longkd.chatgpt_openai.feature.art.vyro.adapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.dp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemAspectRatioBinding

class AspectRatioAdapter(
    var listData: ArrayList<AspectRatioData>,
    var clickListener: ItemClickListener<AspectRatioData>,
) : BaseListAdapter<AspectRatioData>(listData) {
    companion object {
        const val PAYLOAD_UPDATE = "PAYLOAD_UPDATE"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: AspectRatioData,
        position: Int
    ) {
        when (holder) {
            is AspectRatioViewholder -> {
                if (listData.size > position)
                    holder.bindData(listData[position])
            }
        }
    }

    fun updateData(list: ArrayList<AspectRatioData>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateDataDiff(list: ArrayList<AspectRatioData>) {
        val newRecipeList: MutableList<AspectRatioData> = ArrayList()
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
        private val oldDataFileCloudDtoList: ArrayList<AspectRatioData>,
        private val newDataFileCloudDtoList: ArrayList<AspectRatioData>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldDataFileCloudDtoList.size

        override fun getNewListSize(): Int = newDataFileCloudDtoList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.ratio == newItem.ratio
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldDataFileCloudDtoList[oldItemPosition]
            val newItem = newDataFileCloudDtoList[newItemPosition]
            return oldItem.ratio == newItem.ratio && oldItem.isSelect == newItem.isSelect
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
        item: AspectRatioData,
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
        val callback: ItemClickListener<AspectRatioData>
    ) : BaseViewHolder<ItemAspectRatioBinding>(mBinding) {
        fun bindData(data: AspectRatioData) {
            binding.tvAspectRadio.text = data.ratio
            val ratioWidth = data.ratio?.split(":")!![0].toInt()
            val ratioHeight = data.ratio.split(":")[1].toInt()
            val layoutParam = binding.icAspectRadio.layoutParams
            if (ratioWidth == ratioHeight ) {
                layoutParam.width = (14f).dp
                layoutParam.height = (14f).dp
            } else if (ratioWidth > ratioHeight) {
                layoutParam.width = (14f).dp * ratioWidth / ratioHeight
                layoutParam.height = (14f).dp
            } else {
                layoutParam.width = (14f).dp
                layoutParam.height = (14f).dp * ratioHeight / ratioWidth
            }
            binding.icAspectRadio.layoutParams = layoutParam
            binding.icAspectRadio.setBackgroundResource(R.drawable.bg_border_white_4dp)
            binding.root.setOnSingleClick {
                callback.onClick(data, adapterPosition)
            }
            updateSelected(data)
        }

        fun updateSelected(data: AspectRatioData) {
            if (data.isSelect == true) {
                binding.llnAspectRadio.setBackgroundResource(R.drawable.bg_border_choose_version_art)
            } else {
                binding.llnAspectRadio.setBackgroundResource(R.drawable.bg_border_background_tab_layout)
            }
        }
    }
}

data class AspectRatioData(
    val ratio: String ? = "1:1",
    var isSelect: Boolean ? = false
)