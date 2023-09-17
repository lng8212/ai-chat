package com.longkd.base_android.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * @Author: longkd
 * @Since: 21:20 - 12/08/2023
 */
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

inline fun <T : ViewBinding> ViewGroup.viewBinding(
    crossinline inflater: (LayoutInflater, ViewGroup) -> T
): T {
    return inflater.invoke(LayoutInflater.from(context), this)
}
