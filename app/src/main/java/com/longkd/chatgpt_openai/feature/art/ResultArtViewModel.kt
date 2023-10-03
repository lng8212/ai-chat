package com.longkd.chatgpt_openai.feature.art

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.base.OpenAIHolder
import com.longkd.chatgpt_openai.base.model.ErrorType
import com.longkd.chatgpt_openai.base.model.GenerateArtByVyroRequest
import com.longkd.chatgpt_openai.base.model.ResultDataDto
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.open.client.OpenAiService
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtRequest
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResultArtViewModel @Inject constructor(
    val dataRepository: DataRepository
) : BaseViewModel(dataRepository) {
    private var mTimeStampService = TimeStampService(TOKEN_PAKE, timeout = 60, type = 0)

    private var mGenerateArtResult: GenerateArtResult = GenerateArtResult()
    var mGenerateNumber: MutableLiveData<Int> = MutableLiveData()

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
                            OpenAiService(TOKEN_PAKE, timeout = 60, type = 0),
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
                result.value =
                    ResultDataDto.SuccessImage(generateArtResult.attachments?.get(0)?.url ?: "")
                CommonSharedPreferences.getInstance().setListGeneratePhoto(generateArtResult.attachments?.get(0)?.url)
            }

        }
        return result

    }

    fun generateArtByVyro(
        request: GenerateArtByVyroRequest
    ): LiveData<ResultDataDto> {
        val result: MutableLiveData<ResultDataDto> = MutableLiveData()
        uiScope.launch(Dispatchers.Main) {
            val generateArtResult = withContext(Dispatchers.Default) {
                try {
                    CommonSharedPreferences.getInstance().getSharedPreferences()?.let {
                        OpenAIHolder.generateArtByVyro(
                            OpenAiService(TOKEN_PAKE, timeout = 60, type = 0, apiVyro = true),
                            request,
                            it
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            if (generateArtResult == null || generateArtResult.code != 200) {
                result.value = ResultDataDto.Error(ErrorType.UNKNOWN)
            } else {
                result.value =
                    ResultDataDto.SuccessImageVyro(generateArtResult.data)
            }

        }
        return result

    }

    companion object {
        const val TOKEN_PAKE = "wIX1xqLnKnprsmNMw/bMiA=="
    }
}