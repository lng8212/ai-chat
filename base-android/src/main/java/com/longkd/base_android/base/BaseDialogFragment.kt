package com.longkd.base_android.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.longkd.base_android.R
import com.longkd.base_android.utils.BindingReflex

/**
 * @Author: longkd
 * @Date: 9:37 AM - 8/24/2023
 */
abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog_Custom)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val windowParams = window?.attributes
            windowParams?.flags =
                windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            windowParams?.flags =
                windowParams?.flags?.or(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.attributes = windowParams
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
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