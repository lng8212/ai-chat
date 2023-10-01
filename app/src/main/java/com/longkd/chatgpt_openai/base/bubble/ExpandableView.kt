package com.longkd.chatgpt_openai.base.bubble

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.annotation.StyleRes
import com.longkd.chatgpt_openai.R

class ExpandableView(
    private val builder: Builder,
) : BaseFloatingView(builder.context) {

    init {
        setupLayoutParams()
        ScreenInfo.getScreenSize(builder.context).also {
            ScreenInfo.widthPx = it.width
            ScreenInfo.heightPx = it.height
        }
    }

    // interface -----------------------------------------------------------------------------------

    interface Action {
        fun popToBubble() {}

    }

    interface Listener {
        fun onOpenExpandableView() {}
        fun onCloseExpandableView() {}
    }

    // public --------------------------------------------------------------------------------------


    private var isInitialized = false
    fun show() {

        if (builder.view != null) {
            super.show(builder.view!!)
            builder.listener.onOpenExpandableView()
            return
        }


        if (isInitialized) {
//            ContextCompat.getMainExecutor(builder.context).execute {}
            builder.composeLifecycleOwner?.onStart()
            builder.composeLifecycleOwner?.onResume()

        } else {
            builder.composeLifecycleOwner?.onCreate()
            builder.composeLifecycleOwner?.onStart()

            isInitialized = true
        }
        builder.listener.onOpenExpandableView()
    }


    fun remove() {
        builder.view?.let {
            super.remove(it)
        }

//        builder.composeView?.also {
//            super.remove(it)
//            builder.composeLifecycleOwner?.onPause()
//            builder.composeLifecycleOwner?.onStop()
//
//        }
        builder.listener.onCloseExpandableView()
    }

    override fun destroy() {
        super.destroy()
        builder.composeLifecycleOwner?.onDestroy()

    }


    // private -------------------------------------------------------------------------------------

    override fun setupLayoutParams() {
        super.setupLayoutParams()

        windowParams.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = Gravity.BOTTOM
            flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dimAmount = builder.dim         // default = 0.5f
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            builder.viewStyle?.let {
                windowAnimations = it
            }

        }


    }


    // builder class -------------------------------------------------------------------------------

    class Builder(internal val context: Context) {

        internal var view: View? = null
        internal var viewStyle: Int? = R.style.default_bubble_style

        internal var composeLifecycleOwner: ComposeLifecycleOwner? = null

        internal var listener = object : Listener {}

        internal var dim = 0.5f

        fun view(view: View): Builder {
            this.view = view
            return this
        }

        fun addExpandableViewListener(listener: Listener): Builder {
            this.listener = listener
            return this
        }

        fun expandableViewStyle(@StyleRes style: Int?): Builder {
            viewStyle = style
            return this
        }

        /**
         * @param dimAmount background opacity below the expandable-view
         * */
        fun dimAmount(dimAmount: Float): Builder {
            this.dim = dimAmount
            return this
        }


        fun build(): ExpandableView {
            return ExpandableView(this)
        }

    }

}