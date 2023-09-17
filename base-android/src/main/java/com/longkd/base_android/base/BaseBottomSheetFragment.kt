package com.longkd.base_android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.longkd.base_android.R
import com.longkd.base_android.utils.BindingReflex

/**
 * @Author: longkd
 * @Date: 10:15 AM - 8/24/2023
 */
abstract class BaseBottomSheetFragment<VB : ViewBinding> : BottomSheetDialogFragment() {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    @LayoutRes
    protected open val layoutId = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater = inflater, container = container)
        initView()
        return _binding!!.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    abstract fun initView()
    private fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return if (layoutId == -1) {
            BindingReflex.reflexViewBinding(javaClass, inflater)
        } else {
            try {
                DataBindingUtil.inflate(inflater, layoutId, container, false)
            } catch (e: Exception) {
                BindingReflex.reflexViewBinding<VB>(javaClass, inflater)
            }
        }
    }

}