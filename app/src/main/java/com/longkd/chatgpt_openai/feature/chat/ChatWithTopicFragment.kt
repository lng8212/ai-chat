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
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.base.widget.header.BaseHeaderView
import com.longkd.chatgpt_openai.databinding.FragmentChatWithTopicBinding
import com.longkd.chatgpt_openai.dialog.*
import com.longkd.chatgpt_openai.feature.ShareDataViewModel
import com.longkd.chatgpt_openai.feature.chat.viewholder.ChatDetailAdapter
import com.longkd.chatgpt_openai.feature.connect.FragmentLostInternet
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class ChatWithTopicFragment: BaseFragment<FragmentChatWithTopicBinding>(R.layout.fragment_chat_with_topic) {
    private val mViewModel: HomeViewModel by viewModels()
    private var mAdapter: ChatDetailAdapter? = null
    private var mRequestSpeech: ActivityResultLauncher<Intent>? = null
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var mIsNewChatAction = false
    private var isNewChat = true
    private val mShareDataViewModel: ShareDataViewModel by activityViewModels()
    private var onNetworkConnectivityCallback: ConnectivityManager.NetworkCallback? = null
    private var autoSpeak = false
    private var mIsStartMore = false
    private var textMessaeMore = ""
    private var limitChar: Int ? = null
    private var isCheckDuplicateRegenerate: Boolean = false
    private var isClickSuggestQuestion: Boolean = true
    private var heightToolbar: Int ? = null
    private var mListSuggest: ArrayList<String> = arrayListOf()
    private val mHandler: Handler? by lazy {
        Handler(Looper.getMainLooper())
    }

    private val mRunner: Runnable by lazy {
        Runnable {
            mBinding?.fmChatScrollView?.scrollBy(0, 100)
            mHandler?.postDelayed(this.mRunner, 2000)
        }
    }

    private var mUserMode: String = ""
    private var mSpeech: TextToSpeech? = null
    private var currentDto: ChatBaseDto? = null

    override fun initHeaderView(): BaseHeaderView? {
        return mBinding?.chatFmHeaderView
    }

    override fun initViews() {
        mUserMode = CommonSharedPreferences.getInstance().userRate
        autoSpeak =
            CommonSharedPreferences.getInstance().getBoolean(Constants.ENABLE_AUTO_SPEAK, false)
        val themeColor: Int = R.color.color_chat_style_1
        CommonSharedPreferences.getInstance().apply {
            this.titleAppChat.let { if (!it.isNullOrBlank()) mHeaderView?.setTitle(it) }
            limitChar = 1000
        }
        mHeaderView?.apply {
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
            mAdapter?.mEnableAnimateText = true
            mBinding?.chatFmTvStopAnimateText?.gone()
            mBinding?.chatFmEdt?.isEnabled = true
            isClickSuggestQuestion = true
            mHandler?.removeCallbacks(mRunner)
            mSpeech?.stop()
            displaySuggestQues()
        }

        mAdapter?.onClickRegenerate = { _ ->
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

        mAdapter?.onClickSeeMore = {

        }
        mBinding?.chatFmRcv?.adapter = mAdapter
        mBinding?.chatFmRcv?.itemAnimator = DefaultItemAnimator()
        mRequestSpeech = activity?.activityResultRegistry?.let { activityResultRegistry ->
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                activityResultRegistry
            ) { result ->
                result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let {
                    mBinding?.chatFmEdt?.setText(it)
                    if (it.isNotEmpty()) mBinding?.chatFmBtnSend?.isEnabled = true
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
        mBinding?.chatFmBtnSend?.isEnabled = false
    }

    private fun scrollNettedScrollView() {
        lifecycleScope.launchWhenStarted {
            delay(100L)
            with(mBinding?.fmChatScrollView) {
                val scrollViewHeight = this?.height.orZero()
                if (scrollViewHeight > 0) {
                    val lastView: View? = this?.getChildAt(this.childCount.minus(1))
                    val lastViewBottom = lastView?.bottom.orZero() + this?.paddingBottom.orZero()
                    val deltaScrollY = lastViewBottom - scrollViewHeight - this?.scrollY.orZero()
                    /* If you want to see the scroll animation, call this. */
                    this?.smoothScrollBy(0, deltaScrollY)
                }
            }
        }
    }

    private fun displaySuggestQues() {
        if (mListSuggest.isNotEmpty()) {
            with(mBinding) {
                this?.fmChatSuggestQuestion?.visible()
                if (!mListSuggest.getOrNull(0).isNullOrEmpty()) {
                    this?.fmChatSuggestQues1?.visible()
                    this?.fmChatSuggestQues1?.text = mListSuggest.getOrNull(0)
                } else  this?.fmChatSuggestQues1?.gone()
                if (!mListSuggest.getOrNull(1).isNullOrEmpty()) {
                    this?.fmChatSuggestQues2?.visible()
                    this?.fmChatSuggestQues2?.text = mListSuggest.getOrNull(1)
                } else  this?.fmChatSuggestQues2?.gone()
                if (!mListSuggest.getOrNull(2).isNullOrEmpty()) {
                    this?.fmChatSuggestQues3?.visible()
                    this?.fmChatSuggestQues3?.text = mListSuggest.getOrNull(2)
                } else  this?.fmChatSuggestQues3?.gone()
            }
        }
    }

    private var isActionSend = false
    private fun actionSend() {
        val inputText = mBinding?.chatFmEdt?.text?.toString() ?: Strings.EMPTY
        UtilsApp.hideKeyboard(activity)
        if (!NetworkUtil.isInternetAvailable(requireContext())) {
            pushScreen(FragmentLostInternet.newInstance(), FragmentLostInternet::class.java.name)
        }
        if (inputText.isBlank()) {
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
        mQuestionTopic = inputText
        handleCallChatGPTWithTopic(false)
    }

    fun copyText(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, getStringRes(R.string.toast_copy_text), Toast.LENGTH_SHORT).show()
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
            mBinding?.chatFmTvStopAnimateText?.gone()
            isClickSuggestQuestion = true
            mSpeech?.stop()
            mAdapter?.stopAnimateText()
            mHandler?.removeCallbacks(mRunner)
            hideLoading()
            displaySuggestQues()
            scrollNettedScrollView()
        }
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation)
        mBinding?.chatFmRcv?.layoutAnimation = controller
        mBinding?.chatFmRcv?.setOnTouchListener { _, _ ->
            UtilsApp.hideKeyboard(activity)
            return@setOnTouchListener false
        }
        mBinding?.chatFmEdt?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                actionSend()
            }
            return@setOnEditorActionListener false
        }

        mBinding?.chatFmEdt?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(limitChar ?: Constants.LIMIT_INPUT_CHAT))

        mBinding?.chatFmBtnOcr?.setOnSingleClick {
            if (mBinding?.chatFmEdt?.isEnabled == false) return@setOnSingleClick
            showDialogOcr()
        }
        mBinding?.fmChatSuggestQues1?.setOnCLickTextViewSuggest()
        mBinding?.fmChatSuggestQues2?.setOnCLickTextViewSuggest()
        mBinding?.fmChatSuggestQues3?.setOnCLickTextViewSuggest()
    }

    private fun TextView.setOnCLickTextViewSuggest() {
        this.setOnSingleClick {
            mQuestionTopic = this.text.toString()
            handleCallChatGPTWithTopic(false)
        }
    }

    @SuppressLint("SetTextI18n", "StringFormatMatches", "SuspiciousIndentation")
    override fun initData() {
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading()
            withContext(Dispatchers.Default) {
                delay(500)
            }
            hideLoading()
            mViewModel.getCurrentDto().observe(this@ChatWithTopicFragment) {
                currentDto = it
                if (isNewChat || isActionSend || !autoSpeak) {
                    mAdapter?.updateData(it.chatDetail)
                    isActionSend = false
                    kotlin.runCatching {
                        mBinding?.chatFmRcv?.setItemViewCacheSize(it.chatDetail.size - 1)
                    }
                    scrollNettedScrollView()
                }
            }
            initChatTopic()
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

                    override fun onDone(utteranceId: String) {}

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String) {}

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

        mViewModel.inputEdtChat.observe(this) {
            mBinding?.chatFmBtnSend?.isEnabled = !it.isNullOrBlank()
            mBinding?.chatFmLimitText?.text =
                getString(R.string.str_limit_text_chat, it.length, limitChar.toString())
        }

        mViewModel.callChatWithTopic.observe(this) {
            handleCallChatGPTWithTopic(false)
        }
    }

    private fun handleCallChatGPTWithTopic(isRegenerate: Boolean) {
        var lastAssistantChat = ""
        mBinding?.fmChatSuggestQuestion?.gone()
        if (!NetworkUtil.isInternetAvailable(requireContext())) {
            pushScreen(FragmentLostInternet.newInstance(), FragmentLostInternet::class.java.name)
            return
        }
        showLoading()
        isActionSend = true
        mIsStartMore = false
        currentDto?.chatDetail?.lastOrNull { it.chatType == ChatType.SEND.value || it.message == mQuestionTopic }?.let {
            currentDto?.chatDetail?.lastIndexOf(it)?.let { it1 ->
                lastAssistantChat = currentDto?.chatDetail?.get(it1)?.message ?: ""
            }
        }
        mViewModel.completeTopicChat(
            context,
            mQuestionTopic,
            getStringRes(R.string.something_error),
            isNewChat,
            topicType = (arguments?.getParcelable(ChatDetailFragment.TOPIC) as? TopicDto)?.topicType
                ?: -1,
            if (isRegenerate && lastAssistantChat.isNotBlank()) lastAssistantChat else null
        ).observe(this) { dto ->
            isNewChat = false
            handleResultDtoChat(dto)
        }
    }

    private fun handleResultDtoChat(dto: ResultDataDto) {
        when (dto) {
            is ResultDataDto.Error -> {
                if (dto.errorType == ErrorType.UNKNOWN) { isActionSend = true }
                hideLoading()
            }
            else -> {
                isActionSend = true
                mAdapter?.mEnableAnimateText = true
                mIsNewChatAction = true
                mBinding?.chatFmTvStopAnimateText?.visible()
                mListSuggest.clear()
                mListSuggest.addAll((dto as? ResultDataDto.SuccessTopic)?.result?.suggestList ?: listOf())
                if (autoSpeak) {
                    val params = Bundle()
                    params.putString(
                        TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                        (dto as? ResultDataDto.SuccessTopic)?.result?.topicText
                    )
                    mSpeech?.speak(
                        (dto as? ResultDataDto.SuccessTopic)?.result?.topicText,
                        TextToSpeech.QUEUE_FLUSH,
                        params,
                        (dto as? ResultDataDto.SuccessTopic)?.result?.topicText
                    )
                } else { mAdapter?.setCharacterDelay(30L) }
                mHandler?.postDelayed(mRunner, 2000L)
            }
        }
    }

    private var mQuestionTopic = ""
    private fun initChatTopic() {
        mQuestionTopic = arguments?.getString(KEY_QUESTION_WITH_TOPIC) ?: ""
        mViewModel.initChat(
            0,
            getStringRes(R.string.hi_there),
            "at",
            mQuestionTopic,
            arguments?.getString(KEY_TITLE_TOPIC)
        )
    }

    override fun handleOnBackPress(): Boolean {
            popBackstackAllFragment()
        return true
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

    companion object {
        private const val DELAY_SCROLL = 100L
        private const val DELAY_CALL_FUN_REGENERATE = 500L
        private const val KEY_QUESTION_WITH_TOPIC = "key_question_with_topic"
        private const val KEY_TITLE_TOPIC = "key_title_topic"
        fun newInstance(ques: String, titleTopic: String): ChatWithTopicFragment {
            val args = Bundle()
            val fragment = ChatWithTopicFragment()
            args.putString(KEY_QUESTION_WITH_TOPIC, ques)
            args.putString(KEY_TITLE_TOPIC, titleTopic)
            fragment.arguments = args
            return fragment
        }
    }
}