package com.longkd.chatgpt_openai.feature.art

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.WatermarkImageDownloader
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.FragmentResultArtBinding
import com.longkd.chatgpt_openai.dialog.DialogFailArt
import com.longkd.chatgpt_openai.open.State
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ResultArtFragment : BaseFragment<FragmentResultArtBinding>(R.layout.fragment_result_art) {
    private val mViewModel: ResultArtViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null

    private var url: String? = null
    private var prompt: String? = null
    private var size: String? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) Toast.makeText(context, R.string.grantfailed, Toast.LENGTH_SHORT).show()

        }

    companion object {
        private const val TIMER_DEFAULT = 160000L
        private const val TIMER_COLOR = 10000L
        private const val TIMER_QUALITY = 25000L
        private const val TIMER_ARTWORK = 40000L

        fun newInstance(
            prompt: String? = null,
            size: String? = null,
        ): ResultArtFragment {
            val args = Bundle()
            args.putString(GenerateArtFragment.IMAGE_CONTENT, prompt)
            size?.let { args.putString(GenerateArtFragment.IMAGE_SIZE, size) }
            val fragment = ResultArtFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        initTimer()
        prompt = arguments?.getString(GenerateArtFragment.IMAGE_CONTENT, "") ?: Strings.EMPTY
        size = arguments?.getString(GenerateArtFragment.IMAGE_SIZE)
        mViewModel.getImage(prompt ?: "", size ?: "0")
    }


    override fun initActions() {
        mBinding?.fmResultArtImvBack?.setOnSingleClick {
            popBackStack()
        }

        mBinding?.fmResultArtTvRegenerate?.setOnSingleClick {
            mBinding?.fmResultArtCtLoading?.visible()
            mBinding?.fmResultArtLatLoading?.playAnimation()
            countDownTimer?.start()
            mViewModel.getImage(prompt ?: "", size ?: "0")
        }

        mBinding?.fmResultArtImvDownload?.setOnSingleClick {
            showPermission {
                lifecycleScope.launch(Dispatchers.Default) {
                    val data = WatermarkImageDownloader().load(requireContext(), url ?: "")
                    if (data != null) {
                        withContext(Dispatchers.Main) {
                            mBinding?.fmResultArtLLDownloaded?.visible()
                            mBinding?.fmResultArtLatDownloaded?.playAnimation()
                        }
                        WatermarkImageDownloader().saveMediaToStorage(requireContext(), data) {
                            launch(Dispatchers.Main) {
                                mBinding?.fmResultArtLatDownloaded?.addAnimatorListener(object :
                                    Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator) {

                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                        mBinding?.fmResultArtLLDownloaded?.gone()
                                        popBackStack()
                                    }

                                    override fun onAnimationCancel(animation: Animator) {

                                    }

                                    override fun onAnimationRepeat(animation: Animator) {

                                    }

                                })

                            }
                        }
                    }
                }
            }
        }
        mBinding?.fmResultArtImvShare?.setOnSingleClick {
            activity?.let {
                WatermarkImageDownloader().shareImage(
                    url ?: "",
                    requireContext()
                )
            }
        }

    }


    override var initBackAction: Boolean = true

    @SuppressLint("CheckResult")
    override fun initData() {
        lifecycleScope.launch {
            mViewModel.imageURL.collect { state ->
                when (state) {
                    is State.Loading -> {
                    }

                    is State.Success -> {
                        url = state.data?.data?.get(0)?.url ?: ""
                        mBinding?.fmResultArtCtLoading?.gone()
                        mBinding?.fmResultArtLatLoading?.cancelAnimation()
                        countDownTimer?.cancel()
                        mBinding?.fmResultArtTvPrompt?.text = prompt
                        mBinding?.fmResultArtImv?.let {

                            Glide.with(this@ResultArtFragment)
                                .load(url)
                                .placeholder(R.drawable.img_default_art)
                                .into(it)
                        }
                    }

                    is State.Error -> {
                        mBinding?.fmResultArtLatLoading?.cancelAnimation()
                        countDownTimer?.cancel()
                        val dialog = DialogFailArt.newInstance()
                        dialog.onDialogDismissListener = {
                            popBackStack()
                        }
                        dialog.show(activity?.supportFragmentManager)
                    }

                    else -> {}
                }
            }
        }
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

    private fun showPermission(onAction: () -> Unit) {
        if (!checkStoragePermissionNormal() && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else
            onAction.invoke()
    }

}
