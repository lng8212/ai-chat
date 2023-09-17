package com.longkd.base_android.base.locale

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

/**
 * @Author: longkd
 * @Since: 23:05 - 11/08/2023
 */

class LocaleManager private constructor(private val context: Context) {

    companion object {
        fun of(context: Context): LocaleManager = LocaleManager(context)
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun updateResourceConfiguration(locale: Locale): Context {
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun updateResourceConfigurationLegacy(locale: Locale): Context {
        return context.apply {
            val config = Configuration(context.resources.configuration)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    fun updateBaseContextLocale(locale: Locale): Context {
        Locale.setDefault(locale)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourceConfiguration(locale)
        } else {
            updateResourceConfigurationLegacy(locale)
        }
    }

    fun updateBaseContextLanguage(languageCode: String): Context {
        val locale = Locale(languageCode)
        return updateBaseContextLocale(locale)
    }

}
