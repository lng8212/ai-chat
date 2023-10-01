package com.longkd.chatgpt_openai.base.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class ListStringConverter {
    @TypeConverter
    fun fromList(value: List<String>): String {
        return try {
            val gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
            val type = object : TypeToken<List<String>>() {}.type
            gson.toJson(value, type)
        } catch (e: Exception) {
            ""
        }
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(value, type)
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}