package com.longkd.chatgpt_openai.base.bubble

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.PointF
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.databinding.BubbleBinding

internal class FloatingBubbleView(
    private val builder: FloatingBubble.Builder,
) : BaseFloatingViewBinding<BubbleBinding>(
    context = builder.context,
    initializer = BubbleBinding.inflate(LayoutInflater.from(builder.context))
) {

    /**
     * store previous point for later usage, reset after finger down
     * */
     val prevPoint = Point(0, 0)
    private val rawPointOnDown = PointF(0f, 0f)
    private val newPoint = Point(0, 0)

    private val halfScreenWidth = ScreenInfo.widthPx / 2
    private val halfScreenHeight = ScreenInfo.heightPx / 2

    private val halfIconWidthPx: Int
    private val halfIconHeightPx: Int

    init {

        builder.bubbleSizePx.also {
            if (it.notZero()) {
                width = it.width
                height = it.height
            }
        }

        halfIconWidthPx = width / 2
        halfIconHeightPx = height /2

        setupLayoutParams()
        setupBubbleProperties()
        customTouch()
    }


    private var isAnimatingToEdge = false
    fun animateIconToEdge(onFinished: (() -> Unit)? = null) {
        if (isAnimatingToEdge) return

        isAnimatingToEdge = true
        val iconX = binding.root.getXYPointOnScreen().x // 0..X

        val isOnTheLeftSide = iconX + halfIconWidthPx < halfScreenWidth
        val startX: Int
        val endX: Int
        if (isOnTheLeftSide) {
            startX = iconX
            endX = 0
        } else {
            startX = iconX
            endX = ScreenInfo.widthPx - width
        }
        AnimHelper.startSpringX(
            startValue = startX.toFloat(),
            finalPosition = endX.toFloat(),
            event = object : AnimHelper.Event {
                override fun onUpdate(float: Float) {
                    try {
                        windowParams.x = float.toInt()
                        update()
                    } catch (_: Exception) {
                    }
                }

                override fun onEnd() {
                    isAnimatingToEdge = false
                    onFinished?.invoke()
                }
            }
        )
    }

    // private func --------------------------------------------------------------------------------
    fun setImageBubble(@DrawableRes drawable: Int){
        binding.bubbleView.setImageResource(drawable)

    }
    // private func --------------------------------------------------------------------------------
    private fun setupBubbleProperties() {
        val iconBitmap = builder.iconBitmap ?: R.drawable.ic_rounded_blue_diamond.toBitmap(
            builder.context
        )
        binding.bubbleView.apply {
            setImageBitmap(iconBitmap)
            layoutParams.width = this@FloatingBubbleView.width
            layoutParams.height = this@FloatingBubbleView.height

            alpha = builder.opacity
        }

        windowParams.apply {
            x = ScreenInfo.widthPx - this@FloatingBubbleView.width
            y = ScreenInfo.heightPx*2/5
        }

    }

    fun updateLocation(x: Float, y: Float) {
        val mIconDeltaX = x - rawPointOnDown.x
        val mIconDeltaY = y - rawPointOnDown.y

        newPoint.x = prevPoint.x + mIconDeltaX.toInt()
        newPoint.y = prevPoint.y + mIconDeltaY.toInt()

        //region prevent bubble Y point move outside the screen
        val safeTopY = 0
        val safeBottomY =
            ScreenInfo.heightPx - ScreenInfo.softNavBarHeightPx - ScreenInfo.statusBarHeightPx - height
        val isAboveStatusBar = newPoint.y < safeTopY
        val isUnderSoftNavBar = newPoint.y > safeBottomY
        if (isAboveStatusBar) {
            newPoint.y = safeTopY
        } else if (isUnderSoftNavBar) {
            newPoint.y = safeBottomY
        }
        //endregion

        windowParams.x = newPoint.x
        windowParams.y = newPoint.y
        update()
    }

    /**
     * set location without updating UI
     * */
    fun setLocation(x: Float, y: Float){
        newPoint.x = x.toInt()
        newPoint.y = y.toInt()
    }

    fun rawLocationOnScreen(): Pair<Float, Float>{
        return Pair(newPoint.x.toFloat(), newPoint.y.toFloat())
    }

    /**
     * pass close bubble point
     * */
    fun animateTo(x: Float, y: Float) {
        AnimHelper.animateSpringPath(
            startX = newPoint.x.toFloat(),
            startY = newPoint.y.toFloat(),
            endX = x,
            endY = y,
            event = object : AnimHelper.Event {
                override fun onUpdatePoint(x: Float, y: Float) {

                    windowParams.x = x.toInt()
                    windowParams.y = y.toInt()

//                    builder.listener?.onMove(x.toFloat(), y.toFloat()) // don't call this line, it'll spam multiple MotionEvent.OnActionMove
                    update()

                }
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun customTouch() {
        fun onActionDown(motionEvent: MotionEvent) {
            prevPoint.x = windowParams.x
            prevPoint.y = windowParams.y

            rawPointOnDown.x = motionEvent.rawX
            rawPointOnDown.y = motionEvent.rawY

            builder.listener?.onDown(motionEvent.rawX, motionEvent.rawY)
        }

        fun onActionMove(motionEvent: MotionEvent){
            builder.listener?.onMove(motionEvent.rawX, motionEvent.rawY)
        }

        fun onActionUp(motionEvent: MotionEvent) {
            builder.listener?.onUp(motionEvent.rawX, motionEvent.rawY)
        }

        // listen actions --------------------------------------------------------------------------

        val gestureDetector = GestureDetector(builder.context, SingleTapConfirm())

        binding.bubbleView.apply {

            afterMeasured { updateGestureExclusion(builder.context) }

            setOnTouchListener { _, motionEvent ->

                // detect onTouch event first. If event is consumed, return@setOnTouch...
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    builder.listener?.onClick()
                    return@setOnTouchListener true
                }

                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> onActionDown(motionEvent)
                    MotionEvent.ACTION_MOVE -> onActionMove(motionEvent)
                    MotionEvent.ACTION_UP -> onActionUp(motionEvent)
                }

                return@setOnTouchListener true
            }
        }
    }

    private class SingleTapConfirm : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    // override

    override fun setupLayoutParams() {
        super.setupLayoutParams()

        windowParams.apply {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            builder.bubbleStyle?.let {
                windowAnimations = it
            }

        }


    }
}