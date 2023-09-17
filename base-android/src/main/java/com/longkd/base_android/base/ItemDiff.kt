package com.longkd.base_android.base

/**
 * @Author: longkd
 * @Since: 10:48 - 12/08/2023
 */
interface ItemDiff {
    fun isItemTheSame(other: ItemDiff): Boolean
    fun isContentTheSame(other: ItemDiff): Boolean
}