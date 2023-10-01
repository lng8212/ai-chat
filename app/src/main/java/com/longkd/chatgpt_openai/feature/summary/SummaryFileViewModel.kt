package com.longkd.chatgpt_openai.feature.summary

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.OpenAIHolder
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.DateUtils
import com.longkd.chatgpt_openai.open.client.OpenAiService
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Message35Request
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ChatType
import com.longkd.chatgpt_openai.base.model.ModelData
import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SummaryFileViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel(dataRepository) {

    private val _listSummaryHistory = MutableLiveData<ArrayList<SummaryHistoryDto>>()
    private var mTimeStampService =
        TimeStampService("wIX1xqLnKnprsmNMw/bMiA==", timeout = 60, type = 0)

    private val _summaryFileDto = MutableLiveData<SummaryHistoryDto?>()
    val summaryFileDto: LiveData<SummaryHistoryDto?> = _summaryFileDto

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private var typeSummary: String ? = Constants.TYPE_SUMMARY.SUMMARY_FILE
    fun getListSummaryHistory(): LiveData<ArrayList<SummaryHistoryDto>> = _listSummaryHistory
    val arrRemoveSummary = MutableLiveData<ArrayList<SummaryHistoryDto>>(arrayListOf())
    val isRemoveSuccess = MutableLiveData<Boolean>()
    val timesSummary = MutableLiveData<Int>()
    val errorCode = MutableLiveData<Int>()

    fun getAllSummaryFile() {
        uiScope.launch(Dispatchers.Main) {
            val listSummaryHistory = arrayListOf<SummaryHistoryDto>()
            withContext(Dispatchers.Default) {
                dataRepository.getAllSummaryDto().forEach {
                    listSummaryHistory.add(it)
                }
            }
            _listSummaryHistory.value = listSummaryHistory
        }
    }

    fun insertSummary(summaryDto: SummaryHistoryDto) {
        uiScope.launch(Dispatchers.Main) {
            dataRepository.insertSummaryFile(summaryDto)
        }
    }

    fun deleteHistory() {
        uiScope.launch(Dispatchers.Main) {
            arrRemoveSummary.value?.map { it.md5 }?.let {
                withContext(Dispatchers.Default) {
                    dataRepository.removeHistorySummmaryDto(it)
                }
            }
            isRemoveSuccess.postValue(true)
        }
    }

    fun uploadSummaryFile(data: SummaryHistoryDto, context: Context?) {
        uiScope.launch(Dispatchers.Main) {
            _showLoading.postValue(true)
            typeSummary = Constants.TYPE_SUMMARY.SUMMARY_FILE
           val result =  withContext(Dispatchers.Default) {
                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.uploadSummaryFile(
                            OpenAiService(TOKEN_PAKE, timeout = Constants.TIME_OUT, type = 1),
                            data.filePaths.first(),
                            it
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            errorCode.value = result?.code
            handleSaveSummaryResponse(data, null, result, context)
        }
    }

    @Synchronized
    fun uploadSummaryText(summaryContent: String, context: Context?) {
        uiScope.launch(Dispatchers.Main) {
            typeSummary = Constants.TYPE_SUMMARY.SUMMARY_TEXT
            _showLoading.postValue(true)
            val result =  withContext(Dispatchers.Default) {
                try {
                    val completionRequest = Completion35Request()
                    completionRequest.model = ModelData.GPT_35.value
                    completionRequest.messages = arrayListOf(Message35Request("system", summaryContent))
                    completionRequest.maxTokens = 3000
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.uploadSummaryText(
                            1,
                            OpenAiService(TOKEN_PAKE, timeout = Constants.TIME_OUT, type = 0),
                            completionRequest,
                            it
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            errorCode.value = result?.code
            handleSaveSummaryResponse(null, summaryContent, result, context)
        }
    }

    private fun handleSaveSummaryResponse(data: SummaryHistoryDto? = null, summaryContent: String ? = null, result: SummaryFileResponse?, context: Context?) {
        result?.let {
            if (it.code != 200) {
                _showLoading.postValue(false)
                _summaryFileDto.postValue(null)
            } else {
                CommonSharedPreferences.getInstance().apply {
                        setNumberSummaryFile(getNumberSummaryFile(summaryConfigData).minus(1))
                    resetNumberSummaryFile()
                }
                val chatBaseDto = ChatDetailDto(
                    context?.getString(R.string.str_file_summary) + " " + result.summaryText?.replace("TLDR:", ""),
                    System.currentTimeMillis(),
                    DateUtils.parseTime("at"),
                    false,
                    ChatType.RECEIVE.value,
                )
                val summaryDto = when (typeSummary) {
                    Constants.TYPE_SUMMARY.SUMMARY_TEXT -> {
                        SummaryHistoryDto(
                            System.currentTimeMillis().toString(),
                            summaryContent,
                            listOf(),
                            listOf(chatBaseDto),
                            result.suggestList,
                            System.currentTimeMillis(),
                            summaryContent
                        )
                    }

                    else -> {
                        SummaryHistoryDto(
                            data?.md5 ?: System.currentTimeMillis().toString(),
                            data?.fileName,
                            data?.filePaths ?: listOf(),
                            listOf(chatBaseDto),
                            result.suggestList,
                            System.currentTimeMillis(),
                            result.summaryContent
                        )
                    }
                }
                insertSummary(summaryDto)
                _summaryFileDto.postValue(summaryDto)
                _showLoading.postValue(false)
            }
        } ?: run {
            _showLoading.postValue(false)
            _summaryFileDto.postValue(null)
        }
    }

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

                        var timeInMills = CommonSharedPreferences.getInstance().timeSaveSummaryFile
                        if (timeInMills == -1L || timeInMills == null) {
                            CommonSharedPreferences.getInstance().timeSaveSummaryFile = it.toLong()
                            timeInMills = it.toLong()
                        }
                        if (!android.text.format.DateUtils.isToday(timeInMills)) {
                            CommonSharedPreferences.getInstance().timeSaveSummaryFile = it.toLong()
                            CommonSharedPreferences.getInstance().apply {
                                setNumberSummaryFile(summaryConfigData)
                            }
                        }
                }
            }
        }
    }

    fun checkTimesSummaryFile(): Boolean {
        CommonSharedPreferences.getInstance().apply {
            return getNumberSummaryFile(summaryConfigData) > 0

        }
    }

    fun resetNumberSummaryFile() {
        CommonSharedPreferences.getInstance().apply {
                timesSummary.value =  getNumberSummaryFile(summaryConfigData)
        }
    }

    companion object {
        const val TOKEN_PAKE = "wIX1xqLnKnprsmNMw/bMiA=="
    }
}