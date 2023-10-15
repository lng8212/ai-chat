/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.WatermarkImageDownloader
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.FragmentResultArtBinding
import com.longkd.chatgpt_openai.dialog.DialogFailArt
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

    }


    override fun initActions() {
        mBinding?.fmResultArtImvBack?.setOnSingleClick {
            popBackStack()
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
