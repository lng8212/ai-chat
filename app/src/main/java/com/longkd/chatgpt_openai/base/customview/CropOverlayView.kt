package com.longkd.chatgpt_openai.base.customview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import com.longkd.chatgpt_openai.base.util.BitmapUtils
import java.util.*
import kotlin.math.*

class CropOverlayView
@JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var mScaleDetector: ScaleGestureDetector? = null

    /** Boolean to see if multi touch is enabled for the crop rectangle  */
    private var mMultiTouchEnabled = false

    /** Handler from crop window stuff, moving and knowing possition.  */
    private val mCropWindowHandler: CropWindowHandler = CropWindowHandler()

    /** Listener to publicj crop window changes  */
    private var mCropWindowChangeListener: CropWindowChangeListener? = null

    /** Rectangle used for drawing  */
    private val mDrawRect = RectF()

    /** The Paint used to draw the white rectangle around the crop area.  */
    private var mBorderPaint: Paint? = null

    /** The Paint used to draw the corners of the Border  */
    private var mBorderCornerPaint: Paint? = null

    /** The Paint used to darken the surrounding areas outside the crop area.  */
    private var mBackgroundPaint: Paint? = null

    /** Used for oval crop window shape or non-straight rotation drawing.  */
    private val mPath: Path = Path()

    /** The bounding box around the Bitmap that we are cropping.  */
    private val mBoundsPoints = FloatArray(8)

    /** The bounding box around the Bitmap that we are cropping.  */
    private val mCalcBounds = RectF()

    /** The bounding image view width used to know the crop overlay is at view edges.  */
    private var mViewWidth = 0

    /** The bounding image view height used to know the crop overlay is at view edges.  */
    private var mViewHeight = 0

    /** The offset to draw the border corener from the border  */
    private var mBorderCornerOffset = 0f

    /** the length of the border corner to draw  */
    private var mBorderCornerLength = 0f

    /** The initial crop window padding from image borders  */
    private var mInitialCropWindowPaddingRatio = 0f

    /** The radius of the touch zone (in pixels) around a given Handle.  */
    private var mTouchRadius = 0f


    private var mSnapRadius = 0f

    /** The Handle that is currently pressed; null if no Handle is pressed.  */
    private var mMoveHandler: CropWindowMoveHandler? = null
    /**
     * whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows it to
     * be changed.
     */
    /**
     * Flag indicating if the crop area should always be a certain aspect ratio (indicated by
     * mTargetAspectRatio).
     */
    var isFixAspectRatio = false
        private set

    /** save the current aspect ratio of the image  */
    private var mAspectRatioX = 0

    /** save the current aspect ratio of the image  */
    private var mAspectRatioY = 0

    /**
     * The aspect ratio that the crop area should maintain; this variable is only used when
     * mMaintainAspectRatio is true.
     */
    private var mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY

    /** The shape of the cropping area - rectangle/circular.  */
    private var mCropShape: CropImageView.CropShape? = null

    /** the initial crop window rectangle to set  */
    private val mInitialCropWindowRect: Rect = Rect()

    /** Whether the Crop View has been initialized for the first time  */
    private var initializedCropWindow = false

    /** Used to set back LayerType after changing to software.  */
    private var mOriginalLayerType: Int? = null

    /** Set the crop window change listener.  */
    fun setCropWindowChangeListener(listener: CropWindowChangeListener?) {
        mCropWindowChangeListener = listener
    }
    /** Get the left/top/right/bottom coordinates of the crop window.  */
    /** Set the left/top/right/bottom coordinates of the crop window.  */
    var cropWindowRect: RectF?
        get() = mCropWindowHandler.rect
        set(rect) {
            mCropWindowHandler.rect = rect
        }

    /** Fix the current crop window rectangle if it is outside of cropping image or view bounds.  */
    fun fixCurrentCropWindowRect() {
        val rect = cropWindowRect!!
        fixCropWindowRectByRules(rect)
        mCropWindowHandler.rect = rect
    }

    /**
     * Informs the CropOverlayView of the image's position relative to the ImageView. This is
     * necessary to call in order to draw the crop window.
     *
     * @param boundsPoints the image's bounding points
     * @param viewWidth The bounding image view width.
     * @param viewHeight The bounding image view height.
     */
    fun setBounds(boundsPoints: FloatArray?, viewWidth: Int, viewHeight: Int) {
        if (boundsPoints == null || !Arrays.equals(mBoundsPoints, boundsPoints)) {
            if (boundsPoints == null) {
                Arrays.fill(mBoundsPoints, 0f)
            } else {
                System.arraycopy(boundsPoints, 0, mBoundsPoints, 0, boundsPoints.size)
            }
            mViewWidth = viewWidth
            mViewHeight = viewHeight
            val cropRect: RectF? = mCropWindowHandler.rect
            if (cropRect?.width() == 0f || cropRect?.height() == 0f) {
                initCropWindow()
            }
        }
    }

    /** Resets the crop overlay view.  */
    fun resetCropOverlayView() {
        if (initializedCropWindow) {
            cropWindowRect = BitmapUtils.EMPTY_RECT_F
            initCropWindow()
            invalidate()
        }
    }
    /** The shape of the cropping area - rectangle/circular.  */
    /** The shape of the cropping area - rectangle/circular.  */
    var cropShape: CropImageView.CropShape?
        get() = mCropShape
        set(cropShape) {
            if (mCropShape !== cropShape) {
                mCropShape = cropShape
                invalidate()
            }
        }

    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows
     * it to be changed.
     */
    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        if (isFixAspectRatio != fixAspectRatio) {
            isFixAspectRatio = fixAspectRatio
            if (initializedCropWindow) {
                initCropWindow()
                invalidate()
            }
        }
    }
    /** the X value of the aspect ratio;  */
    /** Sets the X value of the aspect ratio; is defaulted to 1.  */
    var aspectRatioX: Int
        get() = mAspectRatioX
        set(aspectRatioX) {
            require(aspectRatioX > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
            if (mAspectRatioX != aspectRatioX) {
                mAspectRatioX = aspectRatioX
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
                if (initializedCropWindow) {
                    initCropWindow()
                    invalidate()
                }
            }
        }
    /** the Y value of the aspect ratio;  */
    /**
     * Sets the Y value of the aspect ratio; is defaulted to 1.
     *
     * @param aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    var aspectRatioY: Int
        get() = mAspectRatioY
        set(aspectRatioY) {
            require(aspectRatioY > 0) { "Cannot set aspect ratio value to a number less than or equal to 0." }
            if (mAspectRatioY != aspectRatioY) {
                mAspectRatioY = aspectRatioY
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY
                if (initializedCropWindow) {
                    initCropWindow()
                    invalidate()
                }
            }
        }

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (default: 3)
     */
    fun setSnapRadius(snapRadius: Float) {
        mSnapRadius = snapRadius
    }

    /** Set multi touch functionality to enabled/disabled.  */
    fun setMultiTouchEnabled(multiTouchEnabled: Boolean): Boolean {
        if (mMultiTouchEnabled != multiTouchEnabled) {
            mMultiTouchEnabled = multiTouchEnabled
            if (mMultiTouchEnabled && mScaleDetector == null) {
                mScaleDetector = ScaleGestureDetector(context, ScaleListener())
            }
            return true
        }
        return false
    }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mCropWindowHandler.setMinCropResultSize(minCropResultWidth, minCropResultHeight)
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mCropWindowHandler.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight)
    }

    /**
     * set the max width/height and scale factor of the shown image to original image to scale the
     * limits appropriately.
     */
    fun setCropWindowLimits(
        maxWidth: Float, maxHeight: Float, scaleFactorWidth: Float, scaleFactorHeight: Float
    ) {
        mCropWindowHandler.setCropWindowLimits(
            maxWidth, maxHeight, scaleFactorWidth, scaleFactorHeight
        )
    }
    /** Get crop window initial rectangle.  */
    /** Set crop window initial rectangle to be used instead of default.  */
    var initialCropWindowRect: Rect?
        get() = mInitialCropWindowRect
        set(rect) {
            mInitialCropWindowRect.set(rect ?: BitmapUtils.EMPTY_RECT)
            if (initializedCropWindow) {
                initCropWindow()
                invalidate()
                callOnCropWindowChanged(false)
            }
        }

    /**
     * Sets all initial values, but does not call initCropWindow to reset the views.<br></br>
     * Used once at the very start to initialize the attributes.
     */
    fun setInitialAttributeValues(options: CropImageOptions) {
        mCropWindowHandler.setInitialAttributeValues(options)
        cropShape = options.cropShape
        setSnapRadius(options.snapRadius)
        setFixedAspectRatio(options.fixAspectRatio)
        aspectRatioX = options.aspectRatioX
        aspectRatioY = options.aspectRatioY
        setMultiTouchEnabled(options.multiTouchEnabled)
        mTouchRadius = options.touchRadius
        mInitialCropWindowPaddingRatio = options.initialCropWindowPaddingRatio
        mBorderPaint = getNewPaintOrNull(options.borderLineThickness, options.borderLineColor)
        mBorderCornerOffset = options.borderCornerOffset
        mBorderCornerLength = options.borderCornerLength
        mBorderCornerPaint =
            getNewPaintOrNull(options.borderCornerThickness, options.borderCornerColor)
        mBackgroundPaint = getNewPaint(options.backgroundColor)
    }
    // region: Private methods
    /**
     * Set the initial crop window size and position. This is dependent on the size and position of
     * the image being cropped.
     */
    private fun initCropWindow() {
        val leftLimit =
            BitmapUtils.getRectLeft(mBoundsPoints).coerceAtLeast(0f)
        val topLimit = BitmapUtils.getRectTop(mBoundsPoints).coerceAtLeast(0f)
        val rightLimit =
            BitmapUtils.getRectRight(mBoundsPoints).coerceAtMost(width.toFloat())
        val bottomLimit =
            BitmapUtils.getRectBottom(mBoundsPoints).coerceAtMost(height.toFloat())
        if (rightLimit <= leftLimit || bottomLimit <= topLimit) {
            return
        }
        val rect = RectF()

        // Tells the attribute functions the crop window has already been initialized
        initializedCropWindow = true
        val horizontalPadding = mInitialCropWindowPaddingRatio * (rightLimit - leftLimit)
        val verticalPadding = mInitialCropWindowPaddingRatio * (bottomLimit - topLimit)
        if (mInitialCropWindowRect.width() > 0 && mInitialCropWindowRect.height() > 0) {
            // Get crop window position relative to the displayed image.
            rect.left =
                leftLimit + mInitialCropWindowRect.left / mCropWindowHandler.scaleFactorWidth
            rect.top =
                topLimit + mInitialCropWindowRect.top / mCropWindowHandler.scaleFactorHeight
            rect.right =
                rect.left + mInitialCropWindowRect.width() / mCropWindowHandler.scaleFactorWidth
            rect.bottom =
                rect.top + mInitialCropWindowRect.height() / mCropWindowHandler.scaleFactorHeight


            rect.left = leftLimit.coerceAtLeast(rect.left)
            rect.top = topLimit.coerceAtLeast(rect.top)
            rect.right = rightLimit.coerceAtMost(rect.right)
            rect.bottom = bottomLimit.coerceAtMost(rect.bottom)
        } else if (isFixAspectRatio && (rightLimit > leftLimit) && (bottomLimit > topLimit)) {

            // If the image aspect ratio is wider than the crop aspect ratio,
            // then the image height is the determining initial length. Else, vice-versa.
            val bitmapAspectRatio = (rightLimit - leftLimit) / (bottomLimit - topLimit)
            if (bitmapAspectRatio > mTargetAspectRatio) {
                rect.top = topLimit + verticalPadding
                rect.bottom = bottomLimit - verticalPadding
                val centerX = width / 2f

                // dirty fix for wrong crop overlay aspect ratio when using fixed aspect ratio
                mTargetAspectRatio = mAspectRatioX.toFloat() / mAspectRatioY

                // Limits the aspect ratio to no less than 40 wide or 40 tall
                val cropWidth = Math.max(
                    mCropWindowHandler.minCropWidth,
                    rect.height() * mTargetAspectRatio
                )
                val halfCropWidth = cropWidth / 2f
                rect.left = centerX - halfCropWidth
                rect.right = centerX + halfCropWidth
            } else {
                rect.left = leftLimit + horizontalPadding
                rect.right = rightLimit - horizontalPadding
                val centerY = height / 2f

                // Limits the aspect ratio to no less than 40 wide or 40 tall
                val cropHeight = Math.max(
                    mCropWindowHandler.minCropHeight,
                    rect.width() / mTargetAspectRatio
                )
                val halfCropHeight = cropHeight / 2f
                rect.top = centerY - halfCropHeight
                rect.bottom = centerY + halfCropHeight
            }
        } else {
            // Initialize crop window to have 10% padding w/ respect to image.
            rect.left = leftLimit + horizontalPadding
            rect.top = topLimit + verticalPadding
            rect.right = rightLimit - horizontalPadding
            rect.bottom = bottomLimit - verticalPadding
        }
        fixCropWindowRectByRules(rect)
        mCropWindowHandler.rect = rect
    }

    /** Fix the given rect to fit into bitmap rect and follow min, max and aspect ratio rules.  */
    private fun fixCropWindowRectByRules(rect: RectF) {
        if (rect.width() < mCropWindowHandler.minCropWidth) {
            val adj: Float = (mCropWindowHandler.minCropWidth - rect.width()) / 2
            rect.left -= adj
            rect.right += adj
        }
        if (rect.height() < mCropWindowHandler.minCropHeight) {
            val adj: Float = (mCropWindowHandler.minCropHeight - rect.height()) / 2
            rect.top -= adj
            rect.bottom += adj
        }
        if (rect.width() > mCropWindowHandler.maxCropWidth) {
            val adj: Float = (rect.width() - mCropWindowHandler.maxCropWidth) / 2
            rect.left += adj
            rect.right -= adj
        }
        if (rect.height() > mCropWindowHandler.maxCropHeight) {
            val adj: Float = (rect.height() - mCropWindowHandler.maxCropHeight) / 2
            rect.top += adj
            rect.bottom -= adj
        }
        calculateBounds(rect)
        if (mCalcBounds.width() > 0 && mCalcBounds.height() > 0) {
            val leftLimit = mCalcBounds.left.coerceAtLeast(0f)
            val topLimit = mCalcBounds.top.coerceAtLeast(0f)
            val rightLimit = mCalcBounds.right.coerceAtMost(width.toFloat())
            val bottomLimit = mCalcBounds.bottom.coerceAtMost(height.toFloat())
            if (rect.left < leftLimit) {
                rect.left = leftLimit
            }
            if (rect.top < topLimit) {
                rect.top = topLimit
            }
            if (rect.right > rightLimit) {
                rect.right = rightLimit
            }
            if (rect.bottom > bottomLimit) {
                rect.bottom = bottomLimit
            }
        }
        if (isFixAspectRatio && abs(rect.width() - rect.height() * mTargetAspectRatio) > 0.1) {
            if (rect.width() > rect.height() * mTargetAspectRatio) {
                val adj = abs(rect.height() * mTargetAspectRatio - rect.width()) / 2
                rect.left += adj
                rect.right -= adj
            } else {
                val adj = abs(rect.width() / mTargetAspectRatio - rect.height()) / 2
                rect.top += adj
                rect.bottom -= adj
            }
        }
    }

    /**
     * Draw crop overview by drawing background over image not in the cripping area, then borders and
     * guidelines.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw translucent background for the cropped area.
        drawBackground(canvas)
        drawBorders(canvas)
        drawCorners(canvas)
    }

    /** Draw shadow background over the image not including the crop area.  */
    private fun drawBackground(canvas: Canvas) {
        val rect: RectF? = mCropWindowHandler.rect
        val left = BitmapUtils.getRectLeft(mBoundsPoints).coerceAtLeast(0f)
        val top = BitmapUtils.getRectTop(mBoundsPoints).coerceAtLeast(0f)
        val right = BitmapUtils.getRectRight(mBoundsPoints).coerceAtMost(width.toFloat())
        val bottom = BitmapUtils.getRectBottom(mBoundsPoints).coerceAtMost(height.toFloat())
        if (!isNonStraightAngleRotated) {
            mBackgroundPaint?.let {
                canvas.drawRect(left, top, right, rect?.top ?: 0f, it)
                canvas.drawRect(left, rect?.bottom ?: 0f, right, bottom, it)
                canvas.drawRect(left, rect?.top ?: 0f, rect?.left ?: 0f, rect?.bottom ?: 0f, it)
                canvas.drawRect(rect?.right ?: 0f, rect?.top ?: 0f, right, rect?.bottom ?: 0f, it)
            }
        } else {
            mPath.reset()
            mPath.moveTo(mBoundsPoints[0], mBoundsPoints[1])
            mPath.lineTo(mBoundsPoints[2], mBoundsPoints[3])
            mPath.lineTo(mBoundsPoints[4], mBoundsPoints[5])
            mPath.lineTo(mBoundsPoints[6], mBoundsPoints[7])
            mPath.close()
            canvas.save()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(mPath)
            } else {
                canvas.clipPath(mPath, Region.Op.INTERSECT)
            }
            canvas.clipRect(rect ?: RectF(), Region.Op.XOR)
            mBackgroundPaint?.let { canvas.drawRect(left, top, right, bottom, it) }
            canvas.restore()
        }

    }

    /** Draw borders of the crop area.  */
    private fun drawBorders(canvas: Canvas) {
        mBorderPaint?.let {
            val w: Float = it.strokeWidth
            val rect: RectF = mCropWindowHandler.rect ?: RectF()
            rect.inset(w / 2, w / 2)
            canvas.drawRect(rect, it)
        }
    }

    /** Draw the corner of crop overlay.  */
    private fun drawCorners(canvas: Canvas) {
        mBorderCornerPaint?.let {
            val lineWidth: Float = mBorderPaint?.strokeWidth ?: 0f
            val cornerWidth: Float = it.strokeWidth

            // for rectangle crop shape we allow the corners to be offset from the borders
            val w: Float = (cornerWidth / 2f).plus(mBorderCornerOffset)
            val rect: RectF = mCropWindowHandler.rect ?: RectF()
            rect.inset(w, w)
            val cornerOffset = (cornerWidth + lineWidth * 2)
            val cornerExtension = cornerWidth / 2 + cornerOffset

            // Top left
            canvas.drawLine(rect.left - cornerOffset, rect.top - cornerExtension, rect.left - cornerOffset, rect.top + mBorderCornerLength, it)
            canvas.drawLine(rect.left - cornerExtension, rect.top - cornerOffset, rect.left + mBorderCornerLength, rect.top - cornerOffset, it)

            // Top right
            canvas.drawLine(rect.right + cornerOffset, rect.top - cornerExtension, rect.right + cornerOffset, rect.top + mBorderCornerLength, it)
            canvas.drawLine(rect.right + cornerExtension, rect.top - cornerOffset, rect.right - mBorderCornerLength, rect.top - cornerOffset, it)

            // Bottom left
            canvas.drawLine(rect.left - cornerOffset, rect.bottom + cornerExtension, rect.left - cornerOffset, rect.bottom - mBorderCornerLength, it)
            canvas.drawLine(rect.left - cornerExtension, rect.bottom + cornerOffset, rect.left + mBorderCornerLength, rect.bottom + cornerOffset, it)

            // Bottom right
            canvas.drawLine(rect.right + cornerOffset, rect.bottom + cornerExtension, rect.right + cornerOffset, rect.bottom - mBorderCornerLength, it)
            canvas.drawLine(rect.right + cornerExtension, rect.bottom + cornerOffset, rect.right - mBorderCornerLength, rect.bottom + cornerOffset, it)

            canvas.drawLine(rect.left - cornerOffset, rect.top.plus(rect.bottom) / 2 - mBorderCornerLength.plus(cornerExtension) / 2, rect.left - cornerOffset, rect.top.plus(rect.bottom) / 2 + mBorderCornerLength.plus(cornerExtension) /2, it)
            canvas.drawLine(rect.left.plus(rect.right) / 2 - mBorderCornerLength.plus(cornerExtension) / 2, rect.top - cornerOffset, rect.left.plus(rect.right) / 2 + mBorderCornerLength.plus(cornerExtension) / 2, rect.top - cornerOffset, it)

            canvas.drawLine(rect.right + cornerOffset, rect.top.plus(rect.bottom) / 2 - mBorderCornerLength.plus(cornerExtension) / 2, rect.right + cornerOffset, rect.top.plus(rect.bottom) / 2 + mBorderCornerLength.plus(cornerExtension) /2, it)
            canvas.drawLine(rect.left.plus(rect.right) / 2 - mBorderCornerLength.plus(cornerExtension) / 2, rect.bottom + cornerOffset, rect.left.plus(rect.right) / 2 + mBorderCornerLength.plus(cornerExtension) / 2, rect.bottom + cornerOffset, it)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // If this View is not enabled, don't allow for touch interactions.
        return if (isEnabled) {
            if (mMultiTouchEnabled) {
                mScaleDetector!!.onTouchEvent(event)
            }
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onActionDown(event.x, event.y)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    onActionUp()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    onActionMove(event.x, event.y)
                    parent.requestDisallowInterceptTouchEvent(true)
                    true
                }
                else -> false
            }
        } else {
            false
        }
    }

    /**
     * On press down start crop window movment depending on the location of the press.<br></br>
     * if press is far from crop window then no move handler is returned (null).
     */
    private fun onActionDown(x: Float, y: Float) {
        mMoveHandler = mCropWindowHandler.getMoveHandler(x, y, mTouchRadius)
        if (mMoveHandler != null) {
            invalidate()
        }
    }

    /** Clear move handler starting in [.onActionDown] if exists.  */
    private fun onActionUp() {
        if (mMoveHandler != null) {
            mMoveHandler = null
            callOnCropWindowChanged(false)
            invalidate()
        }
    }

    /**
     * Handle move of crop window using the move handler created in [.onActionDown].<br></br>
     * The move handler will do the proper move/resize of the crop window.
     */
    private fun onActionMove(x: Float, y: Float) {
        mMoveHandler?.let {
            var snapRadius = mSnapRadius
            val rect: RectF = mCropWindowHandler.rect ?: RectF()
            if (calculateBounds(rect)) {
                snapRadius = 0f
            }
            it.move(rect, x, y, mCalcBounds, mViewWidth, mViewHeight, snapRadius, isFixAspectRatio, mTargetAspectRatio)
            mCropWindowHandler.rect = rect
            callOnCropWindowChanged(true)
            invalidate()
        }
    }

    private fun calculateBounds(rect: RectF): Boolean {
        var left: Float = BitmapUtils.getRectLeft(mBoundsPoints)
        var top: Float = BitmapUtils.getRectTop(mBoundsPoints)
        var right: Float = BitmapUtils.getRectRight(mBoundsPoints)
        var bottom: Float = BitmapUtils.getRectBottom(mBoundsPoints)
        return if (!isNonStraightAngleRotated) {
            mCalcBounds[left, top, right] = bottom
            false
        } else {
            var x0 = mBoundsPoints[0]
            var y0 = mBoundsPoints[1]
            var x2 = mBoundsPoints[4]
            var y2 = mBoundsPoints[5]
            var x3 = mBoundsPoints[6]
            var y3 = mBoundsPoints[7]
            if (mBoundsPoints[7] < mBoundsPoints[1]) {
                if (mBoundsPoints[1] < mBoundsPoints[3]) {
                    x0 = mBoundsPoints[6]
                    y0 = mBoundsPoints[7]
                    x2 = mBoundsPoints[2]
                    y2 = mBoundsPoints[3]
                    x3 = mBoundsPoints[4]
                    y3 = mBoundsPoints[5]
                } else {
                    x0 = mBoundsPoints[4]
                    y0 = mBoundsPoints[5]
                    x2 = mBoundsPoints[0]
                    y2 = mBoundsPoints[1]
                    x3 = mBoundsPoints[2]
                    y3 = mBoundsPoints[3]
                }
            } else if (mBoundsPoints[1] > mBoundsPoints[3]) {
                x0 = mBoundsPoints[2]
                y0 = mBoundsPoints[3]
                x2 = mBoundsPoints[6]
                y2 = mBoundsPoints[7]
                x3 = mBoundsPoints[0]
                y3 = mBoundsPoints[1]
            }
            val a0 = (y3 - y0) / (x3 - x0)
            val a1 = -1f / a0
            val b0 = y0 - a0 * x0
            val b1 = y0 - a1 * x0
            val b2 = y2 - a0 * x2
            val b3 = y2 - a1 * x2
            val c0 = (rect.centerY() - rect.top) / (rect.centerX() - rect.left)
            val c1 = -c0
            val d0 = rect.top - c0 * rect.left
            val d1 = rect.top - c1 * rect.right
            left = left.coerceAtLeast(if ((d0 - b0) / (a0 - c0) < rect.right) (d0 - b0) / (a0 - c0) else left)
            left =
                left.coerceAtLeast(if ((d0 - b1) / (a1 - c0) < rect.right) (d0 - b1) / (a1 - c0) else left)
            left =
                left.coerceAtLeast(if ((d1 - b3) / (a1 - c1) < rect.right) (d1 - b3) / (a1 - c1) else left)
            right =
                right.coerceAtMost(if ((d1 - b1) / (a1 - c1) > rect.left) (d1 - b1) / (a1 - c1) else right)
            right =
                right.coerceAtMost(if ((d1 - b2) / (a0 - c1) > rect.left) (d1 - b2) / (a0 - c1) else right)
            right =
                right.coerceAtMost(if ((d0 - b2) / (a0 - c0) > rect.left) (d0 - b2) / (a0 - c0) else right)
            top = top.coerceAtLeast((a0 * left + b0).coerceAtLeast(a1 * right + b1))
            bottom = bottom.coerceAtMost((a1 * left + b3).coerceAtMost(a0 * right + b2))
            mCalcBounds.left = left
            mCalcBounds.top = top
            mCalcBounds.right = right
            mCalcBounds.bottom = bottom
            true
        }
    }

    /** Is the cropping image has been rotated by NOT 0,90,180 or 270 degrees.  */
    private val isNonStraightAngleRotated: Boolean
        private get() = mBoundsPoints[0] != mBoundsPoints[6] && mBoundsPoints[1] != mBoundsPoints[7]

    /** Invoke on crop change listener safe, don't let the app crash on exception.  */
    private fun callOnCropWindowChanged(inProgress: Boolean) {
        try {
            if (mCropWindowChangeListener != null) {
                mCropWindowChangeListener!!.onCropWindowChanged(inProgress)
            }
        } catch (e: Exception) { }
    }

    /** Interface definition for a callback to be invoked when crop window rectangle is changing.  */
    interface CropWindowChangeListener {
        fun onCropWindowChanged(inProgress: Boolean)
    }

    /** Handle scaling the rectangle based on two finger input  */
    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val rect: RectF = mCropWindowHandler.rect ?: RectF()
            val x = detector.focusX
            val y = detector.focusY
            val dY = detector.currentSpanY / 2
            val dX = detector.currentSpanX / 2
            val newTop = y - dY
            val newLeft = x - dX
            val newRight = x + dX
            val newBottom = y + dY
            if (newLeft < newRight && newTop <= newBottom && newLeft >= 0 && newRight <= mCropWindowHandler.maxCropWidth && newTop >= 0 && newBottom <= mCropWindowHandler.maxCropHeight) {
                rect[newLeft, newTop, newRight] = newBottom
                mCropWindowHandler.rect = rect
                invalidate()
            }
            return true
        }
    }

    companion object {
        /** Creates the Paint object for drawing.  */
        private fun getNewPaint(color: Int): Paint {
            val paint = Paint()
            paint.color = color
            return paint
        }

        /** Creates the Paint object for given thickness and color, if thickness < 0 return null.  */
        private fun getNewPaintOrNull(thickness: Float, color: Int): Paint? {
            return if (thickness > 0) {
                val borderPaint = Paint()
                borderPaint.color = color
                borderPaint.strokeWidth = thickness
                borderPaint.style = Paint.Style.STROKE
                borderPaint.isAntiAlias = true
                borderPaint
            } else null
        }
    }
}