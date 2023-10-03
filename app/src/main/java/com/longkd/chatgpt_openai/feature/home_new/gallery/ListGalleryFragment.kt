package com.longkd.chatgpt_openai.feature.home_new.gallery

import android.Manifest
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.MyApp
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentListGallaryBinding
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.base.model.SummaryData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ListGalleryFragment :
    BaseFragment<FragmentListGallaryBinding>(R.layout.fragment_list_gallary) {
    private val mViewModel: ListGalleryViewModel by viewModels()
    private var fromType: String? = null
    private lateinit var adapter: GalleryAdapter
    var onClickItem: ((uri: String?) -> Unit)? = null
    private var summaryContent: String? = null
    override fun initViews() {
        fromType = arguments?.getString(KEY_GALLERY)
        validatePermission()
        context?.let { mViewModel?.getAllImages(it) }
        when (fromType) {
            Constants.GALLERY_TYPE.OCR -> {
                mBinding?.galleryFmBtnUpload?.gone()
            }

            else -> {
                mBinding?.galleryFmBtnUpload?.visible()
            }
        }
        adapter = GalleryAdapter(
            mutableListOf(),
            fromType ?: "",
            object : ItemClickListener<SummaryData> {
                override fun onClick(item: SummaryData?, position: Int) {
                    if (fromType == Constants.GALLERY_TYPE.OCR) {
                        val uri = context?.let {
                            FileProvider.getUriForFile(
                                it,
                                activity?.packageName ?: "",
                                File(item?.uri)
                            )
                        }
                        pushScreenWithAnimate(
                            OCRResultFragment.newInstance(uri.toString()),
                            OCRResultFragment::class.java.name
                        )
                    } else {
                        item?.isSelect = !(item?.isSelect ?: false)
                        adapter.notifyDataSetChanged()
                        mBinding?.galleryFmBtnUpload?.text =
                            getString(R.string.str_upload_s_photo, sizeListSelect())
                    }
                }
            })
        mBinding?.galleryFmRcl?.adapter = adapter
        mBinding?.galleryFmRcl?.layoutManager =
            GridLayoutManager(context, Constants.LAYOUT_SPAN_COUNT_3)
    }

    private fun sizeListSelect() = adapter.listDto.filter { it.isSelect }.size

    override fun initActions() {
        mBinding?.galleryFmTvTitle?.setOnSingleClick {
            handleOnBackPress()
        }

        mBinding?.galleryFmBtnUpload?.setOnSingleClick {
            if (sizeListSelect() <= 0) return@setOnSingleClick

            if (sizeListSelect() > 3) {
                mBinding?.galleryFmToastView?.showText(
                    getString(R.string.str_upload_photo_msg),
                    backgroundColor = R.color.color_1A3239
                )
                return@setOnSingleClick
            }
            mViewModel?.showLoading(true)
            adapter.listDto.filter { it.isSelect }.forEachIndexed { index, data ->
                context?.let { context ->
                    val imageInput = InputImage.fromFilePath(
                        context,
                        FileProvider.getUriForFile(
                            context,
                            activity?.packageName ?: "",
                            File(data.uri)
                        )
                    )
                    val recognizer: TextRecognizer = TextRecognition.getClient()
                    recognizer.process(imageInput).apply {
                        addOnSuccessListener {
                            summaryContent?.plus("\n" + it.text)
                            if (index == sizeListSelect().minus(1)) {
                                mViewModel?.uploadSummaryText(
                                    summaryContent ?: "",
                                    context,
                                    adapter.listDto.filter { it.isSelect })
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity?.let {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context?.packageName, null)
                        addCategory(CATEGORY_DEFAULT)
                        addFlags(FLAG_ACTIVITY_NEW_TASK)
                        addFlags(FLAG_ACTIVITY_NO_HISTORY)
                        addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    }
                    startActivity(intent)
                    return
                }
            }
            if (!PermissionUtils.checkPermissionReadExternalStorageTargetSDK33(MyApp.getInstance().applicationContext)) {
                requestPermissionLauncherSDK33.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionUtils.checkPermissionReadExternalStorage(MyApp.getInstance().applicationContext)) {
                requestPermissionLauncher.launch(storgerPermissions)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (!it.value) popBackStack()
            else context?.let { mViewModel?.getAllImages(it) }
        }
    }

    private val storgerPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val requestPermissionLauncherSDK33 =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (PermissionUtils.checkPermissionReadExternalStorageTargetSDK33(MyApp.getInstance().applicationContext)) {
                context?.let { mViewModel?.getAllImages(it) }
            } else popBackStack()
        }


    override var initBackAction: Boolean = true

    override fun initData() {
        mViewModel?.getImageList()?.observe(this) {
            if (it.isNullOrEmpty()) {
                mBinding?.galleryFmNoImage?.visible()
                mBinding?.galleryFmBtnUpload?.gone()
                mBinding?.galleryFmRcl?.gone()
            } else {
                adapter.setDatas(it.toMutableList())
                mBinding?.galleryFmNoImage?.gone()
                mBinding?.galleryFmRcl?.visible()
                if (fromType == Constants.GALLERY_TYPE.SUMMARY) mBinding?.galleryFmBtnUpload?.visible()
            }
        }

        mViewModel?.showLoading?.observe(this) {
            mBinding?.summaryFmTitleLoading?.text = getString(R.string.str_uploading)
            displayViewLoading(it)
        }

        mViewModel?.summaryFileDto?.observe(this) {
            it?.let {
                showChatDateil(data = it)
            } ?: run {
                mBinding?.llnViewContainer?.gone()
            }
        }
    }

    private fun showChatDateil(data: SummaryHistoryDto) {
        pushScreenWithAnimate(
            ChatDetailFragment.newInstance(summaryData = data, chatId = -10L),
            ChatDetailFragment::class.java.name
        )
    }

    private fun displayViewLoading(isLoading: Boolean?) {
        if (isLoading == true) {
            mBinding?.summaryFmLoading?.playAnimation()
            mBinding?.summaryFmShowLoadingView?.visible()
        } else {
            mBinding?.summaryFmLoading?.cancelAnimation()
            mBinding?.summaryFmShowLoadingView?.gone()
        }
    }

    override fun handleOnBackPress(): Boolean {
        activity?.onBackPressed()
        return true
    }

    companion object {
        private const val KEY_GALLERY = "key_gallery"
        fun newInstance(fromType: String): ListGalleryFragment {
            val args = Bundle()
            args.putString(KEY_GALLERY, fromType)
            val fragment = ListGalleryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}