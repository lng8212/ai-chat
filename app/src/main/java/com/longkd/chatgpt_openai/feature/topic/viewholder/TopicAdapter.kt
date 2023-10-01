package com.longkd.chatgpt_openai.feature.topic.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.TopicListItemBinding

class TopicAdapter(
    private var listData: ArrayList<TopicDto>,
    val callback: ItemClickListener<TopicDto>
) : BaseListAdapter<TopicDto>(listData) {
    var mRecyclerView: RecyclerView? = null

    companion object {
        const val UPDATE_PAYLOAD = "UPDATE_PAYLOAD"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: TopicDto,
        position: Int
    ) {
        when (holder) {
            is TopicVH -> {
                if (listData.size > position) holder.bindData(listData[position])
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: TopicDto,
        position: Int,
        payload: Any
    ) {

    }


    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return TopicVH(
            createBinding(parent, viewType) as TopicListItemBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.topic_list_item)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun updateData(list: List<TopicDto>) {
        listData = ArrayList(list)
        notifyDataSetChanged()
    }

    fun updateDataPayload(list: List<TopicDto>, position: Int) {
        listData = ArrayList(list)
        notifyDataSetChanged()
    }

}