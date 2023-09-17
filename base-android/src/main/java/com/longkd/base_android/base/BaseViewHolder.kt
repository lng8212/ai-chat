package com.longkd.base_android.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * @Author: longkd
 * @Since: 22:26 - 12/08/2023
 */
abstract class BaseViewHolder<in T : Any, VB : ViewBinding>(
    private val _binding: VB? = null,
    view: View? = null
) :
    RecyclerView.ViewHolder(
        _binding?.root ?: view ?: throw Exception("Item view or item binding is required")
    ) {
    protected val binding: VB get() = _binding!!

    abstract fun onBind(item: T)
    open fun onBindPayLoad(item: T) {}
}