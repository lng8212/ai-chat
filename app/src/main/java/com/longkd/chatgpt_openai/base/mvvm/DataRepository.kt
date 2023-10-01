package com.longkd.chatgpt_openai.base.mvvm

import android.content.Context
import com.longkd.chatgpt_openai.base.data.ChatDatabase
import com.longkd.chatgpt_openai.base.data.CoreDao
import com.longkd.chatgpt_openai.base.model.ChatBaseDto
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base DataRepository, you can extend this
 * @Param coreDao
 */
open class DataRepository(private val coreDao: CoreDao) {
    companion object {

        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = ChatDatabase.getInstance(context)
                    instance = DataRepository(database.demoDao())

                }
                return instance
            }
        }
    }

    suspend fun insertChat(chatBaseDto: ChatBaseDto) {
        withContext(Dispatchers.IO) {
            coreDao.insertChat(chatBaseDto)
        }
    }

    suspend fun updateChatDto(chatBaseDto: ChatBaseDto) {
        withContext(Dispatchers.IO) {
            coreDao.updateChatDto(chatBaseDto)
        }
    }

    suspend fun getChatDto(): ChatBaseDto? {
        return withContext(Dispatchers.IO) {
            coreDao.getAllChat().firstOrNull()
        }
    }

    suspend fun getLastChatDto(): ChatBaseDto? {
        return withContext(Dispatchers.IO) {
            coreDao.getAllChat().lastOrNull()
        }
    }

    suspend fun getChatDto(chatId: Long): ChatBaseDto? {
        return withContext(Dispatchers.IO) {
            coreDao.getChatDto(chatId)
        }
    }

    suspend fun getAllChatDto(): List<ChatBaseDto> {
        return withContext(Dispatchers.IO) {
            coreDao.getAllChat()
        }
    }
    suspend fun removeChatDto(id:Long) {
        return withContext(Dispatchers.IO) {
            coreDao.delete(id)
        }
    }

    suspend fun insertSummaryFile(summaryDto: SummaryHistoryDto) {
        withContext(Dispatchers.IO) {
            coreDao.insertChatSummary(summaryDto)
        }
    }

    suspend fun getAllSummaryDto(): List<SummaryHistoryDto> {
        return withContext(Dispatchers.IO) {
            coreDao.getAllSummaryFile()
        }
    }

    suspend fun removeHistorySummmaryDto(arrMD5: List<String>) {
        return withContext(Dispatchers.IO) {
            coreDao.deletesummary(arrMD5)
        }
    }

    suspend fun updateSummaryHistoryDto(data: SummaryHistoryDto) {
        withContext(Dispatchers.IO) {
            coreDao.updateSummaryDto(data)
        }
    }
}
