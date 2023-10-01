package com.longkd.chatgpt_openai.base.widget

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseListAdapter<T>(var listDto: MutableList<T>) :
    RecyclerView.Adapter<BaseViewHolder<ViewDataBinding>>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding> {
        val vh = getViewHolder(parent, viewType)
        when (vh) {

        }
        return vh
    }

    fun setDatas(mList: MutableList<T>) {
        this.listDto.clear()
        this.listDto.addAll(mList)
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<ViewDataBinding>) {
        super.onViewAttachedToWindow(holder)
        when (holder) {

        }
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<ViewDataBinding>) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {

        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewDataBinding>,
        position: Int
    ) {
        (holder as BaseViewHolder<*>).binding.root.tag = position
        getItem(position)?.let { bind(holder, it, position) }
        holder.binding.executePendingBindings()
    }

    fun getItem(position: Int): T? {
        return listDto.getOrNull(position)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ViewDataBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            getItem(position)?.let { bindPayload(holder, it, position, payloads[0]) }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return listDto.size
    }


    protected abstract fun bind(holder: BaseViewHolder<ViewDataBinding>, item: T, position: Int)
    protected abstract fun bindPayload(
        holder: BaseViewHolder<ViewDataBinding>,
        item: T,
        position: Int,
        payload: Any
    )

    abstract fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewDataBinding>

    abstract fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding

}


