package com.longkd.chatgpt_openai.feature.home_new.gallery

import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.model.ChatType
import com.longkd.chatgpt_openai.base.model.ModelData
import com.longkd.chatgpt_openai.base.model.SummaryData
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.DateUtils
import com.longkd.chatgpt_openai.open.chat.ChatRepository
import com.longkd.chatgpt_openai.open.dto.completion.Completion35Request
import com.longkd.chatgpt_openai.open.dto.completion.Message35Request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ListGalleryViewModel
@Inject constructor(
    private val dataRepository: DataRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel() {

    private var imagesLiveData: MutableLiveData<List<SummaryData>?> = MutableLiveData()
    fun getImageList(): MutableLiveData<List<SummaryData>?> = imagesLiveData

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _summaryFileDto = MutableLiveData<SummaryHistoryDto?>()
    val summaryFileDto: LiveData<SummaryHistoryDto?> = _summaryFileDto

    private fun loadImagesfromSDCard(context: Context): ArrayList<SummaryData> {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val listOfAllImages = ArrayList<SummaryData>()
        val projection = arrayOf(MediaStore.MediaColumns.DATA)

        try {
            MediaScannerConnection.scanFile(
                context, arrayOf(collection.path), arrayOf("*/*"), null
            )
            val se = MediaStore.Files.FileColumns.SIZE + ">0"
            val resolver = context.contentResolver

            val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
            val query = resolver?.query(
                collection, projection, se, null, sortOrder
            )
            query?.use { cursor ->
                while (cursor.moveToNext()) {
                    val path = cursor.getString(0)
                    val file = File(path)
                    if (file.length() > 0 && !path.isNullOrBlank()) {
                        listOfAllImages.add(
                            SummaryData(uri = path)
                        )
                    }
                }
            }
            query?.close()
        } catch (ex: Exception) {
            return arrayListOf()
        }
        return listOfAllImages
    }

    fun getAllImages(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            imagesLiveData.value = withContext(Dispatchers.IO) {
                loadImagesfromSDCard(context)
            }
        }
    }

    fun insertSummary(summaryDto: SummaryHistoryDto) {
        uiScope.launch(Dispatchers.Main) {
            dataRepository.insertSummaryFile(summaryDto)
        }
    }

    @Synchronized
    fun uploadSummaryText(summaryContent: String, context: Context?, listUri: List<SummaryData>) {
        uiScope.launch(Dispatchers.Main) {
            val result =  withContext(Dispatchers.Default) {
                try {
                    val completionRequest = Completion35Request()
                    completionRequest.model = ModelData.GPT_35.value
                    completionRequest.messages = arrayListOf(Message35Request("system", summaryContent))
                    completionRequest.maxTokens = 2000
                    chatRepository.uploadSummaryText(completionRequest).data
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
            }
            result?.let {
                if (it.code != 200) {
                    _showLoading.postValue(false)
                    _summaryFileDto.postValue(null)
                } else {
                    CommonSharedPreferences.getInstance().apply {
                            setNumberSummaryFile(getNumberSummaryFile(summaryConfigData).minus(1))
                    }
                    val chatBaseDto = ChatDetailDto(
                        context?.getString(R.string.str_file_summary) + " " + result.summaryText?.replace("TLDR:", ""),
                        System.currentTimeMillis(),
                        DateUtils.parseTime("at"),
                        false,
                        ChatType.RECEIVE.value,
                    )
                    val summaryDto = SummaryHistoryDto(
                        System.currentTimeMillis().toString(),
                        "${listUri.size} images",
                        listUri.map { it.uri ?: "" },
                        listOf(chatBaseDto),
                        result.suggestList,
                        System.currentTimeMillis(),
                        summaryContent
                    )
                    insertSummary(summaryDto)
                    _summaryFileDto.postValue(summaryDto)
                    _showLoading.postValue(false)
                }
            } ?: run {
                _showLoading.postValue(false)
                _summaryFileDto.postValue(null)
            }
        }
    }

    fun showLoading(b: Boolean) {
        _showLoading.postValue(true)
    }
}