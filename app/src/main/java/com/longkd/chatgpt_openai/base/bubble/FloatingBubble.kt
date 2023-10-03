package com.longkd.chatgpt_openai.base.bubble

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import android.util.Size
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.longkd.chatgpt_openai.R

class FloatingBubble
internal constructor(
    private val builder: Builder
) {

    private var bubbleView: FloatingBubbleView
    private var closeBubbleView: FloatingCloseBubbleView? = null
    private var bottomBackground: FloatingBottomBackground? = null

    init {
        ScreenInfo.getScreenSize(builder.context).also {
            ScreenInfo.widthPx = it.width
            ScreenInfo.heightPx = it.height
        }
        ScreenInfo.statusBarHeightPx = ScreenInfo.getStatusBarHeight(builder.context)
        ScreenInfo.softNavBarHeightPx = ScreenInfo.getSoftNavigationBarSize(builder.context)

        bubbleView = FloatingBubbleView(
            builder.addFloatingBubbleListener(CustomBubbleListener())
        )

        if (builder.isCloseBubbleEnabled) {
            closeBubbleView = FloatingCloseBubbleView(builder)
        }

        if (builder.isBottomBackgroundEnabled) {
            bottomBackground = FloatingBottomBackground(builder)
        }

    }

    // listener ------------------------------------------------------------------------------------


    interface Action {

        /**
         * if you do not override expandable view, throw exception
         * */
        fun navigateToExpandableView() {}

    }

    interface Listener {

        /**
         * the location of the finger on the screen
         * */
        fun onDown(x: Float, y: Float) {}

        /**
         * the location of the finger on the screen
         * */
        fun onUp(x: Float, y: Float) {}

        /**
         * the location of the finger on the screen
         * */
        fun onMove(x: Float, y: Float) {}

        fun onClick() {}

        fun onDestroy() {}

    }
    private var preX = 0
    private var preY = 0
    // internal func ---------------------------------------------------------------------------------

    internal fun showIcon() {
        bubbleView.show()
    }

    internal fun removeIcon() {
        bubbleView.remove()
    }
    internal fun bubbleExpandableView() {
        bubbleView.setImageBubble(R.drawable.ic_bubble_chat_expand)
        bubbleView.animateTo(ScreenInfo.widthPx*0.8f,(ScreenInfo.heightPx - ScreenInfo.statusBarHeightPx-  ScreenInfo.softNavBarHeightPx)*0.2f - bubbleView.height)
        bubbleView.setLocation(ScreenInfo.widthPx*0.8f,(ScreenInfo.heightPx - ScreenInfo.statusBarHeightPx -  ScreenInfo.softNavBarHeightPx)*0.2f - bubbleView.height)

    }
    internal fun returnPreLocation(){
        bubbleView.setImageBubble(R.drawable.ic_bubble_chat)
        bubbleView.animateTo(bubbleView.prevPoint.x.toFloat(),bubbleView.prevPoint.y.toFloat())
        bubbleView.setLocation(bubbleView.prevPoint.x.toFloat(),bubbleView.prevPoint.y.toFloat())
        tryRemoveCloseBubbleAndBackground()
    }
    internal fun tryShowCloseBubbleAndBackground() {
        bottomBackground?.show()
        closeBubbleView?.show()
    }

    internal fun tryRemoveCloseBubbleAndBackground() {
        bottomBackground?.remove()
        closeBubbleView?.remove()
    }

    // private func --------------------------------------------------------------------------------

    private inner class CustomBubbleListener : Listener {

        private var isCloseBubbleVisible = false
        private var isBubbleAnimated = false

        override fun onMove(x: Float, y: Float) {
            when (builder.behavior) {
                BubbleBehavior.DYNAMIC_CLOSE_BUBBLE -> {
                    isCloseBubbleVisible = false
                    bubbleView.updateLocation(x, y)
                    val (bubbleX, bubbleY) = bubbleView.rawLocationOnScreen()
                    closeBubbleView?.animateCloseIconByBubble(bubbleX.toInt(), bubbleY.toInt())
                }
                BubbleBehavior.FIXED_CLOSE_BUBBLE -> {
                    if (isFingerInsideClosableArea(x, y)) {
                        if (isBubbleAnimated.not()) {

                            val xOffset = (closeBubbleView!!.width - builder.bubbleSizePx.width) / 2
                            val yOffset = (closeBubbleView!!.height - builder.bubbleSizePx.height) / 2

                            val xUpdated = closeBubbleView!!.baseX.toFloat() + xOffset
                            val yUpdated = closeBubbleView!!.baseY.toFloat() + yOffset

                            bubbleView.animateTo(xUpdated, yUpdated)
                            bubbleView.setLocation(xUpdated, yUpdated)

                            isBubbleAnimated = true
                        }

                    } else {
                        isBubbleAnimated = false
                        bubbleView.updateLocation(x, y)
                    }
                }
            }

            if (builder.isCloseBubbleEnabled && !isCloseBubbleVisible) {
                tryShowCloseBubbleAndBackground()
                isCloseBubbleVisible = true
                Log.d("xuannv", "onMove: ")
            }
        }

        override fun onUp(x: Float, y: Float) {
            isCloseBubbleVisible = false
            tryRemoveCloseBubbleAndBackground()

            val shouldDestroy = when(builder.behavior){
                BubbleBehavior.FIXED_CLOSE_BUBBLE -> isFingerInsideClosableArea(x, y)
                BubbleBehavior.DYNAMIC_CLOSE_BUBBLE -> {
                    val (bubbleX, bubbleY) = bubbleView.rawLocationOnScreen()
                    isBubbleInsideClosableArea(bubbleX.toInt(), bubbleY.toInt())
                }
            }

            if (shouldDestroy) {
                builder.listener?.onDestroy()
            } else {
                if (builder.isAnimateToEdgeEnabled) {
                    bubbleView.animateIconToEdge()
                }
            }
        }
    }

    /**
     * pass bubble location
     * */
    private fun isBubbleInsideClosableArea(x: Int, y: Int): Boolean {
        return closeBubbleView?.distanceRatioFromBubbleToClosableArea(
            x = x,
            y = y
        ) == 0.0f
    }

    private fun isFingerInsideClosableArea(x: Float, y: Float): Boolean {
        return closeBubbleView?.distanceRatioFromLocationToClosableArea(
            x = x,
            y = y
        ) == 0.0f
    }

    // builder -------------------------------------------------------------------------------------

    class Builder(internal val context: Context) {

        private val DEFAULT_BUBBLE_SIZE_PX = 160

        // bubble
        internal var iconView: View? = null
        internal var iconBitmap: Bitmap? = null
        internal var bubbleStyle: Int? = R.style.default_bubble_style
        internal var bubbleSizePx: Size = Size(DEFAULT_BUBBLE_SIZE_PX, DEFAULT_BUBBLE_SIZE_PX)

        // close-bubble
        internal var closeIconView: View? = null
        internal var closeIconBitmap: Bitmap? = null
        internal var closeBubbleStyle: Int? = R.style.default_close_bubble_style
        internal var closeBubbleSizePx: Size = Size(DEFAULT_BUBBLE_SIZE_PX, DEFAULT_BUBBLE_SIZE_PX)

        // config
        internal var startPoint = Point(0, 0)
        internal var elevation = 0
        internal var opacity = 1f
        internal var isCloseBubbleEnabled = true
        internal var isAnimateToEdgeEnabled = true
        internal var isBottomBackgroundEnabled = false

        internal var closablePerimeterDp = 100

        internal var listener: Listener? = null

        internal var behavior: BubbleBehavior = BubbleBehavior.FIXED_CLOSE_BUBBLE


        /**
         * choose behavior for the bubbles
         * */
        fun behavior(behavior: BubbleBehavior): Builder {
            this.behavior = behavior
            return this
        }

        /**
         * the more value, the larger closeable area
         *
         * @param dp distance between bubble and close-bubble
         * */
        fun closablePerimeter(dp: Int): Builder {
            this.closablePerimeterDp = dp
            return this
        }

        /**
         * @param enabled show gradient dark background on the bottom of the screen
         * */
        fun bottomBackground(enabled: Boolean): Builder {
            this.isBottomBackgroundEnabled = enabled
            return this
        }

        /**
         * @param enabled animate bubble to the left/right side of the screen
         * */
        fun enableAnimateToEdge(enabled: Boolean): Builder {
            isAnimateToEdgeEnabled = enabled
            return this
        }

        /**
         * @param enabled show close-bubble or not
         * */
        fun enableCloseBubble(enabled: Boolean): Builder {
            isCloseBubbleEnabled = enabled
            return this
        }

        // being developed, therefore this function not exposed to the outside package
        internal fun bubble(view: View): Builder {
            iconView = view
            return this
        }

        /**
         * set drawable to bubble with default size
         * */
        fun bubble(@DrawableRes drawable: Int): Builder {
            iconBitmap = ContextCompat.getDrawable(context, drawable)!!.toBitmap()
            return this
        }

        /**
         * set drawable to bubble width given width and height in dp
         * */
        fun bubble(@DrawableRes drawable: Int, widthDp: Int, heightDp: Int): Builder {
            bubbleSizePx = Size(widthDp.toPx, heightDp.toPx)
            return bubble(drawable)
        }

        /**
         * set bitmap to bubble width default size
         * */
        fun bubble(bitmap: Bitmap): Builder {
            iconBitmap = bitmap
            return this
        }

        /**
         * set bitmap to bubble width given width and height in dp
         * */
        fun bubble(bitmap: Bitmap, widthDp: Int, heightDp: Int): Builder {
            bubbleSizePx = Size(widthDp.toPx, heightDp.toPx)
            return bubble(bitmap)
        }

        /**
         * set open and exit animation to bubble
         * */
        fun bubbleStyle(@StyleRes style: Int?): Builder {
            this.bubbleStyle = style
            return this
        }

        // being developed, therefore this function is not exposed to the outside packages
        internal fun closeBubble(view: View): Builder {
            this.closeIconView = view
            return this
        }

        /**
         * set drawable to close-bubble with default size
         * */
        fun closeBubble(@DrawableRes drawable: Int): Builder {
            this.closeIconBitmap = ContextCompat.getDrawable(context, drawable)!!.toBitmap()
            return this
        }

        /**
         * set drawable to close-bubble with given width and height in dp
         * */
        fun closeBubble(@DrawableRes drawable: Int, widthDp: Int, heightDp: Int): Builder {
            this.closeBubbleSizePx = Size(widthDp.toPx, heightDp.toPx)
            return closeBubble(drawable)
        }

        /**
         * set bitmap to close-bubble with default size
         * */
        fun closeBubble(bitmap: Bitmap): Builder {
            closeIconBitmap = bitmap
            return this
        }

        /**
         * set open and exit style to close-bubble
         * */
        fun closeBubbleStyle(@StyleRes style: Int?): Builder {
            this.closeBubbleStyle = style
            return this
        }

        /**
         * add a listener, pass an instance of FloatingBubble.Action
         * @param FloatingBubble.Listener
         * */
        fun addFloatingBubbleListener(listener: Listener): Builder {

            val tempListener = this.listener
            this.listener = object : Listener {

                override fun onClick() {
                    tempListener?.onClick()
                    listener.onClick()
                }

                override fun onDown(x: Float, y: Float) {
                    tempListener?.onDown(x, y)
                    listener.onDown(x, y)
                }

                override fun onMove(x: Float, y: Float) {
                    tempListener?.onMove(x, y)
                    listener.onMove(x, y)
                }

                override fun onUp(x: Float, y: Float) {
                    tempListener?.onUp(x, y)
                    listener.onUp(x, y)
                }

                override fun onDestroy() {
                    tempListener?.onDestroy()
                    listener.onDestroy()
                }

            }
            return this
        }

        /**
         * examples: x=0, y=0 show bubble on the top-left corner of the screen.
         *
         * you can set x/y as negative value, but the bubble will be outside the screen.
         *
         * @param x 0 ... screenWidth (px).
         * @param y 0 ... screenHeight (px).
         * */
        fun startLocation(x: Int, y: Int): Builder {
            startPoint.x = x
            startPoint.y = y
            return this
        }

        // not exposed to the outside packages because of being developed
        internal fun elevation(dp: Int): Builder {
            elevation = dp
            return this
        }

        /**
         * - 0.0f: invisible
         * - 0.0f < x < 1.0f: view with opacity
         * - 1.0f: completely visible
         * */
        fun opacity(opacity: Float): Builder {
            this.opacity = opacity
            return this
        }

        internal fun build(): FloatingBubble {
            return FloatingBubble(this)
        }
    }

}