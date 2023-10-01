package com.longkd.chatgpt_openai.base.data

import androidx.room.*
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.model.ChatBaseDto

@Dao
interface CoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dtoList: List<ChatBaseDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(dto: ChatBaseDto)

    @Update
    fun updateChatDto(dto: ChatBaseDto)

    @Query("DELETE FROM chat_base_dto WHERE chatID = (:path)")
    suspend fun delete(path: Long)

    @Query("SELECT * FROM chat_base_dto ORDER BY chatId DESC")
    fun getAllChat(): List<ChatBaseDto>

    @Query("SELECT * FROM chat_base_dto WHERE chatId= :chatId")
    fun getChatDto(chatId: Long): ChatBaseDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSummary(dtoList: List<SummaryHistoryDto>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChatSummary(dto: SummaryHistoryDto)

    @Update
    fun updateSummaryDto(dto: SummaryHistoryDto)

    @Query("DELETE FROM summary_file_dto WHERE md5 in (:md5)")
    suspend fun deletesummary(md5: List<String>)

    @Query("SELECT * FROM summary_file_dto ORDER BY fileName DESC")
    fun getAllSummaryFile(): List<SummaryHistoryDto>

    @Query("SELECT * FROM summary_file_dto WHERE md5= :md5")
    fun getSummaryDto(md5: String): SummaryHistoryDto?
}
