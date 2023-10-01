package com.longkd.chatgpt_openai.base.util

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getFormattedDate(j: Long): String {
        val instance = Calendar.getInstance()
        instance.timeInMillis = j
        return if (DateUtils.isToday(j)) {
           "Today, " + DateFormat.format("h:mm aa", instance).toString()
        } else if (isYesterday(j)) {
            "Yesterday, " + DateFormat.format("h:mm aa", instance).toString()
        } else {
            DateFormat.format("d MMM, yyyy, HH:mm", instance).toString()
        }
    }

    private fun isYesterday(j: Long): Boolean {
        return DateUtils.isToday(j + 86400000)
    }

    fun parseTime(textAtTime: String): String {
        return try {
            val formatter = SimpleDateFormat("d MMM, yyyy ; HH:mm", Locale.US)
            val now = Date()
            formatter.format(now).toString().replace(";", textAtTime)
        } catch (e: Exception) {
            ""
        }
    }

    fun parceTimeToLocate(timeInMillis: Long): String {
        val date = Date()
        date.time = timeInMillis
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(date)
        val df = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone("UTC")
        val dateLocate = df.parse(timeStamp)
        df.timeZone = TimeZone.getDefault()
        return df.format(dateLocate)
    }
}