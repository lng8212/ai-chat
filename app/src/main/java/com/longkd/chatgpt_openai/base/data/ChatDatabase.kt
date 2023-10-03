package com.longkd.chatgpt_openai.base.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.model.ChatBaseDto


@Database(entities = [ChatBaseDto::class, SummaryHistoryDto::class], version = 6, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun demoDao(): CoreDao
}
