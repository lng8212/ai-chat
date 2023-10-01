package com.longkd.chatgpt_openai.base.customview

import android.graphics.RectF
import kotlin.math.abs


internal class CropWindowHandler {
    /** The 4 edges of the crop window defining its coordinates and size  */
    private val mEdges = RectF()

    /**
     * Rectangle used to return the edges rectangle without ability to change it and without creating
     * new all the time.
     */
    private val mGetEdges = RectF()

    /** Minimum width in pixels that the crop window can get.  */
    private var mMinCropWindowWidth = 0f

    /** Minimum height in pixels that the crop window can get.  */
    private var mMinCropWindowHeight = 0f

    /** Maximum width in pixels that the crop window can CURRENTLY get.  */
    private var mMaxCropWindowWidth = 0f

    /** Maximum height in pixels that the crop window can CURRENTLY get.  */
    private var mMaxCropWindowHeight = 0f

    /**
     * Minimum width in pixels that the result of cropping an image can get, affects crop window width
     * adjusted by width scale factor.
     */
    private var mMinCropResultWidth = 0f

    /**
     * Minimum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private var mMinCropResultHeight = 0f

    /**
     * Maximum width in pixels that the result of cropping an image can get, affects crop window width
     * adjusted by width scale factor.
     */
    private var mMaxCropResultWidth = 0f

    /**
     * Maximum height in pixels that the result of cropping an image can get, affects crop window
     * height adjusted by height scale factor.
     */
    private var mMaxCropResultHeight = 0f
    /** get the scale factor (on width) of the showen image to original image.  */
    /** The width scale factor of shown image and actual image  */
    var scaleFactorWidth = 1f
        private set
    /** get the scale factor (on height) of the showen image to original image.  */
    /** The height scale factor of shown image and actual image  */
    var scaleFactorHeight = 1f
        private set

    /** Get the left/top/right/bottom coordinates of the crop window.  */
    /** Set the left/top/right/bottom coordinates of the crop window.  */
    var rect: RectF?
        get() {
            mGetEdges.set(mEdges)
            return mGetEdges
        }
        set(rect) {
            mEdges.set(rect!!)
        }

    /** Minimum width in pixels that the crop window can get.  */
    val minCropWidth: Float
        get() = Math.max(mMinCropWindowWidth, mMinCropResultWidth / scaleFactorWidth)

    /** Minimum height in pixels that the crop window can get.  */
    val minCropHeight: Float
        get() = Math.max(mMinCropWindowHeight, mMinCropResultHeight / scaleFactorHeight)

    /** Maximum width in pixels that the crop window can get.  */
    val maxCropWidth: Float
        get() = Math.min(mMaxCropWindowWidth, mMaxCropResultWidth / scaleFactorWidth)

    /** Maximum height in pixels that the crop window can get.  */
    val maxCropHeight: Float
        get() = Math.min(mMaxCropWindowHeight, mMaxCropResultHeight / scaleFactorHeight)

    /**
     * the min size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMinCropResultSize(minCropResultWidth: Int, minCropResultHeight: Int) {
        mMinCropResultWidth = minCropResultWidth.toFloat()
        mMinCropResultHeight = minCropResultHeight.toFloat()
    }

    /**
     * the max size the resulting cropping image is allowed to be, affects the cropping window limits
     * (in pixels).<br></br>
     */
    fun setMaxCropResultSize(maxCropResultWidth: Int, maxCropResultHeight: Int) {
        mMaxCropResultWidth = maxCropResultWidth.toFloat()
        mMaxCropResultHeight = maxCropResultHeight.toFloat()
    }

    /**
     * set the max width/height and scale factor of the showen image to original image to scale the
     * limits appropriately.
     */
    fun setCropWindowLimits(
        maxWidth: Float, maxHeight: Float, scaleFactorWidth: Float, scaleFactorHeight: Float
    ) {
        mMaxCropWindowWidth = maxWidth
        mMaxCropWindowHeight = maxHeight
        this.scaleFactorWidth = scaleFactorWidth
        this.scaleFactorHeight = scaleFactorHeight
    }

    /** Set the variables to be used during crop window handling.  */
    fun setInitialAttributeValues(options: CropImageOptions) {
        mMinCropWindowWidth = options.minCropWindowWidth.toFloat()
        mMinCropWindowHeight = options.minCropWindowHeight.toFloat()
        mMinCropResultWidth = options.minCropResultWidth.toFloat()
        mMinCropResultHeight = options.minCropResultHeight.toFloat()
        mMaxCropResultWidth = options.maxCropResultWidth.toFloat()
        mMaxCropResultHeight = options.maxCropResultHeight.toFloat()
    }

    fun showGuidelines(): Boolean {
        return !(mEdges.width() < 100 || mEdges.height() < 100)
    }


    fun getMoveHandler(x: Float, y: Float, targetRadius: Float): CropWindowMoveHandler? {
        val type = getRectanglePressedMoveType(x, y, targetRadius)
        return if (type != null) CropWindowMoveHandler(type, this, x, y) else null
    }

    private fun getRectanglePressedMoveType(
        x: Float, y: Float, targetRadius: Float
    ): CropWindowMoveHandler.Type? {
        var moveType: CropWindowMoveHandler.Type? = null

        // Note: corner-handles take precedence, then side-handles, then center.
        if (isInCornerTargetZone(x, y, mEdges.left, mEdges.top, targetRadius)) {
            moveType = CropWindowMoveHandler.Type.TOP_LEFT
        } else if (isInCornerTargetZone(
                x, y, mEdges.right, mEdges.top, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.TOP_RIGHT
        } else if (isInCornerTargetZone(
                x, y, mEdges.left, mEdges.bottom, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.BOTTOM_LEFT
        } else if (isInCornerTargetZone(
                x, y, mEdges.right, mEdges.bottom, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.BOTTOM_RIGHT
        } else if (isInCenterTargetZone(
                x, y, mEdges.left, mEdges.top, mEdges.right, mEdges.bottom
            )
            && focusCenter()
        ) {
            moveType = CropWindowMoveHandler.Type.CENTER
        } else if (isInHorizontalTargetZone(
                x, y, mEdges.left, mEdges.right, mEdges.top, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.TOP
        } else if (isInHorizontalTargetZone(
                x, y, mEdges.left, mEdges.right, mEdges.bottom, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.BOTTOM
        } else if (isInVerticalTargetZone(
                x, y, mEdges.left, mEdges.top, mEdges.bottom, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.LEFT
        } else if (isInVerticalTargetZone(
                x, y, mEdges.right, mEdges.top, mEdges.bottom, targetRadius
            )
        ) {
            moveType = CropWindowMoveHandler.Type.RIGHT
        } else if (isInCenterTargetZone(
                x, y, mEdges.left, mEdges.top, mEdges.right, mEdges.bottom
            )
            && !focusCenter()
        ) {
            moveType = CropWindowMoveHandler.Type.CENTER
        }
        return moveType
    }

    private fun focusCenter(): Boolean {
        return !showGuidelines()
    } // endregion

    companion object {
        private fun isInCornerTargetZone(
            x: Float, y: Float, handleX: Float, handleY: Float, targetRadius: Float
        ): Boolean {
            return abs(x - handleX) <= targetRadius && abs(y - handleY) <= targetRadius
        }

        private fun isInHorizontalTargetZone(
            x: Float,
            y: Float,
            handleXStart: Float,
            handleXEnd: Float,
            handleY: Float,
            targetRadius: Float
        ): Boolean {
            return x > handleXStart && (x < handleXEnd) && (abs(y - handleY) <= targetRadius)
        }

        private fun isInVerticalTargetZone(
            x: Float,
            y: Float,
            handleX: Float,
            handleYStart: Float,
            handleYEnd: Float,
            targetRadius: Float
        ): Boolean {
            return abs(x - handleX) <= targetRadius && (y > handleYStart) && y < handleYEnd
        }

        private fun isInCenterTargetZone(
            x: Float, y: Float, left: Float, top: Float, right: Float, bottom: Float
        ): Boolean {
            return x > left && (x < right) && (y > top) && (y < bottom)
        }
    }
}