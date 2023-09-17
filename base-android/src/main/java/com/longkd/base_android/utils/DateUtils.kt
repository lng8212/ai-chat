package com.longkd.base_android.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @Author: longkd
 * @Since: 10:39 - 12/08/2023
 */
object DateUtils {
    /**
     *
     * ## Date and Time format explanation
     *
     *  ----------------------------------------------------------------------
     *  format:
     *  EEE   : Day ( Mon )
     *
     *  MMMM  : Full month name ( December )    `MMMM February`
     *
     *  MMM   : Month in words ( Dec )
     *
     *  MM    : Month ( 12 )
     *
     *  dd    : Day in 2 chars ( 03 )
     *
     *  d     : Day in 1 char (3)
     *
     *  HH    : Hours ( 12 )
     *
     *  mm    : Minutes ( 50 )
     *
     *  ss    : Seconds ( 34 )
     *
     *  yyyy  : Year ( 2020 )                 `both yyyy and YYYY are same`
     *
     *  YYYY  : Year ( 2020 )
     *
     *  zzz   : GMT+05:30
     *
     *  a     : ( AM / PM )
     *
     *  aa    : ( AM / PM )
     *
     *  aaa   : ( AM / PM )
     *
     *  aaaa  : ( AM / PM )
     *
     * ----------------------------------------------------------------------------
     */

    fun format(time: Long, format: String, locale: Locale = Locale.ROOT): String {
        return SimpleDateFormat(format, locale).format(time)
    }

    fun format_mm_ss(time: Long, locale: Locale = Locale.ROOT): String {
        val format = "mm:ss"
        return format(time, format, locale)
    }

    fun format_HH_mm(time: Long, locale: Locale = Locale.ROOT): String {
        val format = "HH : mm"
        return format(time, format, locale)
    }

    fun format_dd_MMM_yyyy(time: Long, locale: Locale = Locale.ROOT): String {
        val format = "dd MMM yyyy"
        return format(time, format, locale)
    }

    fun parse(dateString: String, format: String, locale: Locale = Locale.ROOT): Date? {
        val sdf = SimpleDateFormat(format, locale)
        return try {
            sdf.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentDate(): String {
        return format_dd_MMM_yyyy(System.currentTimeMillis())
    }

    fun getCurrentTime(): String {
        return format_HH_mm(System.currentTimeMillis())
    }
}