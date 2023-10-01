package com.longkd.chatgpt_openai.base.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.exifinterface.media.ExifInterface
import com.longkd.chatgpt_openai.base.customview.CropOverlayView.CropWindowChangeListener
import com.longkd.chatgpt_openai.base.util.BitmapUtils
import com.longkd.chatgpt_openai.base.util.orZero
import com.longkd.chatgpt_openai.R
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt


class CropImageView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    /** Image view widget used to show the image for cropping.  */
    private val mImageView: ImageView

    /** Overlay over the image view to show cropping UI.  */
    private val mCropOverlayView: CropOverlayView?

    /** The matrix used to transform the cropping image in the image view  */
    private val mImageMatrix: Matrix = Matrix()

    /** Reusing matrix instance for reverse matrix calculations.  */
    private val mImageInverseMatrix: Matrix = Matrix()

    /** Progress bar widget to show progress bar on async image loading and cropping.  */
    private val mProgressBar: ProgressBar

    /** Rectangle used in image matrix transformation calculation (reusing rect instance)  */
    private val mImagePoints = FloatArray(8)

    /** Rectangle used in image matrix transformation for scale calculation (reusing rect instance)  */
    private val mScaleImagePoints = FloatArray(8)

    private var mBitmap: Bitmap? = null

    /** The image rotation value used during loading of the image so we can reset to it  */
    private var mInitialDegreesRotated = 0

    /** How much the image is rotated from original clockwise  */
    private var mDegreesRotated = 0

    /** if the image flipped horizontally  */
    private var mFlipHorizontally: Boolean

    /** if the image flipped vertically  */
    private var mFlipVertically: Boolean
    private var mLayoutWidth = 0
    private var mLayoutHeight = 0
    private var mImageResource = 0

    /** The initial scale type of the image in the crop image view  */
    private var mScaleType: ScaleType

    var isSaveBitmapToInstanceState = false
    private var mShowCropOverlay = true
    private var mShowProgressBar = false
    private var mAutoZoomEnabled = true

    /** The max zoom allowed during cropping  */
    private var mMaxZoom: Int

    /** callback to be invoked when crop overlay is released.  */
    private var mOnCropOverlayReleasedListener: OnSetCropOverlayReleasedListener? = null

    /** callback to be invoked when crop overlay is moved.  */
    private var mOnSetCropOverlayMovedListener: OnSetCropOverlayMovedListener? = null

    /** callback to be invoked when crop window is changed.  */
    private var mOnSetCropWindowChangeListener: OnSetCropWindowChangeListener? = null

    /** callback to be invoked when image async loading is complete.  */
    private var mOnSetImageUriCompleteListener: OnSetImageUriCompleteListener? = null

    /** callback to be invoked when image async cropping is complete.  */
    private var mOnCropImageCompleteListener: OnCropImageCompleteListener? = null

    /** The URI that the image was loaded from (if loaded from URI)  */
    private var mLoadedImageUri: Uri? = null

    /** The sample size the image was loaded by if was loaded by URI  */
    private var mLoadedSampleSize = 1

    /** The current zoom level to to scale the cropping image  */
    private var mZoom = 1f

    /** The X offset that the cropping image was translated after zooming  */
    private var mZoomOffsetX = 0f

    /** The Y offset that the cropping image was translated after zooming  */
    private var mZoomOffsetY = 0f

    /** Used to restore the cropping windows rectangle after state restore  */
    private var mRestoreCropWindowRect: RectF? = null

    /** Used to restore image rotation after state restore  */
    private var mRestoreDegreesRotated = 0

    /**
     * Used to detect size change to handle auto-zoom using [.handleCropWindowChanged] in [.layout].
     */
    private var mSizeChanged = false

    /**
     * Temp URI used to save bitmap image to disk to preserve for instance state in case cropped was
     * set with bitmap
     */
    private var mSaveInstanceStateBitmapUri: Uri? = null

    /** Task used to load bitmap async from UI thread  */
    private var mBitmapLoadingWorkerTask: WeakReference<BitmapLoadingWorkerTask>? = null

    /** Task used to crop bitmap async from UI thread  */
    private var mBitmapCroppingWorkerTask: WeakReference<BitmapCroppingWorkerTask>? = null

    // endregion
    constructor(context: Context) : this(context, null) {}

    init {
        var options: CropImageOptions? = null
        if (options == null) {
            options = CropImageOptions()
            if (attrs != null) {
                val ta: TypedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0)
                try {
                    options.fixAspectRatio = ta.getBoolean(
                        R.styleable.CropImageView_cropFixAspectRatio,
                        options.fixAspectRatio
                    )
                    options.aspectRatioX = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioX,
                        options.aspectRatioX
                    )
                    options.aspectRatioY = ta.getInteger(
                        R.styleable.CropImageView_cropAspectRatioY,
                        options.aspectRatioY
                    )
                    options.scaleType = ScaleType.values()[ta.getInt(
                        R.styleable.CropImageView_cropScaleType,
                        options.scaleType.ordinal
                    )]
                    options.autoZoomEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropAutoZoomEnabled,
                        options.autoZoomEnabled
                    )
                    options.multiTouchEnabled = ta.getBoolean(
                        R.styleable.CropImageView_cropMultiTouchEnabled, options.multiTouchEnabled
                    )
                    options.maxZoom =
                        ta.getInteger(R.styleable.CropImageView_cropMaxZoom, options.maxZoom)
                    options.cropShape = CropShape.values()[ta.getInt(
                        R.styleable.CropImageView_cropShape,
                        options.cropShape.ordinal
                    )]
                    options.snapRadius = ta.getDimension(
                        R.styleable.CropImageView_cropSnapRadius,
                        options.snapRadius
                    )
                    options.touchRadius = ta.getDimension(
                        R.styleable.CropImageView_cropTouchRadius,
                        options.touchRadius
                    )
                    options.initialCropWindowPaddingRatio = ta.getFloat(
                        R.styleable.CropImageView_cropInitialCropWindowPaddingRatio,
                        options.initialCropWindowPaddingRatio
                    )
                    options.borderLineThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderLineThickness,
                        options.borderLineThickness
                    )
                    options.borderLineColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderLineColor,
                        options.borderLineColor
                    )
                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.borderCornerOffset = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerOffset, options.borderCornerOffset
                    )
                    options.borderCornerLength = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerLength, options.borderCornerLength
                    )
                    options.borderCornerColor = ta.getInteger(
                        R.styleable.CropImageView_cropBorderCornerColor, options.borderCornerColor
                    )
                    options.backgroundColor = ta.getInteger(
                        R.styleable.CropImageView_cropBackgroundColor,
                        options.backgroundColor
                    )
                    options.showCropOverlay = ta.getBoolean(
                        R.styleable.CropImageView_cropShowCropOverlay,
                        mShowCropOverlay
                    )
                    options.showProgressBar = ta.getBoolean(
                        R.styleable.CropImageView_cropShowProgressBar,
                        mShowProgressBar
                    )
                    options.borderCornerThickness = ta.getDimension(
                        R.styleable.CropImageView_cropBorderCornerThickness,
                        options.borderCornerThickness
                    )
                    options.minCropWindowWidth = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowWidth, options.minCropWindowWidth.toFloat()
                    ).toInt()
                    options.minCropWindowHeight = ta.getDimension(
                        R.styleable.CropImageView_cropMinCropWindowHeight,
                        options.minCropWindowHeight.toFloat()
                    ).toInt()
                    options.minCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultWidthPX,
                        options.minCropResultWidth.toFloat()
                    ).toInt()
                    options.minCropResultHeight = ta.getFloat(
                        R.styleable.CropImageView_cropMinCropResultHeightPX,
                        options.minCropResultHeight.toFloat()
                    ).toInt()
                    options.maxCropResultWidth = ta.getFloat(
                        R.styleable.CropImageView_cropMaxCropResultWidthPX,
                        options.maxCropResultWidth.toFloat()
                    ).toInt()
                    options.maxCropResultHeight = ta.getFloat(R.styleable.CropImageView_cropMaxCropResultHeightPX, options.maxCropResultHeight.toFloat()).toInt()
                    options.flipHorizontally = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally, options.flipHorizontally
                    )
                    options.flipVertically = ta.getBoolean(
                        R.styleable.CropImageView_cropFlipHorizontally,
                        options.flipVertically
                    )
                    isSaveBitmapToInstanceState = ta.getBoolean(
                        R.styleable.CropImageView_cropSaveBitmapToInstanceState,
                        isSaveBitmapToInstanceState
                    )

                    // if aspect ratio is set then set fixed to true
                    if (ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
                        && ta.hasValue(R.styleable.CropImageView_cropAspectRatioX)
                        && !ta.hasValue(R.styleable.CropImageView_cropFixAspectRatio)
                    ) {
                        options.fixAspectRatio = true
                    }
                } finally {
                    ta.recycle()
                }
            }
        }
        options.validate()
        mScaleType = options.scaleType
        mAutoZoomEnabled = options.autoZoomEnabled
        mMaxZoom = options.maxZoom
        mShowCropOverlay = options.showCropOverlay
        mShowProgressBar = options.showProgressBar
        mFlipHorizontally = options.flipHorizontally
        mFlipVertically = options.flipVertically
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val v: View = inflater.inflate(R.layout.layout_crop_image_view, this, true)
        mImageView = v.findViewById(R.id.ImageView_image)
        mImageView.scaleType = ImageView.ScaleType.MATRIX
        mCropOverlayView = v.findViewById(R.id.CropOverlayView)
        mCropOverlayView.setCropWindowChangeListener(
            object : CropWindowChangeListener {
                override fun onCropWindowChanged(inProgress: Boolean) {
                    handleCropWindowChanged(inProgress, true)
                    val listener = mOnCropOverlayReleasedListener
                    if (listener != null && !inProgress) {
                        listener.onCropOverlayReleased(cropRect)
                    }
                    val movedListener = mOnSetCropOverlayMovedListener
                    if (movedListener != null && inProgress) {
                        movedListener.onCropOverlayMoved(cropRect)
                    }
                }
            })
        mCropOverlayView.setInitialAttributeValues(options)
        mProgressBar = v.findViewById(R.id.CropProgressBar)
        setProgressBarVisibility()
    }
    /** Get the scale type of the image in the crop view.  */
    /** Set the scale type of the image in the crop view  */
    var scaleType: ScaleType
        get() = mScaleType
        set(scaleType) {
            if (scaleType != mScaleType) {
                mScaleType = scaleType
                mZoom = 1f
                mZoomOffsetY = 0f
                mZoomOffsetX = mZoomOffsetY
                mCropOverlayView!!.resetCropOverlayView()
                requestLayout()
            }
        }
    /** The shape of the cropping area - rectangle/circular.  */
    /**
     * The shape of the cropping area - rectangle/circular.<br></br>
     * To set square/circle crop shape set aspect ratio to 1:1.
     */
    var cropShape: CropShape?
        get() = mCropOverlayView!!.cropShape
        set(cropShape) {
            mCropOverlayView!!.cropShape = cropShape
        }
    /** if auto-zoom functionality is enabled. default: true.  */
    /** Set auto-zoom functionality to enabled/disabled.  */
    var isAutoZoomEnabled: Boolean
        get() = mAutoZoomEnabled
        set(autoZoomEnabled) {
            if (mAutoZoomEnabled != autoZoomEnabled) {
                mAutoZoomEnabled = autoZoomEnabled
                handleCropWindowChanged(false, false)
                mCropOverlayView!!.invalidate()
            }
        }

    /** Set multi touch functionality to enabled/disabled.  */
    fun setMultiTouchEnabled(multiTouchEnabled: Boolean) {
        if (mCropOverlayView!!.setMultiTouchEnabled(multiTouchEnabled)) {
            handleCropWindowChanged(false, false)
            mCropOverlayView.invalidate()
        }
    }
    /** The max zoom allowed during cropping.  */
    /** The max zoom allowed during cropping.  */
    var maxZoom: Int
        get() = mMaxZoom
        set(maxZoom) {
            if (mMaxZoom != maxZoom && maxZoom > 0) {
                mMaxZoom = maxZoom
                handleCropWindowChanged(false, false)
                mCropOverlayView!!.invalidate()
            }
        }

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mCropOverlayView!!.setMinCropResultSize(minCropResultWidth, minCropResultHeight)
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mCropOverlayView!!.setMaxCropResultSize(maxCropResultWidth, maxCropResultHeight)
    }
    /**
     * Get the amount of degrees the cropping image is rotated cloackwise.<br></br>
     *
     * @return 0-360
     */
    /**
     * Set the amount of degrees the cropping image is rotated cloackwise.<br></br>
     *
     * @param degrees 0-360
     */
    var rotatedDegrees: Int
        get() = mDegreesRotated
        set(degrees) {
            if (mDegreesRotated != degrees) {
                rotateImage(degrees - mDegreesRotated)
            }
        }

    /**
     * whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows it to
     * be changed.
     */
    val isFixAspectRatio: Boolean
        get() = mCropOverlayView!!.isFixAspectRatio

    /**
     * Sets whether the aspect ratio is fixed or not; true fixes the aspect ratio, while false allows
     * it to be changed.
     */
    fun setFixedAspectRatio(fixAspectRatio: Boolean) {
        mCropOverlayView!!.setFixedAspectRatio(fixAspectRatio)
    }
    /** whether the image should be flipped horizontally  */
    /** Sets whether the image should be flipped horizontally  */
    var isFlippedHorizontally: Boolean
        get() = mFlipHorizontally
        set(flipHorizontally) {
            if (mFlipHorizontally != flipHorizontally) {
                mFlipHorizontally = flipHorizontally
                applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
            }
        }
    /** whether the image should be flipped vertically  */
    /** Sets whether the image should be flipped vertically  */
    var isFlippedVertically: Boolean
        get() = mFlipVertically
        set(flipVertically) {
            if (mFlipVertically != flipVertically) {
                mFlipVertically = flipVertically
                applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
            }
        }

    /** both the X and Y values of the aspectRatio.  */
    val aspectRatio: Pair<Int, Int>
        get() = Pair(mCropOverlayView!!.aspectRatioX, mCropOverlayView.aspectRatioY)

    /**
     * Sets the both the X and Y values of the aspectRatio.<br></br>
     * Sets fixed aspect ratio to TRUE.
     *
     * @param aspectRatioX int that specifies the new X value of the aspect ratio
     * @param aspectRatioY int that specifies the new Y value of the aspect ratio
     */
    fun setAspectRatio(aspectRatioX: Int, aspectRatioY: Int) {
        mCropOverlayView!!.aspectRatioX = aspectRatioX
        mCropOverlayView.aspectRatioY = aspectRatioY
        setFixedAspectRatio(true)
    }

    /** Clears set aspect ratio values and sets fixed aspect ratio to FALSE.  */
    fun clearAspectRatio() {
        mCropOverlayView!!.aspectRatioX = 1
        mCropOverlayView.aspectRatioY = 1
        setFixedAspectRatio(false)
    }

    /**
     * An edge of the crop window will snap to the corresponding edge of a specified bounding box when
     * the crop window edge is less than or equal to this distance (in pixels) away from the bounding
     * box edge. (default: 3dp)
     */
    fun setSnapRadius(snapRadius: Float) {
        if (snapRadius >= 0) {
            mCropOverlayView!!.setSnapRadius(snapRadius)
        }
    }
    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    /**
     * if to show progress bar when image async loading/cropping is in progress.<br></br>
     * default: true, disable to provide custom progress bar UI.
     */
    var isShowProgressBar: Boolean
        get() = mShowProgressBar
        set(showProgressBar) {
            if (mShowProgressBar != showProgressBar) {
                mShowProgressBar = showProgressBar
                setProgressBarVisibility()
            }
        }

    var isShowCropOverlay: Boolean
        get() = mShowCropOverlay
        set(showCropOverlay) {
            if (mShowCropOverlay != showCropOverlay) {
                mShowCropOverlay = showCropOverlay
                setCropOverlayVisibility()
            }
        }

    var imageResource: Int
        get() = mImageResource
        set(resId) {
            if (resId != 0) {
                mCropOverlayView!!.initialCropWindowRect = Rect()
                val bitmap: Bitmap = BitmapFactory.decodeResource(resources, resId)
                setBitmap(bitmap, resId, null, 1, 0)
            }
        }

    /** Get the URI of an image that was set by URI, null otherwise.  */
    val imageUri: Uri?
        get() = mLoadedImageUri

    /**
     * Gets the source Bitmap's dimensions. This represents the largest possible crop rectangle.
     *
     * @return a Rect instance dimensions of the source Bitmap
     */
    val wholeImageRect: Rect?
        get() {
            val loadedSampleSize = mLoadedSampleSize
            val bitmap = mBitmap ?: return null
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize
            return Rect(0, 0, orgWidth, orgHeight)
        }// get the points of the crop rectangle adjusted to source bitmap

    var cropRect: Rect?
        get() {
            val loadedSampleSize = mLoadedSampleSize
            val bitmap = mBitmap ?: return null

            // get the points of the crop rectangle adjusted to source bitmap
            val points = cropPoints
            val orgWidth = bitmap.width * loadedSampleSize
            val orgHeight = bitmap.height * loadedSampleSize

            // get the rectangle for the points (it may be larger than original if rotation is not stright)
            return BitmapUtils.getRectFromPoints(
                points,
                orgWidth,
                orgHeight,
                mCropOverlayView!!.isFixAspectRatio,
                mCropOverlayView.aspectRatioX,
                mCropOverlayView.aspectRatioY
            )
        }
        set(rect) {
            mCropOverlayView!!.initialCropWindowRect = rect ?: Rect()
        }

    /**
     * Gets the crop window's position relative to the parent's view at screen.
     *
     * @return a Rect instance containing cropped area boundaries of the source Bitmap
     */
    val cropWindowRect: RectF?
        get() {
            return if (mCropOverlayView == null) {
                null
            } else mCropOverlayView.cropWindowRect
        }// Get crop window position relative to the displayed image.

    /**
     * Gets the 4 points of crop window's position relative to the source Bitmap (not the image
     * displayed in the CropImageView) using the original image rotation.<br></br>
     * Note: the 4 points may not be a rectangle if the image was rotates to NOT stright angle (!=
     * 90/180/270).
     *
     * @return 4 points (x0,y0,x1,y1,x2,y2,x3,y3) of cropped area boundaries
     */
    val cropPoints: FloatArray
        get() {

            // Get crop window position relative to the displayed image.
            val cropWindowRect: RectF? = mCropOverlayView!!.cropWindowRect
            val points = floatArrayOf(
                cropWindowRect?.left.orZero(),
                cropWindowRect?.top.orZero(),
                cropWindowRect?.right.orZero(),
                cropWindowRect?.top.orZero(),
                cropWindowRect?.right.orZero(),
                cropWindowRect?.bottom.orZero(),
                cropWindowRect?.left.orZero(),
                cropWindowRect?.bottom.orZero()
            )
            mImageMatrix.invert(mImageInverseMatrix)
            mImageInverseMatrix.mapPoints(points)
            for (i in points.indices) {
                points[i] *= mLoadedSampleSize.toFloat()
            }
            return points
        }

    val croppedImage: Bitmap?
        get() = getCroppedImage(0, 0, RequestSizeOptions.NONE)

    fun getCroppedImage(reqWidth: Int, reqHeight: Int): Bitmap? {
        return getCroppedImage(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    fun getCroppedImage(reqWidth: Int, reqHeight: Int, options: RequestSizeOptions): Bitmap? {
        var reqWidth = reqWidth
        var reqHeight = reqHeight
        var croppedBitmap: Bitmap? = null
        if (mBitmap != null) {
            mImageView.clearAnimation()
            reqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0
            reqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0
            if (mLoadedImageUri != null
                && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING)
            ) {
                val orgWidth = mBitmap!!.width * mLoadedSampleSize
                val orgHeight = mBitmap!!.height * mLoadedSampleSize
                val bitmapSampled: BitmapUtils.BitmapSampled = BitmapUtils.cropBitmap(
                    context,
                    mLoadedImageUri!!,
                    cropPoints,
                    mDegreesRotated,
                    orgWidth,
                    orgHeight,
                    mCropOverlayView!!.isFixAspectRatio,
                    mCropOverlayView.aspectRatioX,
                    mCropOverlayView.aspectRatioY,
                    reqWidth,
                    reqHeight,
                    mFlipHorizontally,
                    mFlipVertically
                )
                croppedBitmap = bitmapSampled.bitmap
            } else {
                croppedBitmap = BitmapUtils.cropBitmapObjectHandleOOM(
                    mBitmap!!,
                    cropPoints,
                    mDegreesRotated,
                    mCropOverlayView!!.isFixAspectRatio,
                    mCropOverlayView.aspectRatioX,
                    mCropOverlayView.aspectRatioY,
                    mFlipHorizontally,
                    mFlipVertically
                ).bitmap
            }
            croppedBitmap =
                croppedBitmap?.let { BitmapUtils.resizeBitmap(it, reqWidth, reqHeight, options) }
        }
        return croppedBitmap
    }

    /**
     * Gets the cropped image based on the current crop window.<br></br>
     * The result will be invoked to listener set by [ ][.setOnCropImageCompleteListener].
     */
    val croppedImageAsync: Unit
        get() {
            getCroppedImageAsync(0, 0, RequestSizeOptions.NONE)
        }

    fun getCroppedImageAsync(reqWidth: Int, reqHeight: Int) {
        getCroppedImageAsync(reqWidth, reqHeight, RequestSizeOptions.RESIZE_INSIDE)
    }

    fun getCroppedImageAsync(reqWidth: Int, reqHeight: Int, options: RequestSizeOptions) {
        if (mOnCropImageCompleteListener == null) {
            throw IllegalArgumentException("mOnCropImageCompleteListener is not set")
        }
        startCropWorkerTask(reqWidth, reqHeight, options, null, null, 0)
    }

    fun saveCroppedImageAsync(saveUri: Uri?) {
        saveCroppedImageAsync(saveUri, CompressFormat.JPEG, 90, 0, 0, RequestSizeOptions.NONE)
    }

    fun saveCroppedImageAsync(
        saveUri: Uri?, saveCompressFormat: CompressFormat?, saveCompressQuality: Int
    ) {
        saveCroppedImageAsync(
            saveUri, saveCompressFormat, saveCompressQuality, 0, 0, RequestSizeOptions.NONE
        )
    }

    fun saveCroppedImageAsync(
        saveUri: Uri?,
        saveCompressFormat: CompressFormat?,
        saveCompressQuality: Int,
        reqWidth: Int,
        reqHeight: Int
    ) {
        saveCroppedImageAsync(
            saveUri,
            saveCompressFormat,
            saveCompressQuality,
            reqWidth,
            reqHeight,
            RequestSizeOptions.RESIZE_INSIDE
        )
    }

    fun saveCroppedImageAsync(
        saveUri: Uri?,
        saveCompressFormat: CompressFormat?,
        saveCompressQuality: Int,
        reqWidth: Int,
        reqHeight: Int,
        options: RequestSizeOptions
    ) {
        if (mOnCropImageCompleteListener == null) {
            throw IllegalArgumentException("mOnCropImageCompleteListener is not set")
        }
        startCropWorkerTask(
            reqWidth, reqHeight, options, saveUri, saveCompressFormat, saveCompressQuality
        )
    }

    /** Set the callback t  */
    fun setOnSetCropOverlayReleasedListener(listener: OnSetCropOverlayReleasedListener?) {
        mOnCropOverlayReleasedListener = listener
    }

    /** Set the callback when the cropping is moved  */
    fun setOnSetCropOverlayMovedListener(listener: OnSetCropOverlayMovedListener?) {
        mOnSetCropOverlayMovedListener = listener
    }

    /** Set the callback when the crop window is changed  */
    fun setOnCropWindowChangedListener(listener: OnSetCropWindowChangeListener?) {
        mOnSetCropWindowChangeListener = listener
    }

    /**
     * Set the callback to be invoked when image async loading ([.setImageUriAsync]) is
     * complete (successful or failed).
     */
    fun setOnSetImageUriCompleteListener(listener: OnSetImageUriCompleteListener?) {
        mOnSetImageUriCompleteListener = listener
    }

    /**
     * Set the callback to be invoked when image async cropping image ([.getCroppedImageAsync]
     * or [.saveCroppedImageAsync]) is complete (successful or failed).
     */
    fun setOnCropImageCompleteListener(listener: OnCropImageCompleteListener?) {
        mOnCropImageCompleteListener = listener
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(bitmap, 0, null, 1, 0)
    }

    fun setImageBitmap(bitmap: Bitmap?, exif: ExifInterface?) {
        val setBitmap: Bitmap?
        var degreesRotated = 0
        if (bitmap != null && exif != null) {
            val result: BitmapUtils.RotateBitmapResult = BitmapUtils.rotateBitmapByExif(bitmap, exif)
            setBitmap = result.bitmap
            degreesRotated = result.degrees
            mInitialDegreesRotated = result.degrees
        } else {
            setBitmap = bitmap
        }
        mCropOverlayView!!.initialCropWindowRect = null
        setBitmap(setBitmap, 0, null, 1, degreesRotated)
    }

    fun setImageUriAsync(uri: Uri?) {
        if (uri != null) {
            mBitmapLoadingWorkerTask?.let {
                if (it.get() != null) it.get()?.cancel(true)
            }

            clearImageInt()
            mRestoreCropWindowRect = null
            mRestoreDegreesRotated = 0
            mCropOverlayView!!.initialCropWindowRect = null
            mBitmapLoadingWorkerTask = WeakReference(BitmapLoadingWorkerTask(this, uri))
            mBitmapLoadingWorkerTask?.get()?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            setProgressBarVisibility()
        }
    }

    /** Clear the current image set for cropping.  */
    fun clearImage() {
        clearImageInt()
        mCropOverlayView!!.initialCropWindowRect = null
    }

    fun rotateImage(degrees: Int) {
        var degrees = degrees
        if (mBitmap != null) {
            // Force degrees to be a non-zero value between 0 and 360 (inclusive)
            if (degrees < 0) {
                degrees = degrees % 360 + 360
            } else {
                degrees %= 360
            }
            val flipAxes = (!mCropOverlayView!!.isFixAspectRatio
                    && (degrees in 46..134 || degrees > 215 && (degrees < 305)))
            mCropOverlayView.cropWindowRect?.let { BitmapUtils.RECT.set(it) }
            var halfWidth: Float =
                (if (flipAxes) BitmapUtils.RECT.height() else BitmapUtils.RECT.width()) / 2f
            var halfHeight: Float =
                (if (flipAxes) BitmapUtils.RECT.width() else BitmapUtils.RECT.height()) / 2f
            if (flipAxes) {
                val isFlippedHorizontally = mFlipHorizontally
                mFlipHorizontally = mFlipVertically
                mFlipVertically = isFlippedHorizontally
            }
            mImageMatrix.invert(mImageInverseMatrix)
            BitmapUtils.POINTS[0] = BitmapUtils.RECT.centerX()
            BitmapUtils.POINTS[1] = BitmapUtils.RECT.centerY()
            BitmapUtils.POINTS[2] = 0f
            BitmapUtils.POINTS[3] = 0f
            BitmapUtils.POINTS[4] = 1f
            BitmapUtils.POINTS[5] = 0f
            mImageInverseMatrix.mapPoints(BitmapUtils.POINTS)

            // This is valid because degrees is not negative.
            mDegreesRotated = (mDegreesRotated + degrees) % 360
            applyImageMatrix(width.toFloat(), height.toFloat(), true, false)

            // adjust the zoom so the crop window size remains the same even after image scale change
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)
            mZoom /= Math.sqrt(
                (BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble().pow(2.0)
                        + (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble().pow(2.0)
            ).toFloat()
            mZoom = mZoom.coerceAtLeast(1f)
            applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
            mImageMatrix.mapPoints(BitmapUtils.POINTS2, BitmapUtils.POINTS)

            // adjust the width/height by the changes in scaling to the image
            val change = sqrt(
                ((BitmapUtils.POINTS2[4] - BitmapUtils.POINTS2[2]).toDouble().pow(2.0)
                        + (BitmapUtils.POINTS2[5] - BitmapUtils.POINTS2[3]).toDouble().pow(2.0))
            )
            halfWidth *= change.toFloat()
            halfHeight *= change.toFloat()

            BitmapUtils.RECT.set(
                BitmapUtils.POINTS2[0] - halfWidth,
                BitmapUtils.POINTS2[1] - halfHeight,
                BitmapUtils.POINTS2[0] + halfWidth,
                BitmapUtils.POINTS2[1] + halfHeight
            )
            mCropOverlayView.resetCropOverlayView()
            mCropOverlayView.cropWindowRect = BitmapUtils.RECT
            applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
            handleCropWindowChanged(false, false)
            mCropOverlayView.fixCurrentCropWindowRect()
        }
    }

    /** Flips the image horizontally.  */
    fun flipImageHorizontally() {
        mFlipHorizontally = !mFlipHorizontally
        applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
    }

    /** Flips the image vertically.  */
    fun flipImageVertically() {
        mFlipVertically = !mFlipVertically
        applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
    }

    fun onSetImageUriAsyncComplete(result: BitmapLoadingWorkerTask.Result) {
        mBitmapLoadingWorkerTask = null
        setProgressBarVisibility()
        if (result.error == null) {
            mInitialDegreesRotated = result.degreesRotated
            setBitmap(result.bitmap, 0, result.uri, result.loadSampleSize, result.degreesRotated)
        }
        val listener = mOnSetImageUriCompleteListener
        listener?.onSetImageUriComplete(this, result.uri, result.error)
    }

    fun onImageCroppingAsyncComplete(result: BitmapCroppingWorkerTask.Result) {
        mBitmapCroppingWorkerTask = null
        setProgressBarVisibility()
        val listener = mOnCropImageCompleteListener
        if (listener != null) {
            val cropResult = CropResult(
                mBitmap,
                mLoadedImageUri,
                result.bitmap,
                result.uri,
                result.error,
                cropPoints,
                cropRect,
                wholeImageRect,
                rotatedDegrees,
                result.sampleSize
            )
            listener.onCropImageComplete(this, cropResult)
        }
    }

    private fun setBitmap(
        bitmap: Bitmap?,
        imageResource: Int,
        imageUri: Uri?,
        loadSampleSize: Int,
        degreesRotated: Int
    ) {
        if (mBitmap == null || mBitmap != bitmap) {
            mImageView.clearAnimation()
            clearImageInt()
            mBitmap = bitmap
            mImageView.setImageBitmap(mBitmap)
            mLoadedImageUri = imageUri
            mImageResource = imageResource
            mLoadedSampleSize = loadSampleSize
            mDegreesRotated = degreesRotated
            applyImageMatrix(width.toFloat(), height.toFloat(), true, false)
            if (mCropOverlayView != null) {
                mCropOverlayView.resetCropOverlayView()
                setCropOverlayVisibility()
            }
        }
    }

    private fun clearImageInt() {

        // if we allocated the bitmap, release it as fast as possible
        if (mBitmap != null && (mImageResource > 0 || mLoadedImageUri != null)) {
            mBitmap!!.recycle()
        }
        mBitmap = null

        // clean the loaded image flags for new image
        mImageResource = 0
        mLoadedImageUri = null
        mLoadedSampleSize = 1
        mDegreesRotated = 0
        mZoom = 1f
        mZoomOffsetX = 0f
        mZoomOffsetY = 0f
        mImageMatrix.reset()
        mSaveInstanceStateBitmapUri = null
        mImageView.setImageBitmap(null)
        setCropOverlayVisibility()
    }

    fun startCropWorkerTask(
        reqWidth: Int,
        reqHeight: Int,
        options: RequestSizeOptions,
        saveUri: Uri?,
        saveCompressFormat: CompressFormat?,
        saveCompressQuality: Int
    ) {
        var reqWidth = reqWidth
        var reqHeight = reqHeight
        val bitmap = mBitmap
        if (bitmap != null) {
            mImageView.clearAnimation()
            mBitmapCroppingWorkerTask?.let {
                if (it.get() != null) {
                    it.get()?.cancel(true)
                }
            }
            reqWidth = if (options != RequestSizeOptions.NONE) reqWidth else 0
            reqHeight = if (options != RequestSizeOptions.NONE) reqHeight else 0
            val orgWidth = bitmap.width * mLoadedSampleSize
            val orgHeight = bitmap.height * mLoadedSampleSize
            if ((mLoadedImageUri != null
                        && (mLoadedSampleSize > 1 || options == RequestSizeOptions.SAMPLING))
            ) {
                mBitmapCroppingWorkerTask = WeakReference(
                    BitmapCroppingWorkerTask(
                        this,
                        mLoadedImageUri,
                        cropPoints,
                        mDegreesRotated,
                        orgWidth,
                        orgHeight,
                        mCropOverlayView!!.isFixAspectRatio,
                        mCropOverlayView.aspectRatioX,
                        mCropOverlayView.aspectRatioY,
                        reqWidth,
                        reqHeight,
                        mFlipHorizontally,
                        mFlipVertically,
                        options,
                        saveUri,
                        saveCompressFormat,
                        saveCompressQuality
                    )
                )
            } else {
                mBitmapCroppingWorkerTask = WeakReference(
                    BitmapCroppingWorkerTask(
                        this,
                        bitmap,
                        cropPoints,
                        mDegreesRotated,
                        mCropOverlayView!!.isFixAspectRatio,
                        mCropOverlayView.aspectRatioX,
                        mCropOverlayView.aspectRatioY,
                        reqWidth,
                        reqHeight,
                        mFlipHorizontally,
                        mFlipVertically,
                        options,
                        saveUri,
                        saveCompressFormat,
                        saveCompressQuality
                    )
                )
            }
            mBitmapCroppingWorkerTask!!.get()?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            setProgressBarVisibility()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        if ((mLoadedImageUri == null) && (mBitmap == null) && (mImageResource < 1)) {
            return super.onSaveInstanceState()
        }
        val bundle = Bundle()
        var imageUri: Uri? = mLoadedImageUri
        if (isSaveBitmapToInstanceState && (imageUri == null) && (mImageResource < 1)) {
            imageUri = mBitmap?.let {
                BitmapUtils.writeTempStateStoreBitmap(context, it, mSaveInstanceStateBitmapUri)
            }
            mSaveInstanceStateBitmapUri = imageUri
        }
        if (imageUri != null && mBitmap != null) {
            val key: String = UUID.randomUUID().toString()
            BitmapUtils.mStateBitmap = Pair(key, WeakReference(mBitmap))
            bundle.putString("LOADED_IMAGE_STATE_BITMAP_KEY", key)
        }
        mBitmapLoadingWorkerTask?.let {
            val task: BitmapLoadingWorkerTask? = it.get()
            task?.let {
                bundle.putParcelable("LOADING_IMAGE_URI", task.uri)
            }
        }
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putParcelable("LOADED_IMAGE_URI", imageUri)
        bundle.putInt("LOADED_IMAGE_RESOURCE", mImageResource)
        bundle.putInt("LOADED_SAMPLE_SIZE", mLoadedSampleSize)
        bundle.putInt("DEGREES_ROTATED", mDegreesRotated)
        bundle.putParcelable("INITIAL_CROP_RECT", mCropOverlayView!!.initialCropWindowRect)
        mCropOverlayView.cropWindowRect?.let { BitmapUtils.RECT.set(it) }
        mImageMatrix.invert(mImageInverseMatrix)
        mImageInverseMatrix.mapRect(BitmapUtils.RECT)
        bundle.putParcelable("CROP_WINDOW_RECT", BitmapUtils.RECT)
        bundle.putString("CROP_SHAPE", mCropOverlayView.cropShape!!.name)
        bundle.putBoolean("CROP_AUTO_ZOOM_ENABLED", mAutoZoomEnabled)
        bundle.putInt("CROP_MAX_ZOOM", mMaxZoom)
        bundle.putBoolean("CROP_FLIP_HORIZONTALLY", mFlipHorizontally)
        bundle.putBoolean("CROP_FLIP_VERTICALLY", mFlipVertically)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle: Bundle = state as Bundle

            if ((mBitmapLoadingWorkerTask == null
                        ) && (mLoadedImageUri == null
                        ) && (mBitmap == null
                        ) && (mImageResource == 0)
            ) {
                var uri: Uri? = bundle.getParcelable("LOADED_IMAGE_URI")
                if (uri != null) {
                    val key: String? = bundle.getString("LOADED_IMAGE_STATE_BITMAP_KEY")
                    if (key != null) {
                        val stateBitmap: Bitmap? =
                            if (BitmapUtils.mStateBitmap != null && BitmapUtils.mStateBitmap!!.first.equals(key))
                                BitmapUtils.mStateBitmap!!.second.get() else null
                        BitmapUtils.mStateBitmap = null
                        if (stateBitmap != null && !stateBitmap.isRecycled) {
                            setBitmap(stateBitmap, 0, uri, bundle.getInt("LOADED_SAMPLE_SIZE"), 0)
                        }
                    }
                    if (mLoadedImageUri == null) {
                        setImageUriAsync(uri)
                    }
                } else {
                    val resId: Int = bundle.getInt("LOADED_IMAGE_RESOURCE")
                    if (resId > 0) {
                        imageResource = resId
                    } else {
                        uri = bundle.getParcelable("LOADING_IMAGE_URI")
                        if (uri != null) {
                            setImageUriAsync(uri)
                        }
                    }
                }
                mRestoreDegreesRotated = bundle.getInt("DEGREES_ROTATED")
                mDegreesRotated = mRestoreDegreesRotated
                val initialCropRect: Rect? = bundle.getParcelable("INITIAL_CROP_RECT")
                if ((initialCropRect != null
                            && (initialCropRect.width() > 0 || initialCropRect.height() > 0))
                ) {
                    mCropOverlayView!!.initialCropWindowRect = initialCropRect
                }
                val cropWindowRect: RectF? = bundle.getParcelable("CROP_WINDOW_RECT")
                if (cropWindowRect != null && (cropWindowRect.width() > 0 || cropWindowRect.height() > 0)) {
                    mRestoreCropWindowRect = cropWindowRect
                }
                mCropOverlayView?.cropShape = bundle.getString("CROP_SHAPE")?.let { CropShape.valueOf(it) }
                mAutoZoomEnabled = bundle.getBoolean("CROP_AUTO_ZOOM_ENABLED")
                mMaxZoom = bundle.getInt("CROP_MAX_ZOOM")
                mFlipHorizontally = bundle.getBoolean("CROP_FLIP_HORIZONTALLY")
                mFlipVertically = bundle.getBoolean("CROP_FLIP_VERTICALLY")
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)
        if (mBitmap != null) {

            // Bypasses a baffling bug when used within a ScrollView, where heightSize is set to 0.
            if (heightSize == 0) {
                heightSize = mBitmap!!.height
            }
            val desiredWidth: Int
            val desiredHeight: Int
            var viewToBitmapWidthRatio = Double.POSITIVE_INFINITY
            var viewToBitmapHeightRatio = Double.POSITIVE_INFINITY

            // Checks if either width or height needs to be fixed
            if (widthSize < mBitmap!!.width) {
                viewToBitmapWidthRatio = widthSize.toDouble() / mBitmap!!.width.toDouble()
            }
            if (heightSize < mBitmap!!.height) {
                viewToBitmapHeightRatio = heightSize.toDouble() / mBitmap!!.height.toDouble()
            }

            // If either needs to be fixed, choose smallest ratio and calculate from there
            if ((viewToBitmapWidthRatio != Double.POSITIVE_INFINITY
                        || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY)
            ) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize
                    desiredHeight = (mBitmap!!.height * viewToBitmapWidthRatio).toInt()
                } else {
                    desiredHeight = heightSize
                    desiredWidth = (mBitmap!!.width * viewToBitmapHeightRatio).toInt()
                }
            } else {
                // Otherwise, the picture is within frame layout bounds. Desired width is simply picture
                // size
                desiredWidth = mBitmap!!.width
                desiredHeight = mBitmap!!.height
            }
            val width = getOnMeasureSpec(widthMode, widthSize, desiredWidth)
            val height = getOnMeasureSpec(heightMode, heightSize, desiredHeight)
            mLayoutWidth = width
            mLayoutHeight = height
            setMeasuredDimension(mLayoutWidth, mLayoutHeight)
        } else {
            setMeasuredDimension(widthSize, heightSize)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            // Gets original parameters, and creates the new parameters
            val origParams: ViewGroup.LayoutParams = this.layoutParams
            origParams.width = mLayoutWidth
            origParams.height = mLayoutHeight
            layoutParams = origParams
            if (mBitmap != null) {
                applyImageMatrix((r - l).toFloat(), (b - t).toFloat(), true, false)

                // after state restore we want to restore the window crop, possible only after widget size
                // is known
                if (mRestoreCropWindowRect != null) {
                    if (mRestoreDegreesRotated != mInitialDegreesRotated) {
                        mDegreesRotated = mRestoreDegreesRotated
                        applyImageMatrix((r - l).toFloat(), (b - t).toFloat(), true, false)
                    }
                    mImageMatrix.mapRect(mRestoreCropWindowRect)
                    mCropOverlayView!!.cropWindowRect = mRestoreCropWindowRect
                    handleCropWindowChanged(false, false)
                    mCropOverlayView.fixCurrentCropWindowRect()
                    mRestoreCropWindowRect = null
                } else if (mSizeChanged) {
                    mSizeChanged = false
                    handleCropWindowChanged(false, false)
                }
            } else {
                updateImageBounds(true)
            }
        } else {
            updateImageBounds(true)
        }
    }

    /**
     * Detect size change to handle auto-zoom using [.handleCropWindowChanged]
     * in [.layout].
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSizeChanged = oldw > 0 && oldh > 0
    }

    private fun handleCropWindowChanged(inProgress: Boolean, animate: Boolean) {
        val width: Int = getWidth()
        val height: Int = getHeight()
        if ((mBitmap != null) && (width > 0) && (height > 0)) {
            val cropRect: RectF? = mCropOverlayView!!.cropWindowRect
            if (inProgress) {
                if ((cropRect?.left.orZero() < 0) || (cropRect?.top.orZero() < 0) || (cropRect?.right.orZero() > width) || (cropRect?.bottom.orZero() > height)) {
                    applyImageMatrix(width.toFloat(), height.toFloat(), false, false)
                }
            } else if (mAutoZoomEnabled || mZoom > 1) {
                var newZoom = 0f
                // keep the cropping window covered area to 50%-65% of zoomed sub-area
                if ((mZoom < mMaxZoom) && (cropRect?.width().orZero() < width * 0.5f) && (cropRect?.height().orZero() < height * 0.5f)) {
                    newZoom = mMaxZoom.toFloat().coerceAtMost(
                        (width / (cropRect?.width().orZero() / mZoom / 0.64f)).coerceAtMost(height / (cropRect?.height().orZero() / mZoom / 0.64f)))
                }
                if (mZoom > 1 && (cropRect?.width().orZero() > width * 0.65f || cropRect?.height().orZero() > height * 0.65f)) {
                    newZoom = 1f.coerceAtLeast((width / (cropRect?.width().orZero() / mZoom / 0.51f)).coerceAtMost(height / (cropRect?.height().orZero() / mZoom / 0.51f)))
                }
                if (!mAutoZoomEnabled) {
                    newZoom = 1f
                }
                if (newZoom > 0 && newZoom != mZoom) {
//                    if (animate) {
//                        if (mAnimation == null) {
//                            // lazy create animation single instance
//                            mAnimation = CropImageAnimation(mImageView, mCropOverlayView)
//                        }
//                        // set the state for animation to start from
//                        mAnimation.setStartState(mImagePoints, mImageMatrix)
//                    }
                    mZoom = newZoom
                    applyImageMatrix(width.toFloat(), height.toFloat(), true, animate)
                }
            }
            if (mOnSetCropWindowChangeListener != null && !inProgress) {
                mOnSetCropWindowChangeListener!!.onCropWindowChanged()
            }
        }
    }

    /**
     * Apply matrix to handle the image inside the image view.
     *
     * @param width the width of the image view
     * @param height the height of the image view
     */
    private fun applyImageMatrix(width: Float, height: Float, center: Boolean, animate: Boolean) {
        if ((mBitmap != null) && (width > 0) && (height > 0)) {
            mImageMatrix.invert(mImageInverseMatrix)
            val cropRect: RectF? = mCropOverlayView!!.cropWindowRect
            mImageInverseMatrix.mapRect(cropRect)
            mImageMatrix.reset()

            // move the image to the center of the image view first so we can manipulate it from there
            mImageMatrix.postTranslate(
                (width - mBitmap!!.width) / 2, (height - mBitmap!!.height) / 2
            )
            mapImagePointsByImageMatrix()

            // rotate the image the required degrees from center of image
            if (mDegreesRotated > 0) {
                mImageMatrix.postRotate(
                    mDegreesRotated.toFloat(),
                    BitmapUtils.getRectCenterX(mImagePoints),
                    BitmapUtils.getRectCenterY(mImagePoints)
                )
                mapImagePointsByImageMatrix()
            }

            // scale the image to the image view, image rect transformed to know new width/height
            val scale: Float = Math.min(
                width / BitmapUtils.getRectWidth(mImagePoints),
                height / BitmapUtils.getRectHeight(mImagePoints)
            )
            if (((mScaleType == ScaleType.FIT_CENTER
                        ) || (mScaleType == ScaleType.CENTER_INSIDE && scale < 1)
                        || (scale > 1 && mAutoZoomEnabled))
            ) {
                mImageMatrix.postScale(
                    scale,
                    scale,
                    BitmapUtils.getRectCenterX(mImagePoints),
                    BitmapUtils.getRectCenterY(mImagePoints)
                )
                mapImagePointsByImageMatrix()
            }

            // scale by the current zoom level
            val scaleX = if (mFlipHorizontally) -mZoom else mZoom
            val scaleY = if (mFlipVertically) -mZoom else mZoom
            mImageMatrix.postScale(
                scaleX,
                scaleY,
                BitmapUtils.getRectCenterX(mImagePoints),
                BitmapUtils.getRectCenterY(mImagePoints)
            )
            mapImagePointsByImageMatrix()
            mImageMatrix.mapRect(cropRect)
            if (center) {
                // set the zoomed area to be as to the center of cropping window as possible
                mZoomOffsetX = if (width > BitmapUtils.getRectWidth(mImagePoints)) 0f
                else ((width / 2 - cropRect?.centerX()
                    .orZero()).coerceAtMost(-BitmapUtils.getRectLeft(mImagePoints))
                    .coerceAtLeast(getWidth() - BitmapUtils.getRectRight(mImagePoints)) / scaleX)
                mZoomOffsetY =
                    if (height > BitmapUtils.getRectHeight(mImagePoints)) 0f else ((height / 2 - cropRect?.centerY()
                        .orZero()).coerceAtMost(-BitmapUtils.getRectTop(mImagePoints))
                        .coerceAtLeast(getHeight() - BitmapUtils.getRectBottom(mImagePoints)) / scaleY)
            } else {
                // adjust the zoomed area so the crop window rectangle will be inside the area in case it
                // was moved outside
                mZoomOffsetX = ((mZoomOffsetX * scaleX).coerceAtLeast(-cropRect?.left.orZero()).coerceAtMost(-cropRect?.right.orZero() + width) / scaleX)
                mZoomOffsetY = ((mZoomOffsetY * scaleY).coerceAtLeast(-cropRect?.top.orZero()).coerceAtMost(-cropRect?.bottom.orZero() + height) / scaleY)
            }

            // apply to zoom offset translate and update the crop rectangle to offset correctly
            mImageMatrix.postTranslate(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
            cropRect?.offset(mZoomOffsetX * scaleX, mZoomOffsetY * scaleY)
            mCropOverlayView.cropWindowRect = cropRect
            mapImagePointsByImageMatrix()
            mCropOverlayView.invalidate()

            // set matrix to apply
            if (animate) {
                // set the state for animation to end in, start animation now
//                mAnimation.setEndState(mImagePoints, mImageMatrix)
//                mImageView.startAnimation(mAnimation)
            } else {
                mImageView.imageMatrix = mImageMatrix
            }

            // update the image rectangle in the crop overlay
            updateImageBounds(false)
        }
    }

    /**
     * Adjust the given image rectangle by image transformation matrix to know the final rectangle of
     * the image.<br></br>
     * To get the proper rectangle it must be first reset to original image rectangle.
     */
    private fun mapImagePointsByImageMatrix() {
        mImagePoints[0] = 0f
        mImagePoints[1] = 0f
        mImagePoints[2] = mBitmap!!.width.toFloat()
        mImagePoints[3] = 0f
        mImagePoints[4] = mBitmap!!.width.toFloat()
        mImagePoints[5] = mBitmap!!.height.toFloat()
        mImagePoints[6] = 0f
        mImagePoints[7] = mBitmap!!.height.toFloat()
        mImageMatrix.mapPoints(mImagePoints)
        mScaleImagePoints[0] = 0f
        mScaleImagePoints[1] = 0f
        mScaleImagePoints[2] = 100f
        mScaleImagePoints[3] = 0f
        mScaleImagePoints[4] = 100f
        mScaleImagePoints[5] = 100f
        mScaleImagePoints[6] = 0f
        mScaleImagePoints[7] = 100f
        mImageMatrix.mapPoints(mScaleImagePoints)
    }

    /**
     * Set visibility of crop overlay to hide it when there is no image or specificly set by client.
     */
    private fun setCropOverlayVisibility() {
        mCropOverlayView?.setVisibility(if (mShowCropOverlay && mBitmap != null) VISIBLE else INVISIBLE)
    }

    /**
     * Set visibility of progress bar when async loading/cropping is in process and show is enabled.
     */
    private fun setProgressBarVisibility() {
        val visible = (mShowProgressBar && (mBitmap == null && mBitmapLoadingWorkerTask != null || mBitmapCroppingWorkerTask != null))
        mProgressBar.setVisibility(if (visible) VISIBLE else INVISIBLE)
    }

    /** Update the scale factor between the actual image bitmap and the shown image.<br></br>  */
    private fun updateImageBounds(clear: Boolean) {
        if (mBitmap != null && !clear) {

            // Get the scale factor between the actual Bitmap dimensions and the displayed dimensions for
            // width/height.
            val scaleFactorWidth: Float =
                100f * mLoadedSampleSize / BitmapUtils.getRectWidth(mScaleImagePoints)
            val scaleFactorHeight: Float =
                100f * mLoadedSampleSize / BitmapUtils.getRectHeight(mScaleImagePoints)
            mCropOverlayView!!.setCropWindowLimits(
                width.toFloat(), height.toFloat(), scaleFactorWidth, scaleFactorHeight
            )
        }

        // set the bitmap rectangle and update the crop window after scale factor is set
        mCropOverlayView!!.setBounds(if (clear) null else mImagePoints, width, height)
    }

    enum class CropShape {
        RECTANGLE
    }
    /**
     * Options for scaling the bounds of cropping image to the bounds of Crop Image View.<br></br>
     * Note: Some options are affected by auto-zoom, if enabled.
     */
    enum class ScaleType {
        FIT_CENTER,
        CENTER,
        CENTER_CROP,
        CENTER_INSIDE
    }

    /** Possible options for handling requested width/height for cropping.  */
    enum class RequestSizeOptions {
        NONE,
        SAMPLING,
        RESIZE_INSIDE,
        RESIZE_FIT,
        RESIZE_EXACT
    }
    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropOverlayReleasedListener {
        fun onCropOverlayReleased(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropOverlayMovedListener {
        fun onCropOverlayMoved(rect: Rect?)
    }

    /** Interface definition for a callback to be invoked when the crop overlay is released.  */
    interface OnSetCropWindowChangeListener {
        /** Called when the crop window is changed  */
        fun onCropWindowChanged()
    }

    /** Interface definition for a callback to be invoked when image async loading is complete.  */
    interface OnSetImageUriCompleteListener {
        fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?)
    }

    /** Interface definition for a callback to be invoked when image async crop is complete.  */
    interface OnCropImageCompleteListener {
        fun onCropImageComplete(view: CropImageView?, result: CropResult?)
    }

    /** Result data of crop image.  */
    open class CropResult internal constructor(
        /**
         * The image bitmap of the original image loaded for cropping.<br></br>
         * Null if uri used to load image or activity result is used.
         */
        val originalBitmap: Bitmap?,
        originalUri: Uri?,
        bitmap: Bitmap?,
        uri: Uri?,
        error: Exception?,
        cropPoints: FloatArray,
        cropRect: Rect?,
        wholeImageRect: Rect?,
        rotation: Int,
        sampleSize: Int
    ) {
        private val mOriginalUri: Uri?
        val bitmap: Bitmap?
        private val mUri: Uri?
        val error: Exception?
        /** The 4 points of the cropping window in the source image  */
        /** The 4 points of the cropping window in the source image  */
        val cropPoints: FloatArray

        /** The rectangle of the cropping window in the source image  */
        private val mCropRect: Rect?

        /** The rectangle of the source image dimensions  */
        private val mWholeImageRect: Rect?
        /** The final rotation of the cropped image relative to source  */
        /** The final rotation of the cropped image relative to source  */
        val rotation: Int
        /** sample size used creating the crop bitmap to lower its size  */
        /** sample size used creating the crop bitmap to lower its size  */
        val sampleSize: Int

        init {
            mOriginalUri = originalUri
            this.bitmap = bitmap
            mUri = uri
            this.error = error
            this.cropPoints = cropPoints
            mCropRect = cropRect
            mWholeImageRect = wholeImageRect
            this.rotation = rotation
            this.sampleSize = sampleSize
        }

        /**
         * The Android uri of the original image loaded for cropping.<br></br>
         * Null if bitmap was used to load image.
         */
        val originalUri: Uri?
            get() = mOriginalUri

        /** Is the result is success or error.  */
        val isSuccessful: Boolean
            get() = error == null

        /**
         * The Android uri of the saved cropped image result Null if get cropped image was executed, no
         * output requested or failure.
         */
        val uri: Uri?
            get() = mUri

        /** The rectangle of the cropping window in the source image  */
        val cropRect: Rect?
            get() = mCropRect

        /** The rectangle of the source image dimensions  */
        val wholeImageRect: Rect?
            get() = mWholeImageRect
    }

    companion object {
        private fun getOnMeasureSpec(
            measureSpecMode: Int,
            measureSpecSize: Int,
            desiredSize: Int
        ): Int {

            // Measure Width
            val spec: Int
            if (measureSpecMode == MeasureSpec.EXACTLY) {
                // Must be this size
                spec = measureSpecSize
            } else if (measureSpecMode == MeasureSpec.AT_MOST) {
                // Can't be bigger than...; match_parent value
                spec = Math.min(desiredSize, measureSpecSize)
            } else {
                // Be whatever you want; wrap_content
                spec = desiredSize
            }
            return spec
        }
    }
}