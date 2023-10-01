package com.longkd.chatgpt_openai.base.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.longkd.chatgpt_openai.R

abstract class BaseBottomSheetDialogFragment<T : ViewDataBinding>() :
    BottomSheetDialogFragment() {

    private var mRootView: View? = null
    protected lateinit var viewDataBinding: T

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    protected abstract val backgroundTransparent: Boolean

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialog?.setOnShowListener { expandDialog() }
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        dialog?.window?.decorView?.fitsSystemWindows = true
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mRootView = viewDataBinding.root
        return mRootView
    }

    protected fun expandDialog() {
        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
            BottomSheetBehavior.from<View?>(it).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        arguments?.let { handleArguments(it) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (backgroundTransparent) setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(@NonNull view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.executePendingBindings()
        viewDataBinding.lifecycleOwner = this
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setHideKeyboardFocus(view: View) {
        if (view !is TextInputEditText) {
            view.setOnTouchListener { _: View?, _: MotionEvent? ->
                val inputManager =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, 0)
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setHideKeyboardFocus(innerView)
            }
        }
    }

    protected open fun handleArguments(arguments: Bundle) {
        // open fun
    }
}