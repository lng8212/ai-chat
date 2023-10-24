/*
 * Created by longkd on 10/1/23, 11:46 AM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/1/23, 11:46 AM
 */



package com.longkd.chatgpt_openai.service

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.bubble.*
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.feature.chat.viewholder.ChatDetailAdapter
import com.longkd.chatgpt_openai.feature.splash.SplashActivity
import com.longkd.chatgpt_openai.open.chat.ChatRepository
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Message35Request
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException


@AndroidEntryPoint
class BubbleService : FloatingBubbleService(), LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var layout: View
    private var actionPopToBubble: (() -> Unit)? = null
    private val mArrListPromt: ArrayList<Message35Request> = arrayListOf()

    @Inject
    lateinit var dataRepository: DataRepository

    @Inject
    lateinit var chatRepository: ChatRepository
    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun setupBubble(action: FloatingBubble.Action): FloatingBubble.Builder {
        return FloatingBubble.Builder(this)
            .bubble(R.drawable.ic_bubble_chat, 56, 56)
            // set style for bubble, fade animation by default
            .bubbleStyle(null)
            // set start location of bubble, (x=0, y=0) is the top-left
            // enable auto animate bubble to the left/right side when release, true by default
            .enableAnimateToEdge(true)

            // set close-bubble icon attributes, currently only drawable and bitmap are supported
            .closeBubble(R.drawable.ic_close_bubble, 50, 50)

            // set style for close-bubble, null by default
            .closeBubbleStyle(null)

            // show close-bubble, true by default
            .enableCloseBubble(true)
            // the more value (dp), the larger closeable-area
            .closablePerimeter(100)

            // choose behavior of the bubbles
            // DYNAMIC_CLOSE_BUBBLE: close-bubble moving based on the bubble's location
            // FIXED_CLOSE_BUBBLE: bubble will automatically move to the close-bubble when it reaches the closable-area
            .behavior(BubbleBehavior.DYNAMIC_CLOSE_BUBBLE)

            // enable bottom background, false by default
            .bottomBackground(false)

            // add listener for the bubble
            .addFloatingBubbleListener(object : FloatingBubble.Listener {
                override fun onDestroy() {}
                override fun onClick() {
                    action.navigateToExpandableView() // must override `setupExpandableView`, otherwise throw an exception
                }

                override fun onMove(
                    x: Float,
                    y: Float
                ) {
                } // The location of the finger on the screen which triggers the movement of the bubble.

                override fun onUp(x: Float, y: Float) {}   // ..., when finger release from bubble
                override fun onDown(x: Float, y: Float) {} // ..., when finger tap the bubble
            })
            .opacity(1f)
    }

    override fun setupExpandableView(action: ExpandableView.Action): ExpandableView.Builder {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        actionPopToBubble = {
            action.popToBubble()
        }
        val wrapper: ViewGroup = object : FrameLayout(this) {
            override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                    action.popToBubble()
                    return true
                }
                return super.dispatchKeyEvent(event)
            }
        }

        layout = inflater.inflate(R.layout.view_bubble_chat, wrapper)
        initViews()
        initData()
        initActions()
        val mHomeWatcher = HomeWatcher(this)

        layout.findViewById<ConstraintLayout>(R.id.bg).setOnSingleClick {
            action.popToBubble()
        }
        return ExpandableView.Builder(this) // set the amount of dimming below the view.
            .dimAmount(0f)
            .view( layout)
            // apply style for the expandable-view
            .expandableViewStyle(null)
            // ddd listener for the expandable-view
            .addExpandableViewListener(object : ExpandableView.Listener {
                override fun onOpenExpandableView() {
                    mHomeWatcher.setOnHomePressedListener(object : OnHomePressedListener {
                        override fun onHomePressed() {
                            action.popToBubble()
                        }

                        override fun onHomeLongPressed() {
                            action.popToBubble()
                        }
                    })
                    mHomeWatcher.startWatch()
                }
                override fun onCloseExpandableView() {
                    mHomeWatcher.stopWatch()

                }
            })
    }

    private fun handleSelectModelChat() {
        val rootView = layout.findViewById<LinearLayout>(R.id.fmChat_llnSelectModel)
        val tvTitleView = layout.findViewById<TextView>(R.id.viewBubbleChat_model)
        tvTitleView.setOnSingleClick {
            if (rootView.visibility == View.GONE) {
                rootView.visible()
            } else rootView.gone()
            handleDisplaySelectModelChat()
        }
    }

    @SuppressLint("CutPasteId")
    private fun handleDisplaySelectModelChat() {
        val rootView = layout.findViewById<LinearLayout>(R.id.fmChat_llnSelectModel)
        var modelChat = getModelChat()
        val tvTitleGpt4 = rootView.findViewById<TextView>(R.id.titleGpt4)
        val tvTitleView = layout.findViewById<TextView>(R.id.viewBubbleChat_model)
        val layoutViewGpt3_5 = rootView.findViewById<LinearLayout>(R.id.llnView35)
        val layoutViewGpt4 = rootView.findViewById<LinearLayout>(R.id.llnViewGpt4)
     tvTitleGpt4.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        if (modelChat == Constants.MODEL_CHAT.GPT_3_5) {
            tvTitleView.text = "Chat GPT 3.5"
            layoutViewGpt3_5.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
            layoutViewGpt4.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
        } else {
            tvTitleView.text = "Chat GPT 4"
            layoutViewGpt3_5?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
            layoutViewGpt4.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
        }
        layoutViewGpt3_5.setOnSingleClick {
            modelChat = Constants.MODEL_CHAT.GPT_3_5
            layoutViewGpt3_5?.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
            layoutViewGpt4.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
        }

        layoutViewGpt4.setOnSingleClick {
            modelChat = Constants.MODEL_CHAT.GPT_4
            layoutViewGpt3_5?.setBackgroundResource(R.drawable.bg_border_stroke_gray_20)
            layoutViewGpt4.setBackgroundResource(R.drawable.bg_border_stroke_green_20)
        }

        rootView.findViewById<TextView>(R.id.btnContinue).setOnSingleClick {
            rootView.gone()
            tvTitleView?.text = if (modelChat == Constants.MODEL_CHAT.GPT_3_5) "Chat GPT 3.5" else "Chat GPT 4"
            setModelChat(modelChat)
        }
    }

    private fun getModelChat(): String = CommonSharedPreferences.getInstance().modelChatGpt
    fun setModelChat(value: String) {
        CommonSharedPreferences.getInstance().modelChatGpt = value
    }

    private var mAdapter: ChatDetailAdapter? = null
    private var mSpeechRecognizer: SpeechRecognizer? = null
    private val mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var mEnableShowStopAnimate = false
    private var mIsNewChatAction = false
    private var isNewChat = true
    private var mViewTreeObserver: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var onNetworkConnectivityCallback: ConnectivityManager.NetworkCallback? = null
    private var mWillShowSale = false
    private var textTemplate = ""
    private var autoSpeak = false
    private var mIsStartMore = false
    private var mIsCallMore = false
    private var textMessaeMore = ""
    private val handler = Handler(Looper.getMainLooper())
    private val mHandler: Handler? by lazy {
        Handler(Looper.getMainLooper())
    }
    private var currentMessage = 3
    private var mRunner = Runnable {
        kotlin.runCatching {
            layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).scrollBy(0, 100)
        }
    }
    var mUserMode: String = ""
    var mSpeech: TextToSpeech? = null
    private var currentDto: ChatBaseDto? = null
    private var isCheckDuplicateRegenerate: Boolean = false

    fun initViews() {
        mUserMode = CommonSharedPreferences.getInstance().userRate
        autoSpeak =
            CommonSharedPreferences.getInstance().getBoolean(Constants.ENABLE_AUTO_SPEAK, true)
        val themeColor: Int = R.color.color_chat_style_1
        mAdapter = ChatDetailAdapter(arrayListOf(), false, themeColor,
            object : ItemClickListener<ChatDetailDto> {
                override fun onClick(item: ChatDetailDto?, position: Int) {
                    kotlin.runCatching {
                        if (item?.message != null) {
                            copyText(this@BubbleService, item.message)
                        }
                    }
                }
            })
        mAdapter?.mOnAnimateFinished = {
            if (mIsCallMore) {
                mIsCallMore = false
            } else {
                mAdapter?.mEnableAnimateText = true
                layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).gone()
                mHandler?.removeCallbacks(mRunner)
                mRunner.run()
                hideLoading()
            }
        }
        mAdapter?.onClickRegenerate = {
            if (layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).visibility == View.GONE) {
                lifecycleScope.launchWhenStarted {
                    if (isCheckDuplicateRegenerate) return@launchWhenStarted
                    isCheckDuplicateRegenerate = true
                    handleCallChatGPTWithTopic()
                    delay(500L)
                    isCheckDuplicateRegenerate = false
                }
            }
        }

        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).adapter = mAdapter
        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).itemAnimator = DefaultItemAnimator()
        mSpeechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
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
        mViewTreeObserver = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            layout.getWindowVisibleDisplayFrame(r)
            val screenHeight: Int = layout.rootView?.height ?: 0
            val keypadHeight: Int = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).post {
                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).postDelayed({
                        kotlin.runCatching {
                            layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).smoothScrollToPosition(
                                (mAdapter?.listData?.size ?: 1) - 1
                            )
                        }
                    }, 100)
                }
            }
        }
        layout.viewTreeObserver?.addOnGlobalLayoutListener(mViewTreeObserver)
        handleSelectModelChat()
        initChat()
    }

    private fun startSplashActivity() {
        val intent = Intent(this,SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private var isActionSend = false
    private fun actionSend() {
        callGetTimeStamp()
        val inputText = layout.findViewById<EditText>(R.id.viewBubbleChat_edt).text?.toString() ?: Strings.EMPTY
        hideKeyboard(this)

        if (inputText.isBlank()) {
            showDialogError(R.string.input_empty_error)
            return
        }

        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).visible()
        layout.findViewById<EditText>(R.id.viewBubbleChat_edt).setText("")
        showLoading()
        isActionSend = true
        mIsStartMore = false
        completeQRetrofitHandler(
            this,
            inputText.trim(),
            getStringRes(R.string.something_error),
            isNewChat,
            topicType = -1
        ).observe(this) { dto ->
            isNewChat = false
            handleResultDtoChat(dto)
        }
    }

    fun hideKeyboard(context: Context) {
        val imm: InputMethodManager? =
            context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager
        var view: View? = null
        if (view == null) {
            view = View(context)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun copyText(context: Context, text: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, getStringRes(R.string.toast_copy_text), Toast.LENGTH_SHORT).show()
    }

    fun initActions() {
        layout.findViewById<ImageView>(R.id.viewBubbleChat_btnSend).setOnSingleClick {
            actionSend()
        }

        onNetworkConnectivityCallback?.let {
        }

        layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).setOnSingleClick {
            layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).gone()
            mSpeech?.stop()
            mAdapter?.stopAnimateText()
            hideLoading()
        }
        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).itemAnimator = DefaultItemAnimator()
        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).layoutAnimation = controller
        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).setOnTouchListener { v, event ->
            hideKeyboard(this)
            return@setOnTouchListener false
        }
        layout.findViewById<EditText>(R.id.viewBubbleChat_edt).setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                actionSend()
            }
            return@setOnEditorActionListener false
        }
    }

    private fun handleCallChatGPTWithTopic() {
        callGetTimeStamp()
        showLoading()
        isActionSend = true
        mIsStartMore = false
        completeChatWithTopic(
            context = this,
            getStringRes(R.string.something_error)
        ).observe(this) { dto ->
            isNewChat = false
            handleResultDtoChat(dto)
        }
    }

    private fun handleResultDtoChat(dto: ResultDataDto) {
        when (dto) {
            is ResultDataDto.Error -> {
                if (dto.errorType == ErrorType.END_VIP) {
                }
                if (dto.errorType == ErrorType.UNKNOWN) {
                    isActionSend = true
                }
                hideLoading()
            }
            else -> {
                isActionSend = true
                mIsCallMore = (dto as? ResultDataDto.Success)?.mIsCallMore ?: false
                mEnableShowStopAnimate = true
                mAdapter?.mEnableAnimateText = true
                mIsNewChatAction = true
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
                    layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).visible()
                    mAdapter?.setCharacterDelay(30L)
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        val resultLenght =
                            (dto as? ResultDataDto.Success)?.result?.length ?: 0
                        val time = resultLenght * 50
                        val loop = time / 2000 + 1
                        for (i in 0 until loop) {
                            delay(2000)
                            handler.post {
                                mHandler?.post(mRunner)
                            }
                        }
                    }
                }

            }
        }
    }
    private var isCallChatSuccess: Boolean ? = null

    private fun initChat() {
        uiScope.launch(Dispatchers.Main) {
            val newDto = withContext(Dispatchers.Default) {
                ChatBaseDto(
                    System.currentTimeMillis(), arrayListOf(
                        ChatDetailDto(
                            getStringRes(R.string.str_title_start_chat),
                            System.currentTimeMillis(),
                            com.longkd.chatgpt_openai.base.util.DateUtils.parseTime("at"),
                            false,
                            ChatType.RECEIVE.value
                        )
                    ), topicType = -1
                )
            }
            newDto.let { mCurrentChatBaseDto.value = this@BubbleService.reFormatDate(it) }
        }
    }

    private fun completeChatWithTopic(
        context: Context?,
        valueError: String,
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        uiScope.launch(Dispatchers.Main) {
            val resultDataDto = ChatDetailDto(
                "",
                System.currentTimeMillis(),
                "",
                true,
                ChatType.RECEIVE.value,
                ""
            )
            mCurrentChatBaseDto.value = mCurrentChatBaseDto.value?.apply {
                lastTimeUpdate = System.currentTimeMillis()
                chatDetail = ArrayList(chatDetail).apply {
                    add(resultDataDto)
                }
            }
            mCurrentChatBaseDto.value?.let {
                dataRepository.insertChat(it)
            }
            if (isCallChatSuccess == true && mArrListPromt.size > 1) {
                mArrListPromt.removeLastOrNull()
            }
            var resultText = valueError
            val completionResult = withContext(Dispatchers.Default) {
                val completionRequest = Completion35Request()
                completionRequest.model = ModelData.GPT_35.value
                completionRequest.messages = mArrListPromt
                completionRequest.maxTokens =
                    1000

                try {
                    chatRepository.createCompletionV1Chat(completionRequest).data
                } catch (e: Throwable) {
                    if (e.cause is SSLHandshakeException) {
                        resultText = try {
                            context?.getString(R.string.txt_exception_date)
                                ?: "There is an error, please check if automatic date and time are enabled on your phone."
                        } catch (e: Exception) {
                            "There is an error, please check if automatic date and time are enabled on your phone."
                        }
                    }  else if (e.message?.contains(Constants.MESSAGE_TIME_OUT) == true) {
                        resultText = context?.getString(R.string.str_message_time_out_connecting) ?: "Time out! Please make sure your internet connection is stable."
                    }
                    e.printStackTrace()
                    null
                }
            }
            val data = completionResult?.choices?.first()?.message?.content
            var mIsCallMore = false
            completionResult?.choices?.firstOrNull()?.finishReason?.let {
                if (it.isNotEmpty()) mIsCallMore = it != "stop"
            }
            if (data.isNullOrBlank()) {
                isCallChatSuccess = false
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                mArrListPromt.add(
                    Message35Request(
                        completionResult.choices.getOrNull(0)?.message?.role,
                        completionResult.choices.getOrNull(0)?.message?.content
                    )
                )
                resultText = data
                result.value = ResultDataDto.Success(data, mIsCallMore)
                isCallChatSuccess = true
            }
            mCurrentChatBaseDto.value?.apply {
                lastTimeUpdate = System.currentTimeMillis()
                chatDetail.findLast {
                    it.chatType == ChatType.RECEIVE.value
                }?.apply {
                    this.message = resultText
                    this.isTyping = false
                    this.chatUserNane = ""
                    this.isSeeMore = mIsCallMore
                }
            }
            mCurrentChatBaseDto.value?.let {
                dataRepository.updateChatDto(it)
                val currentBaseDto = reFormatDate(it).apply { it.chatDetail.findLast { it.chatType == ChatType.RECEIVE.value }?.isSeeMore = mIsCallMore}
                mCurrentChatBaseDto.value = currentBaseDto
            }
        }
        return result
    }

    @SuppressLint("SetTextI18n")
    fun initData() {
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading()
            withContext(Dispatchers.Default) {
                delay(500)
            }
            hideLoading()
            getCurrentDto().observe(this@BubbleService) {
                currentDto = it
                if (isNewChat || isActionSend || !autoSpeak) {
                    mAdapter?.updateData(it.chatDetail)
                    isActionSend = false
                    kotlin.runCatching {
                        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).setItemViewCacheSize(it.chatDetail.size - 1)
                    }
                    if (mIsNewChatAction) {
                        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).post {
                            layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).postDelayed({
                                kotlin.runCatching {
                                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).smoothScrollToPosition(it.chatDetail.size - 1)
                                }
                            }, 100)
                        }
                    } else {
                        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).post {
                            layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).postDelayed({
                                kotlin.runCatching {
                                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).scrollToPosition(it.chatDetail.size - 1)
                                }
                            }, 100)
                        }
                    }
                }
            }
        }
        mMessageMore.observe(this@BubbleService) { message ->
            val timer = object : CountDownTimer(80000, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    if (autoSpeak) {
                        if (mSpeech?.isSpeaking == false) {
                            mIsStartMore = true
                            textMessaeMore = message
                            if (layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).isVisible) {
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
                    } else {
                        if (!mIsCallMore){
                            if (layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).isVisible) {
                                mAdapter?.updateTextMore(message)
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
        mSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                mSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        handler.post {
                            if (mIsStartMore) {
                                mAdapter?.updateTextMore(textMessaeMore)
                                mIsStartMore = false
                                return@post
                            }
                            currentDto?.let {
                                mAdapter?.updateData(it.chatDetail)
                                layout.findViewById<TextView>(R.id.viewBubbleChat_tvStopAnimateText).visible()
                                kotlin.runCatching {
                                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).setItemViewCacheSize(it.chatDetail.size - 1)
                                }
                                if (mIsNewChatAction) {
                                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).post {
                                        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).postDelayed({
                                            kotlin.runCatching {
                                                layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).smoothScrollToPosition(
                                                    it.chatDetail.size - 1
                                                )
                                            }
                                        }, 100)
                                    }
                                } else {
                                    layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).post {
                                        layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).postDelayed({
                                            kotlin.runCatching {
                                                layout.findViewById<RecyclerView>(R.id.viewBubbleChat_rcv).scrollToPosition(it.chatDetail.size - 1)
                                            }
                                        }, 100)
                                    }
                                }
                            }
                        }
                    }

                    override fun onDone(utteranceId: String) {}

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

                    override fun onStop(utteranceId: String, interrupted: Boolean) {}
                })
            }

        }
        mSpeech?.setSpeechRate(1.2f)
        kotlin.runCatching {
            mSpeech?.language = Locale.getDefault()
        }
    }


    override fun onDestroy() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        val intent = Intent(Constants.SERVICE_DESTROY)
        sendBroadcast(intent)
        super.onDestroy()
        onNetworkConnectivityCallback?.let {
        }
        mSpeech?.stop()

        hideKeyboard(this)
        notifyUpdateChatHistory()
    }


    fun showLoading() {
        layout.findViewById<ImageView>(R.id.viewBubbleChat_btnSend).isEnabled = false
    }

    fun hideLoading() {
        layout.findViewById<ImageView>(R.id.viewBubbleChat_btnSend).isEnabled = true
    }

    fun showDialogError(@StringRes message: Int) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
        }
    }
    var mCurrentChatBaseDto: MutableLiveData<ChatBaseDto> = MutableLiveData()
    private var mTextAtTime: String = ""
    private var mChatNumber: MutableLiveData<Int> = MutableLiveData()
    private var uiScope = CoroutineScope(Dispatchers.Main)
    var mMessageMore: MutableLiveData<String> = MutableLiveData()
    private fun getCurrentDto(): LiveData<ChatBaseDto> = mCurrentChatBaseDto


    private fun parseTime(textAtTime: String): String {
        return try {
            val formatter = SimpleDateFormat("d MMM, yyyy ; HH:mm", Locale.US)
            val now = Date()
            formatter.format(now).toString().replace(";", textAtTime)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getFormattedDate(j: Long): String {
        val instance = Calendar.getInstance()
        instance.timeInMillis = j
        return if (DateUtils.isToday(j)) {
            DateFormat.format("h:mm aa", instance).toString()
        } else if (isYesterday(j)) {
            "Yesterday, " + DateFormat.format("h:mm aa", instance).toString()
        } else {
            DateFormat.format("d MMM, yyyy, HH:mm", instance).toString()
        }
    }

    private suspend fun reFormatDate(dto: ChatBaseDto): ChatBaseDto {
        return withContext(Dispatchers.Default) {
            dto.chatDetail.forEach {
                it.timeChatString = getFormattedDate(it.timeChat)
            }
            dto.chatDetail = disconnectDataLinked(dto.chatDetail)
            dto
        }
    }

    fun disconnectDataLinked(array: List<ChatDetailDto>): List<ChatDetailDto> {
        val newList: ArrayList<ChatDetailDto> = arrayListOf()
        array.forEach { dto ->
            newList.add(
                ChatDetailDto(
                    dto.message,
                    dto.timeChat,
                    dto.timeChatString,
                    dto.isTyping,
                    dto.chatType,
                    dto.chatUserNane,
                    dto.parentId
                ).apply {

                })
        }
        return newList
    }

    private fun isYesterday(j: Long): Boolean {
        return DateUtils.isToday(j + 86400000)
    }


    private suspend fun reFormatDate(dto: ChatDetailDto): ChatDetailDto {
        return withContext(Dispatchers.Default) {
            dto.timeChatString = getFormattedDate(dto.timeChat)
            dto
        }
    }

    fun completeQRetrofitHandler(
        context: Context?,
        input: String,
        valueError: String,
        isNewChat: Boolean,
        topicType: Int
    ): LiveData<ResultDataDto> {
        return completeQRetrofit35(context, input, valueError, isNewChat, topicType)

    }
    private val limitSavePrevConversation = CommonSharedPreferences.getInstance().limitSavePrevConversation.times(2).plus(1)
    fun completeQRetrofit35(
        context: Context?,
        input: String,
        valueError: String,
        isNewChat: Boolean,
        topicType: Int
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        val resultDataDto = ChatDetailDto(
            "",
            System.currentTimeMillis(),
            "",
            true,
            ChatType.RECEIVE.value,
            input
        )
        uiScope.launch(Dispatchers.Main) {
            if (isNewChat) {
                mCurrentChatBaseDto.value = ChatBaseDto(
                    System.currentTimeMillis(), arrayListOf(
                        ChatDetailDto(
                            input,
                            System.currentTimeMillis(),
                            parseTime(mTextAtTime),
                            false,
                            ChatType.SEND.value,
                        ),
                        resultDataDto
                    ),
                    topicType = topicType,
                    lastTimeUpdate = System.currentTimeMillis()
                )
            } else {
                mCurrentChatBaseDto.value?.apply {
                    lastTimeUpdate = System.currentTimeMillis()
                    chatDetail = ArrayList(chatDetail).apply {
                        add(
                            ChatDetailDto(
                                input,
                                System.currentTimeMillis(),
                                parseTime(mTextAtTime),
                                false,
                                ChatType.SEND.value,
                                input

                            )
                        )
                        add(resultDataDto)
                    }
                }
            }
            mCurrentChatBaseDto.value?.let {
                if (isNewChat) {
                    dataRepository.insertChat(it)

                } else {
                    dataRepository.updateChatDto(it)
                }
                mCurrentChatBaseDto.value = reFormatDate(it)
            }
            var resultText = valueError
            val completionResult = withContext(Dispatchers.Default) {
                if (mArrListPromt.size >= (limitSavePrevConversation)) {
                    mArrListPromt.removeFirst()
                    mArrListPromt.removeFirst()
                }
                mArrListPromt.add(Message35Request("user", input))
                val completionRequest = Completion35Request()
                completionRequest.model = ModelData.GPT_35.value
                completionRequest.messages = mArrListPromt
                completionRequest.maxTokens =
                1000

                completionRequest.stop  = listOf("/n")
                try {
                    chatRepository.createCompletionV1Chat(completionRequest).data
                } catch (e: Throwable) {
                    if (e.cause is SSLHandshakeException) {
                        resultText = try {
                            context?.getString(R.string.txt_exception_date)
                                ?: "There is an error, please check if automatic date and time are enabled on your phone."
                        } catch (e: Exception) {
                            "There is an error, please check if automatic date and time are enabled on your phone."
                        }
                    } else if (e.message?.contains(Constants.MESSAGE_TIME_OUT) == true) {
                        resultText = context?.getString(R.string.str_message_time_out_connecting) ?: "Time out! Please make sure your internet connection is stable."
                    }
                    e.printStackTrace()
                    null
                }
            }
            val data = completionResult?.choices?.first()?.message?.content
            var mIsCallMore = false
            completionResult?.choices?.firstOrNull()?.finishReason?.let {
                if (it.isNotEmpty()) mIsCallMore = it != "stop"
            }
            if (data.isNullOrBlank()) {
                isCallChatSuccess = false
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                mArrListPromt.add(
                    Message35Request(
                        completionResult.choices.getOrNull(0)?.message?.role,
                        completionResult.choices.getOrNull(0)?.message?.content
                    )
                )
                isCallChatSuccess = true
                result.value = ResultDataDto.Success(data, mIsCallMore)
                resultText = data
            }
            mCurrentChatBaseDto.value?.apply {
                lastTimeUpdate = System.currentTimeMillis()
                chatDetail.findLast {
                    it.chatType == ChatType.RECEIVE.value
                }?.apply {
                    this.message = resultText
                    this.isTyping = false
                    this.chatUserNane = input
                    this.isSeeMore = mIsCallMore
                }
            }
            mCurrentChatBaseDto.value?.let {
                dataRepository.updateChatDto(it)
                val currentBaseDto = reFormatDate(it).apply {
                    it.chatDetail.findLast { it.chatType == ChatType.RECEIVE.value }?.isSeeMore =
                        mIsCallMore
                }
                mCurrentChatBaseDto.value = currentBaseDto
            }
        }
        return result
    }

    private fun callGetTimeStamp() {
        CommonSharedPreferences.getInstance().timeStamp = System.currentTimeMillis().toString()

    }

    private var mNotifyUpdateChatHistory: MutableLiveData<Int> = MutableLiveData()

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */

    private fun notifyUpdateChatHistory() {
        val new = (mNotifyUpdateChatHistory.value ?: 0) + arrayOf(1, -1, 2).random()
        mNotifyUpdateChatHistory.value = new
    }

    fun getStringRes(@StringRes res: Int): String {
        return try {
            resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry

    }
}

