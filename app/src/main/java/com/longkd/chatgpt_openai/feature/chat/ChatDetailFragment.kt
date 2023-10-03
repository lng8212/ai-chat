package com.longkd.chatgpt_openai.feature.chat

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Network
import android.os.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.longkd.chatgpt_openai.BR
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.base.widget.header.BaseHeaderView
import com.longkd.chatgpt_openai.databinding.ChatDetailsFragmentBinding
import com.longkd.chatgpt_openai.dialog.*
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.ShareDataViewModel
import com.longkd.chatgpt_openai.feature.chat.viewholder.ChatDetailAdapter
import com.longkd.chatgpt_openai.feature.chat.viewholder.SuggestListAdapter
import com.longkd.chatgpt_openai.feature.connect.FragmentLostInternet
import com.longkd.chatgpt_openai.feature.history.FragmentHistory
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import com.longkd.chatgpt_openai.feature.summary.DetailFileSummaryFragment
import com.longkd.chatgpt_openai.feature.summary.SummaryFileFragment
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ErrorType
import com.longkd.chatgpt_openai.base.model.ResultDataDto
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.NetworkUtil
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.orZero
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class ChatDetailFragment :
    BaseFragment<ChatDetailsFragmentBinding>(R.layout.chat_details_fragment) {
    private val mViewModel: HomeViewModel by viewModels()
    private var mAdapter: ChatDetailAdapter? = null
    private var mRequestSpeech: ActivityResultLauncher<Intent>? = null
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var mEnableShowStopAnimate = false
    private var mIsNewChatAction = false
    private var isNewChat = true
    private val mShareDataViewModel: ShareDataViewModel by activityViewModels()
    private var mViewTreeObserver: OnGlobalLayoutListener? = null
    private var onNetworkConnectivityCallback: ConnectivityManager.NetworkCallback? = null
    private var autoSpeak = false
    private var mIsStartMore = false
    private var mIsCallMore = false
    private var textMessaeMore = ""
    private var chatId: Long = -1L
    private var limitChar: Int? = null
    private var isCheckDuplicateRegenerate: Boolean = false
    private var isFromHistory: Boolean? = null
    private var summaryData: SummaryHistoryDto? = null
    private lateinit var suggestAdapter: SuggestListAdapter
    private var isClickSuggestQuestion: Boolean = true
    private val mHandler: Handler? by lazy {
        Handler(Looper.getMainLooper())
    }
    private var isScrollRcl: Boolean = true
    private var mRunner = kotlinx.coroutines.Runnable {
        kotlin.runCatching {
            if (isScrollRcl) mBinding?.chatFmRcv?.scrollBy(0, 100)
        }
    }
    var mUserMode: String = ""
    var mSpeech: TextToSpeech? = null
    private var currentDto: ChatBaseDto? = null
    var onCallBackWhenPurchase: (() -> Unit)? = null

    companion object {
        const val FROM_CHAT_STYLE = "FROM_CHAT_STYLE"
        const val CHAT_MODE = "CHAT_MODE"
        const val TOPIC = "TOPIC"
        const val CHAT_ID = "CHAT_ID"
        private const val KEY_QUESTION_TOPIC = "key_question_topic"
        private const val KEY_FROM_HISTORY = "key_from_history"
        private const val DELAY_SCROLL = 100L
        private const val DELAY_CALL_FUN_REGENERATE = 500L
        private const val KEY_RESULT_OCR = "key_result_ocr"
        private const val KEY_FROM_SUMMARY_FILE = "key_from_summary_file"
        private const val KEY_TEXT_COPY = "KEY_TEXT_COPY"
        fun newInstance(
            fromChatStyle: Boolean = false,
            chatMode: Int = -1,
            chatId: Long = -1,
            topicType: TopicDto? = null,
            fromHistory: Boolean? = false,
            resultOcr: String? = null,
            summaryData: SummaryHistoryDto? = null,
            textCopy: String? = null
        ): ChatDetailFragment {
            val args = Bundle()
            val fragment = ChatDetailFragment()
            args.putBoolean(FROM_CHAT_STYLE, fromChatStyle)
            args.putInt(CHAT_MODE, chatMode)
            args.putLong(CHAT_ID, chatId)
            args.putParcelable(TOPIC, topicType)
            args.putBoolean(KEY_FROM_HISTORY, fromHistory ?: false)
            summaryData?.let { args.putParcelable(KEY_FROM_SUMMARY_FILE, it) }
            resultOcr?.let { args.putString(KEY_RESULT_OCR, it) }
            textCopy?.let { args.putString(KEY_TEXT_COPY, it) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun initHeaderView(): BaseHeaderView? {
        return mBinding?.chatFmHeaderView
    }

    private var heightToolbar: Int? = null
    override fun initViews() {
        chatId = arguments?.getLong(CHAT_ID, -1L) ?: -1L
        isFromHistory = arguments?.getBoolean(KEY_FROM_HISTORY)
        mUserMode = CommonSharedPreferences.getInstance().userRate
        autoSpeak =
            CommonSharedPreferences.getInstance().getBoolean(Constants.ENABLE_AUTO_SPEAK, false)
        val themeColor: Int = R.color.color_chat_style_1
        CommonSharedPreferences.getInstance().apply {
            this.titleAppChat.let { if (!it.isNullOrBlank()) mHeaderView?.setTitle(it) }
            limitChar = 1000
        }
        mHeaderView?.apply {
            setTitleStart(CommonAction {
                handleSelectModelChat()
            }, R.drawable.ic_arrow_down)
            setLeftAction(CommonAction {
                handleOnBackPress()
            })

            setRightAction(
                CommonAction {
                    if (autoSpeak) {
                        mSpeech?.stop()
                        autoSpeak = false
                        CommonSharedPreferences.getInstance()
                            .putBoolean(Constants.ENABLE_AUTO_SPEAK, false)
                        mHeaderView?.setRightIcon(R.drawable.ic_muted_speaker)
                    } else {
                        autoSpeak = true
                        CommonSharedPreferences.getInstance()
                            .putBoolean(Constants.ENABLE_AUTO_SPEAK, true)
                        mHeaderView?.setRightIcon(R.drawable.ic_speaker)
                    }
                },
                if (autoSpeak)
                    R.drawable.ic_speaker
                else
                    R.drawable.ic_muted_speaker
            )
        }

        activity?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            activity?.window?.decorView?.getWindowVisibleDisplayFrame(r)
            val height: Int =
                mBinding?.llnViewContainer?.context?.resources?.displayMetrics?.heightPixels.orZero()
            val diff = height - r.bottom - heightToolbar.orZero()
            if (diff < 0) heightToolbar = diff
            if (diff != 0) {
                if (mBinding?.llnViewContainer?.paddingBottom != diff) {
                    mBinding?.llnViewContainer?.setPadding(0, 0, 0, diff)
                }
            } else {
                if (mBinding?.llnViewContainer?.paddingBottom != 0) {
                    mBinding?.llnViewContainer?.setPadding(0, 0, 0, 0)
                }
            }
        }

        mAdapter = ChatDetailAdapter(arrayListOf(), false, themeColor,
            object : ItemClickListener<ChatDetailDto> {
                override fun onClick(item: ChatDetailDto?, position: Int) {
                    kotlin.runCatching {
                        if (context != null && item?.message != null) {
                            copyText(context!!, item.message)
                        }
                    }
                }
            })
        mAdapter?.mOnAnimateFinished = {
            mViewModel?.updateChatNumber()
            mAdapter?.mEnableAnimateText = true
            mBinding?.chatFmTvStopAnimateText?.gone()
            mHeaderView?.setCustomBtn(R.drawable.ic_history_chat, true)
            mBinding?.chatFmEdt?.isEnabled = true
            isClickSuggestQuestion = true
            mHandler?.removeCallbacks(mRunner)
            mSpeech?.stop()
            mRunner.run()
            isScrollRcl = false
        }

        mAdapter?.onClickRegenerate = { data ->
            if (mBinding?.chatFmTvStopAnimateText?.visibility == View.GONE) {
                lifecycleScope.launchWhenStarted {
                    if (isCheckDuplicateRegenerate) return@launchWhenStarted
                    isCheckDuplicateRegenerate = true
                    handleCallChatGPTWithTopic(true)
                    delay(DELAY_CALL_FUN_REGENERATE)
                    isCheckDuplicateRegenerate = false
                }
            }
        }

        mAdapter?.onClickSeeMore = { }
        mBinding?.chatFmRcv?.adapter = mAdapter
        mBinding?.chatFmRcv?.itemAnimator = DefaultItemAnimator()
        mRequestSpeech = activity?.activityResultRegistry?.let { activityResultRegistry ->
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                activityResultRegistry
            ) { result ->
                result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
                    ?.let {
                        mBinding?.chatFmEdt?.setText(it)
                        if (!it.isEmpty()) mBinding?.chatFmBtnSend?.isEnabled = true
                    }
            }
        }
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            1000
        )
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            1000
        )
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
            1500
        )
        mViewTreeObserver = OnGlobalLayoutListener {
            val r = Rect()
            mBinding?.root?.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = mBinding?.root?.rootView?.height ?: 0
            val keypadHeight: Int = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15 && mBinding?.chatFmEdt?.text.isNullOrEmpty()) {
                scrollToLastPosition((mAdapter?.listData?.size ?: 1).minus(1))
            }
        }

        mBinding?.root?.viewTreeObserver?.addOnGlobalLayoutListener(mViewTreeObserver)
        mBinding?.chatFmBtnSend?.isEnabled = false
        arguments?.getParcelable<SummaryHistoryDto>(KEY_FROM_SUMMARY_FILE)?.let {
            val isShowToolTip = CommonSharedPreferences.getInstance().firstDisplayToolTipSummary
            if (isShowToolTip) {
                mBinding?.fmChatLlnViewToolTip?.visible()
                CommonSharedPreferences.getInstance().firstDisplayToolTipSummary = false
            }
            summaryData = it
            mBinding?.chatFmLlnFile?.visible()
            mBinding?.chatFmNameFile?.text = it.fileName ?: "${it.filePaths.size} images"
            mBinding?.chatFmHeaderView?.gone()
            if (!it.suggestList.isNullOrEmpty()) {
                suggestAdapter =
                    SuggestListAdapter(mutableListOf(), object : ItemClickListener<String> {
                        override fun onClick(item: String?, position: Int) {
                            item?.let {
                                if (isClickSuggestQuestion) {
                                    actionSend(true, it)
                                    suggestAdapter.updateView(it)
                                    isClickSuggestQuestion = false
                                }
                            }
                        }
                    })
                suggestAdapter.setDatas(it.suggestList!!.toMutableList())
                mBinding?.chatFmRclSuggest?.adapter = suggestAdapter
                mBinding?.chatFmRclSuggest?.visible()
            }
        }
        initActionHistory()
    }

    private fun handleSelectModelChat() {
        if (mBinding?.fmChatLlnSelectModel?.root?.visibility == View.GONE) {
            mBinding?.fmChatLlnSelectModel?.root?.visible()
        } else mBinding?.fmChatLlnSelectModel?.root?.gone()
        handleDisplaySelectModelChat()
    }

    private fun handleDisplaySelectModelChat() {
        var modelChat = mViewModel?.getModelChat()
        mBinding?.fmChatLlnSelectModel?.titleGpt4?.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            0,
            0
        )
        if (modelChat == Constants.MODEL_CHAT.GPT_3_5) {
            mHeaderView?.setTitle("Chat GPT 3.5")
            mBinding?.fmChatLlnSelectModel?.llnView35?.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
            mBinding?.fmChatLlnSelectModel?.llnViewGpt4?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
        } else {
            mHeaderView?.setTitle("Chat GPT 4")
            mBinding?.fmChatLlnSelectModel?.llnView35?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
            mBinding?.fmChatLlnSelectModel?.llnViewGpt4?.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
        }
        mBinding?.fmChatLlnSelectModel?.llnView35?.setOnSingleClick {
            modelChat = Constants.MODEL_CHAT.GPT_3_5
            mBinding?.fmChatLlnSelectModel?.llnView35?.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
            mBinding?.fmChatLlnSelectModel?.llnViewGpt4?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
        }

        mBinding?.fmChatLlnSelectModel?.llnViewGpt4?.setOnSingleClick {
            modelChat = Constants.MODEL_CHAT.GPT_4
            mBinding?.fmChatLlnSelectModel?.llnView35?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
            mBinding?.fmChatLlnSelectModel?.llnViewGpt4?.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
        }

        mBinding?.fmChatLlnSelectModel?.btnContinue?.setOnSingleClick {
            mBinding?.fmChatLlnSelectModel?.root?.gone()
            mHeaderView?.setTitle(if (modelChat == Constants.MODEL_CHAT.GPT_3_5) "Chat GPT 3.5" else "Chat GPT 4")
            mViewModel?.setModelChat(modelChat ?: Constants.MODEL_CHAT.GPT_3_5)
        }
    }

    private fun initActionHistory() {
        if (isFromHistory != true) {
            val historyFragmen = FragmentHistory.newInstance().apply {
                onBackPress = {
                    mViewModel?.getMessNumber()
                }
            }

            mHeaderView?.showCustomBtn(
                CommonAction {
                    this@ChatDetailFragment.pushScreenWithAnimate(
                        historyFragmen,
                        FragmentHistory::class.java.name
                    )
                },
                R.drawable.ic_history_chat
            )
        }
    }

    private fun updateChatHis() {
        mViewModel?.getChatDto(chatId)?.observe(this) {
            mViewModel?.initViewChatHis(it)
        }
    }

    private var isActionSend = false
    private fun actionSend(isSummaryChat: Boolean = false, inputSummaryText: String = "") {
//        mViewModel?.callGetTimeStamp()
        val inputText = mBinding?.chatFmEdt?.text?.toString() ?: Strings.EMPTY
        UtilsApp.hideKeyboard(activity)
        if (!NetworkUtil.isInternetAvailable(requireContext())) {
            pushScreen(FragmentLostInternet.newInstance(), FragmentLostInternet::class.java.name)
        }
        if (inputText.isBlank() && !isSummaryChat) {
            showDialogError(R.string.input_empty_error)
            return
        }
        mBinding?.chatFmRcv?.visible()
        mBinding?.chatFmEdt?.setText("")
        mBinding?.chatFmEdt?.isEnabled = false
        mBinding?.chatFmBtnSend?.isEnabled = false
        showLoading()
        isActionSend = true
        mIsStartMore = false
        mHeaderView?.setCustomBtn(R.drawable.ic_history_chat, false)
        mViewModel?.completeQRetrofitHandler(
            context,
            if (isSummaryChat) inputSummaryText else inputText.trim(),
            getStringRes(R.string.something_error),
            isNewChat && chatId == -1L,
            topicType = (arguments?.getParcelable(TOPIC) as? TopicDto)?.topicType ?: -1,
            fromSummary = summaryData
        )?.observe(this) { dto ->
            isNewChat = false
            handleResultDtoChat(dto)
        }
    }

    fun copyText(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, getStringRes(R.string.toast_copy_text), Toast.LENGTH_SHORT).show()
    }

    private fun handleCallChatGPTWithTopic(isRegenerate: Boolean = false) {
        mViewModel?.callGetTimeStamp()
        if (!NetworkUtil.isInternetAvailable(requireContext())) {
            pushScreen(FragmentLostInternet.newInstance(), FragmentLostInternet::class.java.name)
            return
        }
        showLoading()
        isActionSend = true
        mIsStartMore = false
        mHeaderView?.setCustomBtn(R.drawable.ic_history_chat, false)
        mViewModel?.completeChatWithTopic(
            context,
            getStringRes(R.string.something_error),
            isRegenerate
        )?.observe(this) { dto ->
            isNewChat = false
            handleResultDtoChat(dto)
        }
    }

    private fun handleResultDtoChat(dto: ResultDataDto) {
        when (dto) {
            is ResultDataDto.Error -> {
                if (dto.errorType == ErrorType.UNKNOWN) {
                    isActionSend = true
                }
                hideLoading()
            }

            else -> {
                mIsCallMore = (dto as? ResultDataDto.Success)?.mIsCallMore ?: false
                isActionSend = true
                mEnableShowStopAnimate = true
                mAdapter?.mEnableAnimateText = true
                mIsNewChatAction = true
                mBinding?.chatFmTvStopAnimateText?.visible()
                if (autoSpeak) {
                    val params = Bundle()
                    params.putString(
                        TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                        (dto as? ResultDataDto.Success)?.result
                    )
                    mSpeech?.speak(
                        (dto as? ResultDataDto.Success)?.result,
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        (dto as? ResultDataDto.Success)?.result
                    )
                } else {
                    mAdapter?.setCharacterDelay(30L)
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        isScrollRcl = true
                        val resultLength = (dto as? ResultDataDto.Success)?.result?.length ?: 0
                        val loop = (resultLength * 50) / 2000 + 1
                        for (i in 0 until loop) {
                            delay(2000)
                            activity?.runOnUiThread {
                                mHandler?.post(mRunner)
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initActions() {
        mBinding?.chatFmBtnSend?.setOnSingleClick {
            actionSend()
        }

        onNetworkConnectivityCallback = object :
            ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                pushScreen(
                    FragmentLostInternet.newInstance(),
                    FragmentLostInternet::class.java.name
                )
            }
        }
        onNetworkConnectivityCallback?.let {
        }

        mBinding?.chatFmTvStopAnimateText?.setOnSingleClick {
            mViewModel?.updateChatNumber()
            mBinding?.chatFmTvStopAnimateText?.gone()
            mHeaderView?.setCustomBtn(R.drawable.ic_history_chat, true)
            isClickSuggestQuestion = true
            mSpeech?.stop()
            mAdapter?.stopAnimateText()
            mHandler?.removeCallbacks(mRunner)
            isScrollRcl = false
            scrollToLastPosition(currentDto?.chatDetail?.size?.minus(1) ?: 0)
            hideLoading()
        }
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
        mBinding?.chatFmRcv?.layoutAnimation = controller
        mBinding?.chatFmRcv?.setOnTouchListener { v, event ->
            UtilsApp.hideKeyboard(activity)
            return@setOnTouchListener false
        }
        mBinding?.chatFmEdt?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                actionSend()
            }
            return@setOnEditorActionListener false
        }

        mBinding?.chatFmEdt?.filters =
            arrayOf<InputFilter>(LengthFilter(limitChar ?: Constants.LIMIT_INPUT_CHAT))


        mBinding?.chatFmBtnOcr?.setOnSingleClick {
            if (mBinding?.chatFmEdt?.isEnabled == false) return@setOnSingleClick
            showDialogOcr()
        }

        mBinding?.chatFmCloseFile?.setOnSingleClick {
            customBackPressChat()
        }

        mBinding?.chatFmNameFile?.setOnSingleClick {
            pushScreenWithAnimate(
                DetailFileSummaryFragment.newInstance(
                    summaryData,
                    mBinding?.chatFmNameFile?.text.toString()
                ),
                DetailFileSummaryFragment::class.java.name
            )
        }

        mBinding?.fmChatLlnViewToolTip?.setOnSingleClick {
            mBinding?.fmChatLlnViewToolTip?.gone()
        }

        mBinding?.fmChatShowDetailSummary?.setOnSingleClick {
            mBinding?.fmChatLlnViewToolTip?.gone()
            pushScreenWithAnimate(
                DetailFileSummaryFragment.newInstance(
                    summaryData,
                    mBinding?.chatFmNameFile?.text.toString()
                ),
                DetailFileSummaryFragment::class.java.name
            )
        }
    }

    @SuppressLint("SetTextI18n", "StringFormatMatches", "SuspiciousIndentation")
    override fun initData() {
        mBinding?.setVariable(BR.mViewModel, mViewModel)
        handleDisplaySelectModelChat()
        updateChatHis()
        mViewModel?.initChatSummary(summaryData)
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading()
            withContext(Dispatchers.Default) {
                delay(500)
            }
            hideLoading()
            mViewModel?.getCurrentDto()?.observe(this@ChatDetailFragment) {
                currentDto = it
                if (isNewChat || isActionSend || !autoSpeak) {
                    mAdapter?.updateData(it.chatDetail)
                    isActionSend = false
                    kotlin.runCatching {
                        mBinding?.chatFmRcv?.setItemViewCacheSize(it.chatDetail.size - 1)
                    }
                    if (mIsNewChatAction) {
                        scrollToLastPosition(it.chatDetail.size.minus(1))
                    } else {
                        mBinding?.chatFmRcv?.post {
                            mBinding?.chatFmRcv?.postDelayed({
                                kotlin.runCatching {
                                    mBinding?.chatFmRcv?.scrollToPosition(it.chatDetail.size.minus(1))
                                }
                            }, DELAY_SCROLL)
                        }
                    }
                }
            }
            arguments?.getString(KEY_TEXT_COPY)?.let {
                mViewModel?.setTextInputCopy(true)
                mViewModel?.initChat(
                    arguments?.getInt(TOPIC, -1) ?: -1,
                    it,
                    "at"
                )
            } ?: run {
                if (chatId == -1L) {
                    mViewModel?.initChat(
                        arguments?.getInt(TOPIC, -1) ?: -1,
                        getStringRes(R.string.str_title_start_chat), "at"
                    )
                }
            }

            arguments?.getString(KEY_RESULT_OCR)?.let {
                mBinding?.chatFmEdt?.setText(it)
                mBinding?.chatFmEdt?.isEnabled = true
            }

            mViewModel?.mMessageMore?.observe(this@ChatDetailFragment) { message ->
                val timer = object : CountDownTimer(80000, 100) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (autoSpeak) {
                            if (mSpeech?.isSpeaking == false) {
                                mIsStartMore = true
                                textMessaeMore = message
                                if (mBinding?.chatFmTvStopAnimateText?.isVisible == true) {
                                    val params = Bundle()
                                    params.putString(
                                        TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                                        message
                                    )
                                    mSpeech?.speak(
                                        message,
                                        TextToSpeech.QUEUE_FLUSH,
                                        params,
                                        message
                                    )
                                }
                                cancel()
                            }
                        }
                    }

                    override fun onFinish() {
                    }
                }
                timer.start()
            }
        }
        mSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                mSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        activity?.runOnUiThread {
                            if (mIsStartMore) {
                                mAdapter?.updateTextMore(textMessaeMore)
                                mIsStartMore = false
                                return@runOnUiThread
                            }
                            currentDto?.let {
                                mAdapter?.updateData(it.chatDetail)
                                kotlin.runCatching {
                                    mBinding?.chatFmRcv?.setItemViewCacheSize(it.chatDetail.size - 1)
                                }
                                if (mIsNewChatAction) {
                                    scrollToLastPosition(it.chatDetail.size.minus(1))
                                } else {
                                    mBinding?.chatFmRcv?.post {
                                        mBinding?.chatFmRcv?.postDelayed({
                                            kotlin.runCatching {
                                                mBinding?.chatFmRcv?.scrollToPosition(it.chatDetail.size - 1)
                                            }
                                        }, DELAY_SCROLL)
                                    }
                                }
                            }
                        }
                    }

                    override fun onDone(utteranceId: String) {
                    }

                    override fun onError(utteranceId: String) {
                    }

                    override fun onRangeStart(
                        utteranceId: String,
                        start: Int,
                        end: Int,
                        frame: Int
                    ) {
                        mAdapter?.updateText(utteranceId.substring(0, end), end)
                    }

                    override fun onStop(utteranceId: String, interrupted: Boolean) {
                    }
                })
            }

        }
        mSpeech?.setSpeechRate(1.2f)
        kotlin.runCatching {
            mSpeech?.language = Locale.getDefault()
        }

        mViewModel?.callChatWithTopic?.observe(this) {
            handleCallChatGPTWithTopic()
        }
        mViewModel?.inputEdtChat?.observe(this) {
            mBinding?.chatFmBtnSend?.isEnabled = !it.isNullOrBlank()
            mBinding?.chatFmLimitText?.text =
                getString(R.string.str_limit_text_chat, it.length, limitChar.toString())
        }
    }

    override fun handleOnBackPress(): Boolean {
        customBackPressChat()
        return true
    }

    private fun customBackPressChat() {
        (activity as? MainActivity)?.apply {
            onRefeshDataSummary()
            val summaryFragment =
                supportFragmentManager.findFragmentByTag(SummaryFileFragment::class.java.name)
            if (summaryFragment != null) {
                (summaryFragment as SummaryFileFragment).resetNumberSummaryFile()
                popBackStack(SummaryFileFragment::class.java.name, 0)
            } else {
                popBackstackAllFragment()
            }
        } ?: run {
            popBackstackAllFragment()
        }
    }

    override fun onPause() {
        mSpeech?.stop()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        onNetworkConnectivityCallback?.let {
        }
        UtilsApp.hideKeyboard(activity)
        mShareDataViewModel.notifyUpdateChatHistory()
    }

    override var initBackAction: Boolean = true

    override fun showLoading() {
        mBinding?.chatFmBtnSend?.isEnabled = false
        mBinding?.chatFmEdt?.isEnabled = false
    }

    override fun hideLoading() {
        mBinding?.chatFmEdt?.isEnabled = true
    }

    private fun scrollToLastPosition(position: Int) {
        mBinding?.chatFmRcv?.post {
            mBinding?.chatFmRcv?.postDelayed({
                kotlin.runCatching {
                    mBinding?.chatFmRcv?.smoothScrollToPosition(position)
                }
            }, DELAY_SCROLL)
        }
    }

    fun setTextOCr(value: String) {
        mBinding?.chatFmEdt?.setText(value)
        mBinding?.chatFmEdt?.isEnabled = true
    }
}