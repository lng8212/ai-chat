package com.longkd.chatgpt_openai.feature.summary

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ChatType
import com.longkd.chatgpt_openai.base.model.ModelData
import com.longkd.chatgpt_openai.base.model.SummaryFileResponse
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.DateUtils
import com.longkd.chatgpt_openai.open.ChatRepository
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Message35Request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SummaryFileViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel(dataRepository) {

    private val _listSummaryHistory = MutableLiveData<ArrayList<SummaryHistoryDto>>()

    private val _summaryFileDto = MutableLiveData<SummaryHistoryDto?>()
    val summaryFileDto: LiveData<SummaryHistoryDto?> = _summaryFileDto

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private var typeSummary: String? = Constants.TYPE_SUMMARY.SUMMARY_FILE
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
            val result = withContext(Dispatchers.Default) {
                chatRepository.uploadSummaryFile(data.filePaths.first()).data
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
            val result = withContext(Dispatchers.Default) {
                try {
                    val completionRequest = Completion35Request()
                    completionRequest.model = ModelData.GPT_35.value
                    completionRequest.messages =
                        arrayListOf(Message35Request("system", summaryContent))
                    completionRequest.maxTokens = 3000
                    chatRepository.uploadSummaryText(completionRequest).data
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            errorCode.value = result?.code
            handleSaveSummaryResponse(null, summaryContent, result, context)
        }
    }

    private fun handleSaveSummaryResponse(
        data: SummaryHistoryDto? = null,
        summaryContent: String? = null,
        result: SummaryFileResponse?,
        context: Context?
    ) {
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
                    context?.getString(R.string.str_file_summary) + " " + result.summaryText?.replace(
                        "TLDR:",
                        ""
                    ),
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
        CommonSharedPreferences.getInstance().timeStamp = System.currentTimeMillis().toString()
        var timeInMills = CommonSharedPreferences.getInstance().timeSaveSummaryFile
        if (timeInMills == -1L || timeInMills == null) {
            CommonSharedPreferences.getInstance().timeSaveSummaryFile = System.currentTimeMillis()
            timeInMills = System.currentTimeMillis()
        }
        CommonSharedPreferences.getInstance().timeSaveSummaryFile = System.currentTimeMillis()
        CommonSharedPreferences.getInstance().apply {
            setNumberSummaryFile(summaryConfigData)
        }
    }


    fun checkTimesSummaryFile(): Boolean {
        CommonSharedPreferences.getInstance().apply {
            return getNumberSummaryFile(summaryConfigData) > 0

        }
    }

    fun resetNumberSummaryFile() {
        CommonSharedPreferences.getInstance().apply {
            timesSummary.value = getNumberSummaryFile(summaryConfigData)
        }
    }

    companion object {
        const val TOKEN_PAKE = "wIX1xqLnKnprsmNMw/bMiA=="
    }
}