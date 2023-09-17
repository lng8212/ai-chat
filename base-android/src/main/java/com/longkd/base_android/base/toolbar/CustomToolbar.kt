package com.longkd.base_android.base.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.longkd.base_android.R
import com.longkd.base_android.databinding.CustomToolbarBinding
import com.longkd.base_android.ktx.setImageDrawableWithAnimation
import com.longkd.base_android.utils.viewBinding

/**
 * @Author: longkd
 * @Since: 20:19 - 12/08/2023
 */
class CustomToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {


    private val binding = viewBinding(CustomToolbarBinding::inflate)

    fun configure(toolbarConfiguration: ToolbarConfiguration?) {
        if (toolbarConfiguration == null) {
            isVisible = false
            return
        }
        with(toolbarConfiguration) {
            initTitle(titleResId)
            configureStartButton(startIconResId, startIconClick)
            configureEndButton(endIconResId, endIconClick)
        }
        initBackgroundColor(R.color.white)
        isVisible = true
    }

    private fun initTitle(titleResId: Int?) {
        with(binding.txtTitle) {
            isVisible = titleResId != null
            if (titleResId != null) setText(titleResId)
        }
    }

    fun setTitle(title: String?) {
        with(binding.txtTitle) {
            isVisible = title != null
            text = title.orEmpty()
        }
    }

    private fun configureStartButton(resId: Int?, clickAction: (() -> Unit)?) {
        binding.btnStart.apply {
            if (resId == null) {
                visibility = View.INVISIBLE
                return
            }
            setImageResource(resId)
            setOnClickListener { clickAction?.invoke() }
            isVisible = true
        }
    }

    private fun configureEndButton(resId: Int?, clickAction: (() -> Unit)?) {
        binding.btnEnd.apply {
            if (resId == null) {
                visibility = View.INVISIBLE
                return
            }
            setImageResource(resId)
            setOnClickListener { clickAction?.invoke() }
            isVisible = true
        }
    }

    fun changeEndButtonDrawable(resId: Int) {
        binding.btnEnd.apply {
            val drawable = AppCompatResources.getDrawable(context, resId)
            setImageDrawableWithAnimation(drawable)
        }
    }


    private fun initBackgroundColor(resId: Int?) {
        resId?.let {
            binding.root.setBackgroundResource(resId)
        }
    }

}
