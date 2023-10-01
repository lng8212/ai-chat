package com.longkd.chatgpt_openai.feature.chat.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.DetailTopicData
import com.longkd.chatgpt_openai.base.model.TopicData
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemDetailSubTopicBinding
import com.longkd.chatgpt_openai.databinding.ItemTitleSubTopicBinding

class AllTopicAdapter(
    val listData: ArrayList<Pair<Int, Any>>,
) : BaseListAdapter<Pair<Int, Any>>(listData) {
    var clickItemTopic: ((data: DetailTopicData?) -> Unit) ? = null
    var clickArrowRight: ((title: String) -> Unit) ? = null
    override fun bind(holder: BaseViewHolder<ViewDataBinding>, item: Pair<Int, Any>, position: Int) {
        when (holder) {
            is TitleTopicViewHolder -> {
                if (listData.size > position) {
                    holder.bindData(listData[position].second as String)
                }
            }
            is DetailTopicViewHolder -> {
                if (listData.size > position)
                    holder.bindData(listData[position].second as List<DetailTopicData>)
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: Pair<Int, Any>,
        position: Int,
        payload: Any
    ) {
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].first
    }

    fun updateData(list: List<TopicData>) {
        listData.clear()
        val arrTopicDetail = DetailTopicData.values().toMutableList()
        list.forEach {
            if (it.topicId == 0) return@forEach
            addItem(TITLE_TOPIC, it.title as String)
            addItem(DETAIL_TOPIC, arrTopicDetail.filter { data -> it.topicId == data.topicId })
        }
        notifyDataSetChanged()
    }

    private fun addItem(type: Int, item: Any) {
        listData.add(type to item)
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return when (viewType) {
            TITLE_TOPIC -> {
                TitleTopicViewHolder(
                    createBinding(parent, viewType) as ItemTitleSubTopicBinding
                )
            }
            else -> DetailTopicViewHolder(
                createBinding(parent, viewType) as ItemDetailSubTopicBinding
            )
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when (viewType) {
            TITLE_TOPIC ->
                parent.bindingInflate(R.layout.item_title_sub_topic)
            else -> {
                parent.bindingInflate(R.layout.item_detail_sub_topic)
            }
        }
    }

    inner class TitleTopicViewHolder(
        mBinding: ItemTitleSubTopicBinding
    ) : BaseViewHolder<ItemTitleSubTopicBinding>(mBinding) {
        fun bindData(des: String) {
            binding.tvTitle.text = des
            binding.icArrowRight.setOnSingleClick {
                clickArrowRight?.invoke(des)
            }
        }
    }

    inner class DetailTopicViewHolder(
        mBinding: ItemDetailSubTopicBinding,
    ) : BaseViewHolder<ItemDetailSubTopicBinding>(mBinding) {
        fun bindData(des: List<DetailTopicData>) {
            val detailTopicAdapter = DetailTopicAdapter(des.toMutableList(), object :
                ItemClickListener<DetailTopicData> {
                override fun onClick(item: DetailTopicData?, position: Int) {
                    clickItemTopic?.invoke(item)
                }
            }, true)
            binding.rclView.adapter = detailTopicAdapter
            binding.rclView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    companion object {
        const val TITLE_TOPIC = 1
        const val DETAIL_TOPIC = 2
    }
}