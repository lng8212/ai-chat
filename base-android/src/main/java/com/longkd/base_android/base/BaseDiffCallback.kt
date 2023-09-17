package com.longkd.base_android.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * @Author: longkd
 * @Since: 10:48 - 12/08/2023
 */
class BaseDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem is ItemDiff) {
            oldItem.isItemTheSame(newItem as ItemDiff)
        } else {
            oldItem == newItem
        }
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem is ItemDiff) {
            oldItem.isContentTheSame(newItem as ItemDiff)
        } else {
            oldItem == newItem
        }
    }
}
