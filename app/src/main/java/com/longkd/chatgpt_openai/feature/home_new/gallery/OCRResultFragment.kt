package com.longkd.chatgpt_openai.feature.home_new.gallery

import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.customview.CropImageView
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.FragmentOcrResultBinding
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.summary.SummaryFileFragment
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer

class OCRResultFragment: BaseFragment<FragmentOcrResultBinding>(R.layout.fragment_ocr_result),
    CropImageView.OnCropImageCompleteListener {
    private var isShowBackAds: Boolean = true

    override fun initViews() {
        mBinding?.fmOcrImage?.setOnCropImageCompleteListener(this)
        val uriString: String = arguments?.getString(KEY_URI_IMAGE) ?: ""
        val uriImage = Uri.parse(uriString)
        mBinding?.fmOcrImage?.setImageUriAsync(uriImage)

        mBinding?.fmOcrBtnRecognize?.setOnSingleClick {
            if (mBinding?.fmOcrBtnRecognize?.text != getString(R.string.str_txt_apply_text)) {
                mBinding?.fmOcrImage?.croppedImageAsync
                mBinding?.fmOcrLoading?.visible()
            } else {
                if (mBinding?.fmOcrResultText?.text.toString().isNullOrEmpty()) {
                    showDialogError(R.string.input_empty_error)
                    return@setOnSingleClick
                }
                val fragmentChat = (activity as MainActivity).supportFragmentManager.findFragmentByTag(ChatDetailFragment::class.java.name)
                val fragmentSummary = (activity as MainActivity).supportFragmentManager.findFragmentByTag(SummaryFileFragment::class.java.name)
                if (fragmentChat != null) {
                    (fragmentChat as ChatDetailFragment).setTextOCr(mBinding?.fmOcrResultText?.text.toString())
                    (activity as MainActivity).supportFragmentManager.popBackStack(ChatDetailFragment::class.java.name, 0)
                } else if(fragmentSummary != null) {
                     (fragmentSummary as SummaryFileFragment).setOcr(mBinding?.fmOcrResultText?.text.toString())
                     (activity as MainActivity).supportFragmentManager.popBackStack(SummaryFileFragment::class.java.name, 0)
                } else {
                    mainFragment?.pushScreenWithAnimate(
                        ChatDetailFragment.newInstance(resultOcr = mBinding?.fmOcrResultText?.text.toString()),
                        ChatDetailFragment::class.java.name
                    )
                }
            }
        }
    }

    override fun initActions() {
        mBinding?.fmOcrTvTitle?.setOnSingleClick {
            handleOnBackPress()
        }
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        if (!isShowBackAds) {
            activity?.onBackPressed()
        } else {
                activity?.onBackPressed()
        }
        return true
    }

    override fun initData() {

    }

    override fun onCropImageComplete(view: CropImageView?, result: CropImageView.CropResult?) {
        mBinding?.fmOcrImage?.gone()
        mBinding?.fmOcrResultImage?.let {
            Glide.with(this)
                .load(result?.bitmap)
                .into(it)
        }
        val image = InputImage.fromBitmap(result?.bitmap!!, 0)
        val recognizer: TextRecognizer = TextRecognition.getClient()
        recognizer.process(image).apply {
            addOnSuccessListener {
                mBinding?.fmOcrResultText?.text = it.text
                mBinding?.fmOcrResultText?.movementMethod = ScrollingMovementMethod()
                mBinding?.fmOcrLoading?.gone()
            }
        }

        mBinding?.fmOcrLlnResult?.visible()
        mBinding?.fmOcrBtnRecognize?.text = getString(R.string.str_txt_apply_text)
    }

    companion object {
        private const val KEY_URI_IMAGE = "key_uri_image"
        fun newInstance(value: String?): OCRResultFragment {
            val args = Bundle()
            value?.let { args.putString(KEY_URI_IMAGE, it) }
            val fragment = OCRResultFragment()
            fragment.arguments = args
            return fragment
        }
    }

}