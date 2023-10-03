package com.longkd.chatgpt_openai.feature.summary

import android.Manifest
import android.graphics.Rect
import android.os.*
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.MyApp
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentSummaryFileBinding
import com.longkd.chatgpt_openai.dialog.DialogStatusSummaryFile
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.home_new.gallery.ListGalleryFragment
import com.longkd.chatgpt_openai.feature.summary.history.HistorySummaryAdapter
import com.longkd.chatgpt_openai.feature.summary.history.HistorySummaryFileFragment
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.PermissionUtils
import com.longkd.chatgpt_openai.base.util.orZero
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFileFragment :
    BaseFragment<FragmentSummaryFileBinding>(R.layout.fragment_summary_file) {
    private val mViewModel: SummaryFileViewModel by viewModels()
    private var isSelectFile: Boolean? = true
    private var heightToolbar: Int? = null
    private var isFirstShowIntro: Boolean = true
    private lateinit var adapter: HistorySummaryAdapter
    private lateinit var summaryDtoDuplicate: SummaryHistoryDto
    private var mFileName: String? = null
    private var isSummaryOver: Boolean = false
    private var sizeFile: Int = 1
    private var isAdsRewarded: Boolean = false
    var onCallBackWhenPurchase: (() -> Unit) ? = null

    override fun initViews() {

        sizeFile = CommonSharedPreferences.getInstance().configSummaryFileSize

        activity?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            activity?.window?.decorView?.getWindowVisibleDisplayFrame(r)
            val height: Int =
                mBinding?.summaryFmContainerView?.context?.resources?.displayMetrics?.heightPixels.orZero()
            val diff = height - r.bottom - heightToolbar.orZero()
            if (diff < 0) heightToolbar = diff
            if (diff != 0) {
                if (mBinding?.summaryFmContainerView?.paddingBottom != diff) {
                    mBinding?.summaryFmContainerView?.setPadding(0, 0, 0, diff)
                }
            } else {
                if (mBinding?.summaryFmContainerView?.paddingBottom != 0) {
                    mBinding?.summaryFmContainerView?.setPadding(0, 0, 0, 0)
                }
            }
        }

        adapter = HistorySummaryAdapter(mutableListOf())
        mBinding?.summaryFmRcl?.adapter = adapter
        mBinding?.summaryFmRcl?.layoutManager = LinearLayoutManager(context)
        adapter.onClickItem = {
            showFullAdsWhenStartChat(it)
        }
        mBinding?.summaryFmBtnSend?.isEnabled = false
    }

    fun onRefeshData() {
        mViewModel?.getAllSummaryFile()
    }

    override fun initActions() {
        mBinding?.btnContinueSummary?.setOnSingleClick {
        }

        mBinding?.summaryFmBtnGoToChat?.setOnSingleClick {
            if (this::summaryDtoDuplicate.isInitialized) {
                showFullAdsWhenStartChat(summaryDtoDuplicate)
            }
        }

        mBinding?.summaryFmBtnCancel?.setOnSingleClick {
            mBinding?.summaryFmDuplica?.gone()
        }

        mBinding?.summaryFmEdt?.doOnTextChanged { text, _, _, _ ->
            mBinding?.summaryFmBtnSend?.isEnabled = text?.length.orZero() > 0
        }

        mBinding?.baseHeaderBtnLeft?.setOnSingleClick {
            handleOnBackPress()
        }

        mBinding?.summaryFmUploadFile?.setOnSingleClick {
            isSelectFile = true
            if (isSummaryOver) {
                return@setOnSingleClick
            }
            if (mViewModel?.checkTimesSummaryFile() != true) {
                DialogStatusSummaryFile.show(
                    childFragmentManager,
                    getString(R.string.str_out_of_times_summary, CommonSharedPreferences.getInstance().summaryConfigData),
                    true
                )
                return@setOnSingleClick
            }
            if (isFirstShowIntro) {
                DialogStatusSummaryFile.show(
                    childFragmentManager,
                    getString(R.string.str_intro_summary, sizeFile),
                    false
                ).apply {
                    onClickGotIt = {
                        if (!PermissionUtils.checkPermissionReadExternalStorage(MyApp.getInstance().applicationContext) && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(storgerPermissions)
                        } else selectPdf()
                    }
                }
                isFirstShowIntro = false
                return@setOnSingleClick
            }
            if (!PermissionUtils.checkPermissionReadExternalStorage(MyApp.getInstance().applicationContext) && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(storgerPermissions)
            } else selectPdf()
        }

        mBinding?.summaryFmUploadImage?.setOnSingleClick {
            isSelectFile = false
            if (isSummaryOver) {
                return@setOnSingleClick
            }
            if (mViewModel?.checkTimesSummaryFile() != true) {
                DialogStatusSummaryFile.show(
                    childFragmentManager,
                    getString(
                        R.string.str_out_of_times_summary,
                        CommonSharedPreferences.getInstance().summaryConfigData
                    ),
                    true
                )
                return@setOnSingleClick
            }
            selectImage()
        }

        mBinding?.summaryFmBtnOcr?.setOnSingleClick {
            showDialogOcr()
        }

        mBinding?.summaryFmTvMore?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                HistorySummaryFileFragment.newInstance(),
                HistorySummaryFileFragment::class.java.name
            )
        }

        mBinding?.summaryFmBtnSend?.setOnSingleClick {
            if (mViewModel?.checkTimesSummaryFile() != true) {
                DialogStatusSummaryFile.show(
                    childFragmentManager,
                    getString(
                        R.string.str_out_of_times_summary,
                        CommonSharedPreferences.getInstance().summaryConfigData
                    ),
                    true
                )
                return@setOnSingleClick
            }
            mFileName = getString(R.string.str_summary_text)
            mViewModel?.uploadSummaryText(mBinding?.summaryFmEdt?.text.toString(), context)
        }
    }


    fun resetNumberSummaryFile() {
        mViewModel?.resetNumberSummaryFile()
    }

    fun setOcr(value: String) {
        mBinding?.summaryFmEdt?.setText(value)
        mBinding?.summaryFmBtnSend?.isEnabled = true
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.firstOrNull { !it.value } ?: run {
            if (isSelectFile == true) selectPdf()
            else selectImage()
        }
    }

    private val storgerPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private fun selectImage() {
        mainFragment?.pushScreenWithAnimate(
            ListGalleryFragment.newInstance(Constants.GALLERY_TYPE.SUMMARY),
            ListGalleryFragment::class.java.name
        )
    }

    private fun selectPdf() {
        selectPdfActivityResultLauncher.launch("application/pdf")
    }

    private val selectPdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val maxSize = sizeFile * Constants.DEFAULT_SIZE_FILE_SUMMARY
            val mFileSummaryData = FileSummaryUtils.getSummaryFile(
                context,
                it,
                mViewModel?.getListSummaryHistory()?.value,
                maxSize
            )
            if (mFileSummaryData.first == StatusSummary.DUPLICATE.name) {
                mBinding?.summaryFmDuplica?.visible()
                mBinding?.summaryFmFileName?.text = mFileSummaryData.second?.fileName
                if (!this::summaryDtoDuplicate.isInitialized) {
                    summaryDtoDuplicate = SummaryHistoryDto(
                        filePaths = listOf(),
                        chatDetail = listOf(),
                        suggestList = listOf()
                    )
                }
                mFileSummaryData.second?.let { summaryDtoDuplicate = it }
            } else if (mFileSummaryData.first == StatusSummary.OUT_SIZE.name) {

                    DialogStatusSummaryFile.show(
                        childFragmentManager,
                        getString(R.string.str_intro_summary, sizeFile),
                        false
                    )

            } else if (mFileSummaryData.first != StatusSummary.ERROR.name) {
                mFileSummaryData.second?.let {
                    mViewModel?.uploadSummaryFile(it, context)
                }
            }
        }
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        (activity as MainActivity).apply {

                onBackPressed()

        }
        return true
    }

    override fun initData() {
        mViewModel?.callGetTimeStamp()
        mViewModel?.getAllSummaryFile()
        mViewModel?.resetNumberSummaryFile()
        observerData()
    }

    private fun observerData() {
        mViewModel?.getListSummaryHistory()?.observe(this) {
            if (it.isNullOrEmpty()) {
                mBinding?.summaryFmLlnNoHistory?.visible()
                mBinding?.summaryFmListSummary?.gone()
            } else {
                mBinding?.summaryFmListSummary?.visible()
                mBinding?.summaryFmLlnNoHistory?.gone()
                mBinding?.summaryFmTvMore?.visibility = if (it.size > 4) View.VISIBLE else View.GONE
                if (it.size > 4) {
                    adapter.setDatas(it.subList(0, 4))
                } else adapter.setDatas(it)
            }
        }

        mViewModel?.showLoading?.observe(this) {
            mBinding?.summaryFmTitleLoading?.text = mFileName
            displayViewLoading(it)
        }

        mViewModel?.summaryFileDto?.observe(this) {
            it?.let {
                mBinding?.summaryFmEdt?.text?.clear()
                showFullAdsWhenStartChat(it)
            } ?: run {
                mBinding?.llnTopContainer?.gone()
                mBinding?.fmChatBottomContainer?.gone()
            }
        }

        mViewModel?.timesSummary?.observe(this) {
            mBinding?.summaryFmTvTimesSummary1?.text = getString(R.string.left_s, it.toString())
            mBinding?.summaryFmTvTimesSummary2?.text = getString(R.string.left_s, it.toString())
        }

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

    fun showDetailChat(summaryHistoryDto: SummaryHistoryDto) {
        mainFragment?.pushScreenWithAnimate(
            ChatDetailFragment.newInstance(
                summaryData = summaryHistoryDto,
                chatId = -10L
            ),
            ChatDetailFragment::class.java.name
        )
    }
    private fun showFullAdsWhenStartChat(data: SummaryHistoryDto) {
        if (isSelectFile == null) {
            showDetailChat(data)
            return
        }
                    showDetailChat(data)
    }
    companion object {
        fun newInstance(): SummaryFileFragment {
            val args = Bundle()
            val fragment = SummaryFileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}