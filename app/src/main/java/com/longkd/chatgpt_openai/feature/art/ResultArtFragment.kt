package com.longkd.chatgpt_openai.feature.art

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.ErrorType
import com.longkd.chatgpt_openai.base.model.GenerateArtByVyroRequest
import com.longkd.chatgpt_openai.base.model.ResultDataDto
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentResultArtBinding
import com.longkd.chatgpt_openai.dialog.DialogFailArt
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.WatermarkImageDownloader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultArtFragment : BaseFragment<FragmentResultArtBinding>(R.layout.fragment_result_art) {
    private val mViewModel: ResultArtViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null
    private val TIMER_DEFAULT = 160000L
    private val TIMER_COLOR = 10000L
    private val TIMER_QUALITY = 25000L
    private val TIMER_ARTWORK = 40000L
    private var prompt: String? = null
    private var modelId: String? = null
    private var width: Int? = null
    private var height: Int? = null
    private var isShowImvMascot: Boolean = true
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) Toast.makeText(context, R.string.grantfailed, Toast.LENGTH_SHORT).show()

        }
    private var currentUrlImage = ""

    companion object {
        const val KEY_PROMPT = "KEY_PROMPT"
        const val KEY_STYLE = "KEY_STYLE"
        private const val KEY_MODEL_ID = "key_model_id"
        private const val KEY_WIDTH = "key_width"
        private const val KEY_HEIGHT = "key_height"
        private const val DEFAULT_SIZE = 512
        fun newInstance(
            prompt: String? = null,
            style: String? = null,
            modelId: String? = null,
            width: Int? = null,
            height: Int? = null
        ): ResultArtFragment {
            val args = Bundle()
            args.putString(KEY_PROMPT, prompt)
            args.putString(KEY_STYLE, style)
            args.putString(KEY_MODEL_ID, modelId)
            width?.let { args.putInt(KEY_WIDTH, width) }
            height?.let { args.putInt(KEY_HEIGHT, height) }
            val fragment = ResultArtFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        initTimer()
        prompt = arguments?.getString(KEY_PROMPT, "") ?: Strings.EMPTY
        modelId = arguments?.getString(KEY_MODEL_ID, "") ?: Strings.EMPTY
        width = arguments?.getInt(KEY_WIDTH)
        height = arguments?.getInt(KEY_HEIGHT)
        generateImage(
            prompt ?: "",
            modelId ?: "",
            width ?: DEFAULT_SIZE,
            height ?: DEFAULT_SIZE
        )
    }

    @SuppressLint("CheckResult")
    private fun generateImage(
        prompt: String,
        modelId: String,
        width: Int,
        height: Int,
        isReGenerate: Boolean = false
    ) {
        mBinding?.fmResultArtTvRegenerate?.isEnabled = false
//        mViewModel?.createAiArt(prompt.trim(), modelId, width, height)?.observe(this) { resultData ->
//            when (resultData) {
//                is ResultDataDto.Error -> {
//                    FirebaseTracking.logArtAction(context, if (isReGenerate) "regenerate" else "generate", status = "fail", modelId = modelId)
//                    if (resultData.errorType == ErrorType.END_VIP) {
//                    } else {
//                        mBinding?.fmResultArtLatLoading?.cancelAnimation()
//                        countDownTimer?.cancel()
//                        val dialog = DialogFailArt.newInstance()
//                        dialog.onDialogDismissListener = {
//                            popBackStack()
//                        }
//                        dialog.show(activity?.supportFragmentManager)
//                    }
//                }
//                is ResultDataDto.SuccessImage -> {
//                    FirebaseTracking.logArtAction(context, if (isReGenerate) "regenerate" else "generate", status = "success", modelId = modelId)
//                    mBinding?.fmResultArtCtLoading?.gone()
//                    mBinding?.fmResultArtLatLoading?.cancelAnimation()
//                    countDownTimer?.cancel()
//                    mBinding?.fmResultArtImv?.let {
//                        Glide.with(this@ResultArtFragment)
//                            .asBitmap()
//                            .apply { RequestOptions().override(width, height) }
//                            .load(resultData.url).placeholder(R.drawable.img_default_art)
//                            .into(object : CustomTarget<Bitmap>() {
//                                @Synchronized
//                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                    var outputBitmap: Bitmap? = null
//                                        val watermarkBitmap =
//                                            BitmapFactory.decodeResource(context?.resources, R.drawable.water_mark)
//                                        val watermarkWidth = resource.width / 4
//                                        val watermarkHeight =
//                                            watermarkWidth * watermarkBitmap.height / watermarkBitmap.width
//                                        val watermarkResized = Bitmap.createScaledBitmap(
//                                            watermarkBitmap,
//                                            watermarkWidth,
//                                            watermarkHeight,
//                                            false
//                                        )
//                                        outputBitmap =
//                                            Bitmap.createBitmap(
//                                                resource.width,
//                                                resource.height,
//                                                Bitmap.Config.ARGB_8888
//                                            )
//                                        val canvas = Canvas(outputBitmap)
//                                        canvas.drawBitmap(resource, 0f, 0f, null)
//                                        canvas.drawBitmap(
//                                            watermarkResized,
//                                            10f,
//                                            resource.height.toFloat() - watermarkHeight,
//                                            null
//                                        )
//                                    it.setImageBitmap(outputBitmap)
//                                    mBinding?.fmResultArtTvRegenerate?.isEnabled = true
//                                }
//
//                                override fun onLoadCleared(placeholder: Drawable?) {
//                                    // Do nothing
//                                }
//                            })
//                    }
//                    currentUrlImage = resultData.url
//                    mBinding?.fmResultArtTvPrompt?.text =
//                        arguments?.getString(KEY_PROMPT, "") ?: Strings.EMPTY
//                    mBinding?.fmResultArtTvStyle?.text = arguments?.getString(KEY_STYLE, "")
//                }
//                else -> {}
//            }
//        }

        mViewModel?.generateArtByVyro(
            GenerateArtByVyroRequest(
                prompt = " A surreal landscape with floating islands",
                styleId = 27,
                aspectRatio = "1:1"
            )
        )?.observe(this) { result ->
            when (result) {
                is ResultDataDto.Error -> {
                    if (result.errorType != ErrorType.END_VIP) {
                        mBinding?.fmResultArtLatLoading?.cancelAnimation()
                        countDownTimer?.cancel()
                        val dialog = DialogFailArt.newInstance()
                        dialog.onDialogDismissListener = {
                            popBackStack()
                        }
                        dialog.show(activity?.supportFragmentManager)
                    }
                }

                is ResultDataDto.SuccessImageVyro -> {
                    mBinding?.fmResultArtCtLoading?.gone()
                    mBinding?.fmResultArtLatLoading?.cancelAnimation()
                    countDownTimer?.cancel()
                    mBinding?.fmResultArtImv?.let {
                        Glide.with(this@ResultArtFragment)
                            .asBitmap()
                            .apply { RequestOptions().override(width, height) }
                            .load(result.data).placeholder(R.drawable.img_default_art)
                            .into(object : CustomTarget<Bitmap>() {
                                @Synchronized
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val watermarkBitmap =
                                        BitmapFactory.decodeResource(
                                            context?.resources,
                                            R.drawable.water_mark
                                        )
                                    val watermarkWidth = resource.width / 4
                                    val watermarkHeight =
                                        watermarkWidth * watermarkBitmap.height / watermarkBitmap.width
                                    val watermarkResized = Bitmap.createScaledBitmap(
                                        watermarkBitmap,
                                        watermarkWidth,
                                        watermarkHeight,
                                        false
                                    )
                                    var outputBitmap: Bitmap = Bitmap.createBitmap(
                                            resource.width,
                                            resource.height,
                                            Bitmap.Config.ARGB_8888
                                        )
                                    val canvas = Canvas(outputBitmap)
                                    canvas.drawBitmap(resource, 0f, 0f, null)
                                    canvas.drawBitmap(
                                        watermarkResized,
                                        10f,
                                        resource.height.toFloat() - watermarkHeight,
                                        null
                                    )
                                    it.setImageBitmap(outputBitmap)
                                    mBinding?.fmResultArtTvRegenerate?.isEnabled = true
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    // Do nothing
                                }
                            })
                    }
                    mBinding?.fmResultArtTvPrompt?.text =
                        arguments?.getString(KEY_PROMPT, "") ?: Strings.EMPTY
                    mBinding?.fmResultArtTvStyle?.text = arguments?.getString(KEY_STYLE, "")
                }

                else -> {}
            }
        }
    }

    override fun initActions() {
        mBinding?.fmResultArtImvBack?.setOnSingleClick {
            popBackStack()
        }

        mBinding?.fmResultArtTvRegenerate?.setOnSingleClick {
            mBinding?.fmResultArtCtLoading?.visible()
            mBinding?.fmResultArtLatLoading?.playAnimation()
            countDownTimer?.start()
            generateImage(
                prompt ?: "",
                modelId ?: "",
                width ?: DEFAULT_SIZE,
                height ?: DEFAULT_SIZE
            )
        }

        mBinding?.fmResultArtImvDownload?.setOnSingleClick {
            showPermission(CommonAction {
                activity?.let { it1 ->
                    WatermarkImageDownloader(it1).downloadImageWithWatermark(
                        currentUrlImage,
                        CommonAction {
                            mBinding?.fmResultArtLLDownloaded?.visible()
                            mBinding?.fmResultArtLatDownloaded?.playAnimation()
                            mBinding?.fmResultArtLatDownloaded?.addAnimatorListener(object :
                                Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {

                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    mBinding?.fmResultArtLLDownloaded?.gone()
                                }

                                override fun onAnimationCancel(animation: Animator) {

                                }

                                override fun onAnimationRepeat(animation: Animator) {

                                }

                            })
                        }, isShowImvMascot
                    )
                }
            })
        }
        mBinding?.fmResultArtImvShare?.setOnSingleClick {
            activity?.let { it1 ->
                WatermarkImageDownloader(it1).shareImageWithWatermark(
                    currentUrlImage,
                    isShowImvMascot
                )
            }
        }

    }


    override var initBackAction: Boolean = true

    override fun initData() {
    }


    private fun initTimer() {
        countDownTimer = object : CountDownTimer(TIMER_DEFAULT, 10) {
            override fun onTick(millisUntilFinished: Long) {
                val currentTimer = TIMER_DEFAULT - millisUntilFinished
                val seconds = currentTimer.toDouble() / 1000.0
                val formattedTime = "%.2fs".format(seconds)
                mBinding?.fmResultArtTvTimer?.text = formattedTime
                when {
                    currentTimer in (TIMER_COLOR + 1) until TIMER_QUALITY -> {
                        mBinding?.fmResultArtTvDes?.text = getString(R.string.adjusting_colors)
                    }

                    currentTimer in (TIMER_QUALITY + 1) until TIMER_ARTWORK -> {
                        mBinding?.fmResultArtTvDes?.text = getString(R.string.enhancing_quality)
                    }

                    currentTimer > TIMER_ARTWORK -> {
                        mBinding?.fmResultArtTvDes?.text = getString(R.string.creating_artwork)
                    }
                }
            }

            override fun onFinish() {
                val dialog = DialogFailArt.newInstance()
                dialog.onDialogDismissListener = {
                    popBackStack()
                }
                dialog.show(activity?.supportFragmentManager)
            }
        }
        countDownTimer?.start()

    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }


    private fun checkStoragePermissionNormal(): Boolean {
        return (context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } == PackageManager.PERMISSION_GRANTED)
    }

    private fun showPermission(action: CommonAction) {
        if (!checkStoragePermissionNormal() && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else
            action.action?.invoke()
    }

}
