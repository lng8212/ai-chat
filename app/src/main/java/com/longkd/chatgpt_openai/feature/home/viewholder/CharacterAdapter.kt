package com.longkd.chatgpt_openai.feature.home.viewholder

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.CharacterDto
import com.longkd.chatgpt_openai.base.util.bindingInflate
import com.longkd.chatgpt_openai.base.widget.BaseListAdapter
import com.longkd.chatgpt_openai.base.widget.BaseViewHolder
import com.longkd.chatgpt_openai.databinding.ItemListCharacterBinding

class CharacterAdapter(
    private var listData: ArrayList<CharacterDto>,
    val callback: ItemClickListener<CharacterDto>
) : BaseListAdapter<CharacterDto>(listData) {
    var mRecyclerView: RecyclerView? = null

    companion object {
        const val UPDATE_PAYLOAD = "UPDATE_PAYLOAD"
    }

    override fun bind(
        holder: BaseViewHolder<ViewDataBinding>,
        item: CharacterDto,
        position: Int
    ) {
        when (holder) {
            is CharacterGridVH -> {
                if (listData.size > position) holder.bindData(listData[position])
            }
        }
    }

    override fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: CharacterDto,
        position: Int,
        payload: Any
    ) {
        if (payload == UPDATE_PAYLOAD) {
            (holder as? CharacterGridVH)?.updateSelected(item.isSelected)
        } else {

        }
    }


    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding> {
        return CharacterGridVH(
            createBinding(parent, viewType) as ItemListCharacterBinding,
            callback
        )
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return parent.bindingInflate(R.layout.item_list_character)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun updateData(list: List<CharacterDto>) {
        listData = ArrayList(list)
        notifyDataSetChanged()
    }

    fun updateDataPayload(list: List<CharacterDto>, position: Int) {
        listData = ArrayList(list)
        notifyDataSetChanged()
    }

}