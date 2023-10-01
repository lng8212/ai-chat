package com.longkd.chatgpt_openai.feature.art.wallpaper

import android.animation.Animator
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.lifecycle.lifecycleScope
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.FragmentSetWallpaperBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.delay


@Suppress("NAME_SHADOWING")
class SetWallpaperFragment: BaseFragment<FragmentSetWallpaperBinding>(R.layout.fragment_set_wallpaper) {
    var callBack: (() -> Unit) ?= null
    var onSetClick: (() -> Unit) ?= null
    private var bitmap: Bitmap ? = null
    private var isImageFromGenerate: Boolean? = false
    override fun initViews() {
        if (bitmap != null && bitmap?.isRecycled == false) {
            bitmap?.recycle()
            bitmap = null
        }
        val myWallpaperManager = WallpaperManager.getInstance(activity?.applicationContext)
        val wallPaperData = arguments?.getParcelable<WallPaperData>(KEY_WALLPAPER_DATA)
        isImageFromGenerate = arguments?.getBoolean(KEY_IS_IMAGE_FROM_GENERATE)
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val screenWidth: Int = metrics.widthPixels
        val screenHeight: Int = metrics.heightPixels
        myWallpaperManager.setWallpaperOffsetSteps(1f, 1f)
        myWallpaperManager.suggestDesiredDimensions(screenWidth, screenHeight)
        wallPaperData?.url?.let { data ->
            mBinding?.image?.let {
                Glide.with(this)
                    .asBitmap()
                    .apply(RequestOptions().override(screenWidth, screenHeight))
                    .load(data)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?
                        ) {
                            it.setImageBitmap(resource)
                            bitmap = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }

            onSetClick = {
                bitmap?.let {
                    myWallpaperManager.setBitmap(
                        cropBitmapFromCenterAndScreenSize(it, screenWidth.toFloat(), screenHeight.toFloat())
                    )
                }
            }
        }
    }

    private fun handleSave() {
        mBinding?.fmResultArtLatDownloaded?.playAnimation()
        mBinding?.fmResultArtLatDownloaded?.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                mBinding?.fmResultArtLLDownloaded?.gone()
                if (isImageFromGenerate == true) {
                    callBack?.invoke()
                    popBackStack()
                }
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }


    private fun cropBitmapFromCenterAndScreenSize(bitmap: Bitmap, screenWidth: Float, screenHeight: Float): Bitmap {
        var bitmap = bitmap
        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()
        val bitmapRatio = (bitmapWidth / bitmapHeight)
        val screenRatio = (screenWidth / screenHeight)
        val bitmapNewWidth: Int
        val bitmapNewHeight: Int
        if (screenRatio > bitmapRatio) {
            bitmapNewWidth = screenWidth.toInt()
            bitmapNewHeight = (bitmapNewWidth / bitmapRatio).toInt()
        } else {
            bitmapNewHeight = screenHeight.toInt()
            bitmapNewWidth = (bitmapNewHeight * bitmapRatio).toInt()
        }
        bitmap = Bitmap.createScaledBitmap(
            bitmap, bitmapNewWidth,
            bitmapNewHeight, true
        )

        val bitmapGapX: Int = ((bitmapNewWidth - screenWidth) / 2.0f).toInt()
        val bitmapGapY: Int = ((bitmapNewHeight - screenHeight) / 2.0f).toInt()
        bitmap = Bitmap.createBitmap(bitmap, bitmapGapX, bitmapGapY, screenWidth.toInt(), screenHeight.toInt())
        return bitmap
    }

    override fun initActions() {
        mBinding?.baseHeaderBtnLeft?.setOnSingleClick {
            handleOnBackPress()
        }

        mBinding?.btnSetWallpaper?.setOnSingleClick {
            mBinding?.fmResultArtLLDownloaded?.visible()
            lifecycleScope.launchWhenStarted {
                delay(DELAY)
                handleSave()
                onSetClick?.invoke()
            }
        }
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        if (isImageFromGenerate == true) {
            activity?.onBackPressed()
            return true
        }
        return true
    }

    override fun initData() {}

    companion object {
        private const val KEY_WALLPAPER_DATA = "key_wallpaper_data"
        private const val KEY_IS_IMAGE_FROM_GENERATE = "key_is_image_from_generate"
        private const val DELAY = 100L
        fun newInstance(wallPaperData: WallPaperData?, isImageFromGenerate: Boolean): SetWallpaperFragment{
            val args = Bundle()
            wallPaperData?.let { args.putParcelable(KEY_WALLPAPER_DATA, it) }
            args.putBoolean(KEY_IS_IMAGE_FROM_GENERATE, isImageFromGenerate)
            val fragment = SetWallpaperFragment()
            fragment.arguments = args
            return fragment
        }
    }
}