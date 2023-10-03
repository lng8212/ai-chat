package com.longkd.chatgpt_openai.feature.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.OpenAIHolder
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.feature.art.StyleArtDto
import com.longkd.chatgpt_openai.feature.art.StyleArtHelper
import com.longkd.chatgpt_openai.open.client.OpenAiService
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.CompletionRequest
import com.longkd.chatgpt_openai.open.dto.completion.Message35Request
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtRequest
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtResult
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ChatRole
import com.longkd.chatgpt_openai.base.model.ChatType
import com.longkd.chatgpt_openai.base.model.ErrorType
import com.longkd.chatgpt_openai.base.model.ModelData
import com.longkd.chatgpt_openai.base.model.ResultDataDto
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.DateUtils
import com.longkd.chatgpt_openai.base.util.LoggerUtil
import com.longkd.chatgpt_openai.base.util.convertToChatBaseDto
import com.longkd.chatgpt_openai.open.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException
import kotlin.collections.ArrayList


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel(dataRepository) {
    private var mTimeStampService =
        TimeStampService(TOKEN, timeout = 60, type = 0)
    var mCurrentChatBaseDto: MutableLiveData<ChatBaseDto> = MutableLiveData()
    var mMessageMore: MutableLiveData<String> = MutableLiveData()
    var mGenerateNumber: MutableLiveData<Int> = MutableLiveData()
    private var mTextAtTime: String = ""
    private var mChatNumber: MutableLiveData<Int> = MutableLiveData()
    private val mHistoryList: MutableLiveData<ArrayList<ChatDetailDto>> = MutableLiveData()
    var currentStyleArt: MutableLiveData<ArrayList<StyleArtDto>> = MutableLiveData()
    private var mGenerateArtResult: GenerateArtResult = GenerateArtResult()
    private var position: Int = 1
    val mArrListPromt: ArrayList<Message35Request> = arrayListOf()
    val callChatWithTopic = MutableLiveData<Unit>()
    private val limitSavePrevConversation = CommonSharedPreferences.getInstance().limitSavePrevConversation.times(2).plus(1)
    private var isTextInputCopy: Boolean ? = null

    fun getCurrentDto(): LiveData<ChatBaseDto> = mCurrentChatBaseDto

    val inputEdtChat = MutableLiveData<String>()

    private var isCallChatSuccess: Boolean ? = null

    fun setTextInputCopy(value: Boolean) {
        this.isTextInputCopy = value
    }

    fun initChat(topicType: Int, firstMessage: String, textAtTime: String, question: String ? = null, topic: String ? = null) {
        if (!topic.isNullOrEmpty()) mArrListPromt.add(Message35Request(ChatRole.SYSTEM.value, "topic: $topic"))
        mTextAtTime = textAtTime
        question?.let {
            uiScope.launch(Dispatchers.Main) {
                val newDto = withContext(Dispatchers.Default) {
                    ChatBaseDto(
                        System.currentTimeMillis(), arrayListOf(
                            ChatDetailDto(
                                "$question",
                                System.currentTimeMillis(),
                                DateUtils.parseTime(textAtTime),
                                false,
                                ChatType.SEND.value,
                            )
                        ),
                        topicType = topicType
                    )
                }
                newDto.let { mCurrentChatBaseDto.value = this@HomeViewModel.reFormatDate(it) }
                callChatWithTopic.postValue(Unit)
            }
        } ?: run {
            if (this.isTextInputCopy == true) mArrListPromt.add(Message35Request(ChatRole.ASSISTANT.value, "$firstMessage"))
            uiScope.launch(Dispatchers.Main) {
                val newDto = withContext(Dispatchers.Default) {
                    ChatBaseDto(
                        System.currentTimeMillis(), arrayListOf(
                            ChatDetailDto(
                                firstMessage,
                                System.currentTimeMillis(),
                                DateUtils.parseTime(textAtTime),
                                false,
                                ChatType.RECEIVE.value
                            )
                        ), topicType = topicType
                    )
                }
                newDto.let { mCurrentChatBaseDto.value = this@HomeViewModel.reFormatDate(it) }
            }
        }
    }

    fun initViewChatHis(chatBaseDto: ChatBaseDto) {
        chatBaseDto.let {
            it.chatDetail.forEach {
                val role = if (it.chatType == ChatType.SEND.value) ChatRole.USER.value else ChatRole.SYSTEM.value
                mArrListPromt.add(Message35Request(role = role, content = "${it.message}"))
            }
        }
        val sizeArrPromt = mArrListPromt.size.minus(limitSavePrevConversation)
        if (sizeArrPromt > 0) {
            for (i in 0..sizeArrPromt) {
                mArrListPromt.removeFirstOrNull()
            }
        }
        mCurrentChatBaseDto.value = chatBaseDto
    }

    fun getMessNumber(): LiveData<Int> {
        uiScope.launch(Dispatchers.Main) {
            val value = withContext(Dispatchers.Default) {
                CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                    try {
                        OpenAIHolder.getChatFreeMessage(1, it)
                    } catch (e: Throwable) {
                        0
                    }
                } ?: 0
            }
            mChatNumber.value = value
        }
        return mChatNumber
    }

    fun getGenerateNumber() {
        uiScope.launch(Dispatchers.Main) {
            val value = withContext(Dispatchers.Default) {
                CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                    try {
                        OpenAIHolder.getFreeNumberGenerate(it)
                    } catch (e: Throwable) {
                        0
                    }
                } ?: 0
            }
            mGenerateNumber.value = value
        }
    }

    fun putGenerateNumber(numberGenerate: Int = 10) {
        CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
            try {
                OpenAIHolder.putNumberGenerate(it, numberGenerate)
            } catch (e: Throwable) {

            }
        }
    }

    fun getMessNumberNormal(): LiveData<Int> {
        val mChatNumber: MutableLiveData<Int> = MutableLiveData()
        uiScope.launch(Dispatchers.Main) {
            val value = withContext(Dispatchers.Default) {
                CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                    try {
                        OpenAIHolder.getChatFreeMessage(1, it)
                    } catch (e: Throwable) { 0 }
                } ?: 0
            }
            mChatNumber.value = value
        }
        return mChatNumber
    }

    private suspend fun reFormatDate(dto: ChatBaseDto): ChatBaseDto {
        return withContext(Dispatchers.Default) {
            dto.chatDetail.forEach {
                it.timeChatString = DateUtils.getFormattedDate(it.timeChat)
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
                    dto.message ?: "",
                    dto.timeChat ?: 0,
                    dto.timeChatString ?: "",
                    dto.isTyping ?: false,
                    dto.chatType ?: -1,
                    dto.chatUserNane ?: "",
                    dto.parentId ?: 0,
                    dto.isSeeMore
                ).apply {

                })
        }
        return newList
    }
    fun getAllChatHistory(): LiveData<ArrayList<ChatDetailDto>> {
        uiScope.launch(Dispatchers.Main) {
            val listChatHistory = arrayListOf<ChatDetailDto>()
            withContext(Dispatchers.Default) {
                dataRepository.getAllChatDto().forEach {
                    it.chatDetail.lastOrNull()?.let { it1 ->
                        listChatHistory.add(reFormatDate(it1.apply {
                            parentId = it.chatId
                        }))
                    }
                }
            }
            mHistoryList.value = listChatHistory
        }
        return mHistoryList
    }

    private suspend fun reFormatDate(dto: ChatDetailDto): ChatDetailDto {
        return withContext(Dispatchers.Default) {
            dto.timeChatString = DateUtils.getFormattedDate(dto.timeChat)
            dto
        }
    }

    fun getChatDto(chatId: Long): LiveData<ChatBaseDto> {
        val result: MutableLiveData<ChatBaseDto> = MutableLiveData()
        uiScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                dataRepository.getChatDto(chatId)?.let {
                    it.chatDetail.lastOrNull()?.let { it1 ->
                        result.postValue(reFormatDate(it))
                    }
                }
            }
        }
        return result
    }

    fun removeChatHistory(id: Long) {
        uiScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                dataRepository.removeChatDto(id)
            }
        }
    }

    fun completeTopicChat(
        context: Context?,
        input: String,
        valueError: String,
        isNewChat: Boolean,
        topicType: Int,
        lastAssistantChat: String ? = null
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        val resultDataDto = ChatDetailDto(
            "",
            System.currentTimeMillis(),
            "",
            true,
            ChatType.RECEIVE.value,
            input,
            isSeeMore = false,

            )
        uiScope.launch(Dispatchers.Main) {
            if (isNewChat) {
                mCurrentChatBaseDto.value = ChatBaseDto(
                    System.currentTimeMillis(), arrayListOf(
                        initChatDetailDto(input, ChatType.SEND.value),
                        resultDataDto
                    ),
                    topicType = topicType,
                    lastTimeUpdate = System.currentTimeMillis()
                )
            } else {
                mCurrentChatBaseDto.value?.apply {
                    lastTimeUpdate = System.currentTimeMillis()
                    chatDetail = ArrayList(chatDetail).apply {
                        add(initChatDetailDto(input, ChatType.SEND.value, input))
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
                if (mArrListPromt.lastOrNull()?.role != ChatRole.USER.value) {
                    mArrListPromt.add(Message35Request(ChatRole.USER.value, "$input"))
                } else {
                    mArrListPromt.lastOrNull()?.let {
                        it.content = "$input"
                    }
                }
                if (!lastAssistantChat.isNullOrEmpty()) {
                    mArrListPromt.lastOrNull { it.role == ChatRole.ASSISTANT.value }?.let {
                        it.content = lastAssistantChat
                    }
                }
                val completionRequest = Completion35Request()
                completionRequest.model = ModelData.GPT_35.value
                completionRequest.messages = mArrListPromt
                completionRequest.maxTokens = 1500
                try {
                    LoggerUtil.e("completionRequest: ${completionRequest.messages}")
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let { sharef ->
                        OpenAIHolder.completeTopicChat(
                            1, OpenAiService(TOKEN, timeout = Constants.TIME_OUT_TOPIC, type = 0), completionRequest, sharef
                        )
                    }
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

            if (completionResult?.code != 200) {
                isCallChatSuccess = false
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                LoggerUtil.e("completion Result Chat: $completionResult")
                mArrListPromt.firstOrNull { it.role == ChatRole.ASSISTANT.value }?.let {
                    it.content = completionResult.topicText
                } ?: run {
                    mArrListPromt.add(Message35Request(ChatRole.ASSISTANT.value, completionResult.topicText))
                }
                CommonSharedPreferences.getInstance().numberFreeChat1 = CommonSharedPreferences.getInstance().numberFreeChat1?.minus(1)
                isCallChatSuccess = true
                result.value = ResultDataDto.SuccessTopic(completionResult)
                resultText = completionResult.topicText ?: ""
            }
            mCurrentChatBaseDto.value?.apply {
                lastTimeUpdate = System.currentTimeMillis()
                chatDetail.findLast {
                    it.chatType == ChatType.RECEIVE.value
                }?.apply {
                    this.message = resultText
                    this.isTyping = false
                    this.chatUserNane = ""
                    this.isSeeMore = false
                }
            }
            mCurrentChatBaseDto.value?.let {
                dataRepository.updateChatDto(it)
                val currentBaseDto = reFormatDate(it).apply { it.chatDetail.findLast { it.chatType == ChatType.RECEIVE.value }?.isSeeMore = false}
                mCurrentChatBaseDto.value = currentBaseDto
            }
        }
        return result
    }

    @Synchronized
    fun completeChatWithTopic(
        context: Context?,
        valueError: String,
        isRegenerate: Boolean
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
            if (isRegenerate && isCallChatSuccess == true && mArrListPromt.size > 1) {
                mArrListPromt.removeLastOrNull()
            }
            var resultText = valueError
            val completionResult = withContext(Dispatchers.Default) {
                val completionRequest = Completion35Request()
                completionRequest.model = ModelData.GPT_35.value
                completionRequest.messages = mArrListPromt
                completionRequest.maxTokens = 1500

                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.callCompletion35(
                            1, "", OpenAiService(TOKEN, timeout = Constants.TIME_OUT, type = 0), completionRequest, it
                        )
                    }
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
            var data = completionResult?.choices?.first()?.message?.content
            var mIsCallMore = false
            completionResult?.choices?.firstOrNull()?.finishReason?.let {
            }
//            if (IapUtils.isVipChat()) data = data?.substring(0, data.lastIndexOf(".").plus(1))
            if (data.isNullOrBlank()) {
                isCallChatSuccess = false
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                mArrListPromt.add(
                    Message35Request(
                        completionResult?.choices?.getOrNull(0)?.message?.role,
                        completionResult?.choices?.getOrNull(0)?.message?.content
                    )
                )
                CommonSharedPreferences.getInstance().numberFreeChat1 = CommonSharedPreferences.getInstance().numberFreeChat1?.minus(1)
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
    fun completeQRetrofitHandler(
        context: Context?,
        input: String,
        valueError: String,
        isNewChat: Boolean,
        topicType: Int,
        fromSummary: SummaryHistoryDto? = null
    ): LiveData<ResultDataDto> {
        return completeQRetrofit35(context, input, valueError, isNewChat, topicType, fromSummary)
    }

    private fun completeQRetrofit35(
        context: Context?,
        input: String,
        valueError: String,
        isNewChat: Boolean,
        topicType: Int,
        fromSummary: SummaryHistoryDto? = null
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        val resultDataDto = ChatDetailDto(
            "",
            System.currentTimeMillis(),
            "",
            true,
            ChatType.RECEIVE.value,
            input,
            isSeeMore = false,

        )
        uiScope.launch(Dispatchers.Main) {
            if (isNewChat && isTextInputCopy != true) {
                mCurrentChatBaseDto.value = ChatBaseDto(
                    System.currentTimeMillis(), arrayListOf(
                        initChatDetailDto(input, ChatType.SEND.value),
                        resultDataDto
                    ),
                    topicType = topicType,
                    lastTimeUpdate = System.currentTimeMillis()
                )
            } else {
                mCurrentChatBaseDto.value?.apply {
                    lastTimeUpdate = System.currentTimeMillis()
                    chatDetail = ArrayList(chatDetail).apply {
                        add(initChatDetailDto(input, ChatType.SEND.value, input))
                        add(resultDataDto)
                    }
                }
            }
            mCurrentChatBaseDto.value?.let {
                fromSummary?.let { data ->
                    data.chatDetail = it.chatDetail
                    data.suggestList?.let { it1 ->
                        val arrSuggest = ArrayList(it1)
                        arrSuggest.remove(input)
                        data.suggestList = arrSuggest
                    }
                    dataRepository.updateSummaryHistoryDto(data)
                } ?: run {
                    if (isNewChat) {
                        dataRepository.insertChat(it)
                    } else {
                        dataRepository.updateChatDto(it)
                    }
                }
                mCurrentChatBaseDto.value = reFormatDate(it)
            }
            var resultText = valueError
            val completionResult = withContext(Dispatchers.Default) {
                if (mArrListPromt.size >= (limitSavePrevConversation)) {
                    mArrListPromt.removeFirst()
                    mArrListPromt.removeFirst()
                }
                mArrListPromt.add(Message35Request(ChatRole.USER.value, input))
                val completionRequest = Completion35Request()
                completionRequest.model = ModelData.GPT_35.value
                completionRequest.messages = mArrListPromt
                completionRequest.maxTokens =
                    1500

                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let { sharef ->
                        fromSummary?.let {
                            OpenAIHolder.completeSummaryChat(
                                1, input, OpenAiService(TOKEN, timeout = Constants.TIME_OUT, type = 0), completionRequest, sharef
                            )
                        } ?: run {
                            if (CommonSharedPreferences.getInstance().modelChatGpt == Constants.MODEL_CHAT.GPT_3_5){
                                chatRepository.createCompletionV1Chat(completionRequest).data
                            }
                            else chatRepository.createCompletionV1ChatGPT4(completionRequest).data

//                            OpenAIHolder.callCompletion35(
//                                1, input, OpenAiService(TOKEN, timeout = Constants.TIME_OUT, type = 0), completionRequest, sharef
//                            )
                        }
                    }
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
            var data = completionResult?.choices?.first()?.message?.content
            var mIsCallMore = false
           // if (IapUtils.isVipChat()) data = data?.substring(0, data.lastIndexOf(".").plus(1))
            if (data.isNullOrBlank()) {
                isCallChatSuccess = false
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                mArrListPromt.add(
                    Message35Request(
                        completionResult?.choices?.getOrNull(0)?.message?.role,
                        completionResult?.choices?.getOrNull(0)?.message?.content
                    )
                )
                CommonSharedPreferences.getInstance().numberFreeChat1 = CommonSharedPreferences.getInstance().numberFreeChat1?.minus(1)
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
                fromSummary?.let { data ->
                    data.chatDetail = it.chatDetail
                    dataRepository.updateSummaryHistoryDto(data)
                } ?: run {
                    dataRepository.updateChatDto(it)
                }
                val currentBaseDto = reFormatDate(it).apply { it.chatDetail.findLast { it.chatType == ChatType.RECEIVE.value }?.isSeeMore = mIsCallMore}
                mCurrentChatBaseDto.value = currentBaseDto
            }
        }
        return result
    }
    // call chat second
    private fun completeQRetrofitMore(
        context: Context?,
        input: String,
    ) {
        uiScope.launch(Dispatchers.Main) {
            val completionResult = withContext(Dispatchers.Default) {
                val completionRequest = CompletionRequest()
                completionRequest.model = ModelData.GPT_D.value
                completionRequest.prompt = input
                completionRequest.echo = false
                completionRequest.temperature = 0.9
                completionRequest.topP = 1.0
                completionRequest.bestOf = 1
                completionRequest.stop = listOf("User:", "Assistant:")
                completionRequest.presencePenalty = 0.6
                completionRequest.maxTokens =  950
                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.callCompletionMore(1, input, OpenAiService(TOKEN, timeout = 60, type = 0), completionRequest
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }

            val data = completionResult?.choices?.first()?.text
            if (data.isNullOrBlank()) {
                return@launch
            }
            mMessageMore.value = data.toString()
        }
    }
    //get token time
    fun callGetTimeStamp() {
        val time = CommonSharedPreferences.getInstance().timeStartGetTime
        if (System.currentTimeMillis() - time > Constants.TIME_8_MINUS) {
            CommonSharedPreferences.getInstance().timeStartGetTime = System.currentTimeMillis()
            uiScope.launch(Dispatchers.Main) {
                val data = withContext(Dispatchers.Default) {
                    try {
                        OpenAIHolder.callGetTime(mTimeStampService)
                    } catch (e: Throwable) {
                        null
                    }
                }
                data?.token?.let {
                    CommonSharedPreferences.getInstance().timeStamp = it
                }
            }
        }
    }

    fun createAiArt(
        input: String,
        modelId: String,
        width: Int,
        height: Int
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        uiScope.launch(Dispatchers.Main) {
            val generateArtResult = withContext(Dispatchers.Default) {
                val generateArtRequest = GenerateArtRequest()
                generateArtRequest.model = modelId
                generateArtRequest.messageId = ""
                generateArtRequest.prompt = input
                generateArtRequest.width = width
                generateArtRequest.height = height
                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.generateAiArt(
                            input,
                            OpenAiService(TOKEN, timeout = 60, type = 0),
                            generateArtRequest,
                            it
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            if (generateArtResult == null || generateArtResult.attachments?.get(0)?.url.isNullOrBlank()) {
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                mGenerateArtResult = generateArtResult
                position = 1
                result.value =
                    ResultDataDto.SuccessImage(generateArtResult.attachments?.get(0)?.url ?: "")
                CommonSharedPreferences.getInstance().setListGeneratePhoto(generateArtResult.attachments?.get(0)?.url)
            }

        }
        return result

    }

    fun getListStyleArt(context: Context?) {
        var listCurrent = arrayListOf<StyleArtDto>()
        uiScope.launch(Dispatchers.Main) {
            listCurrent = withContext(Dispatchers.Default) {
                StyleArtHelper.getListStyleArt(context)
            }
            currentStyleArt.value = listCurrent
        }
    }

    fun updateListStyleArt(context: Context?, data: StyleArtDto) {
        val listCurrent = arrayListOf<StyleArtDto>()
        var isDataExist = false
        currentStyleArt.value?.forEach {
            if (it.name == data.name) {
                isDataExist = true
            } else {
                listCurrent.add(StyleArtDto(it.resID, it.name, it.modelId, false))
            }
        }
        if (!isDataExist)
            listCurrent.removeLast()
        listCurrent.add(0, StyleArtDto(data.resID, data.name, data.modelId ,true))
        currentStyleArt.value = listCurrent

        uiScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Default) {
                CommonSharedPreferences.getInstance().setListStyleArt(listCurrent)
            }
        }

    }

    fun updateChatNumber() {
        mChatNumber.value = (mChatNumber.value ?: 1) - 1
    }

    fun initChatSummary(data: SummaryHistoryDto?) {
        data?.let {
            it.chatDetail.forEach {
                val role = if (it.chatType == ChatType.SEND.value) ChatRole.USER.value else ChatRole.SYSTEM.value
                mArrListPromt.add(Message35Request(role = role, content = "${it.message}"))
            }
            mArrListPromt.firstOrNull()?.content = it.summaryContent
            val sizeArrPromt = mArrListPromt.size.minus(limitSavePrevConversation)
            if (sizeArrPromt > 0) {
                for (i in 0..sizeArrPromt) {
                    mArrListPromt.removeAt(1)
                }
            }
            mCurrentChatBaseDto.value = it.convertToChatBaseDto()
        }
    }

    fun getModelChat(): String = CommonSharedPreferences.getInstance().modelChatGpt
    fun setModelChat(value: String) {
        CommonSharedPreferences.getInstance().modelChatGpt = value
    }

    private fun initChatDetailDto(mes: String, chatType: Int, userName: String = ""): ChatDetailDto {
        return  ChatDetailDto(
            mes, System.currentTimeMillis(), DateUtils.parseTime(mTextAtTime), false, chatType, userName
        )
    }

    companion object {
        private const val TOKEN = "wIX1xqLnKnprsmNMw/bMiA=="
    }
}
