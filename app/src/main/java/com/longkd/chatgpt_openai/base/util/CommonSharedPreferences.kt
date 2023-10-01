package com.longkd.chatgpt_openai.base.util

import android.content.Context
import android.content.SharedPreferences
import com.longkd.chatgpt_openai.base.data.TopicDto
import com.longkd.chatgpt_openai.feature.art.StyleArtDto
import com.longkd.chatgpt_openai.feature.art.StyleArtHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CommonSharedPreferences {
    private var sharedPreferences: SharedPreferences? = null

    companion object {
         private const val SHARED_PREFERENCES_NAME = "chat_app_sf"
        private const val KEY_LIMIT_LAST_CONVERSATI = "key_limit_last_conversati"
        private const val KEY_TITLE_APP_CHAT = "key_title_app_chat"
        private const val KEY_TOKEN_LIMIT_WITH_PREMIUM = "key_token_limit_with_premium"
        private const val KEY_TOKEN_LIMIT_NO_PREMIUM = "key_token_limit_no_premium"
        private const val KEY_SAVE_GENERATE_PHOTO = "key_save_generate_photo"
        private const val KEY_SAVE_SUMMARY_FILE_TIME = "key_save_summary_file_time"
        private const val KEY_SAVE_NUMBER_SUMMARY_FILE = "key_save_number_summary_file"
        private const val KEY_NUMBER_SUMMARY_FILE_WITHOUT_PREMIUM = "key_number_summary_file_without_premium"
        private const val KEY_MODEL_CHAT_GPT = "key_model_chat_gpt"
        private const val KEY_FIRST_DISPLAY_SUMMARY_FILE = "key_first_display_summary_file"
        private const val KEY_SAVE_STATUS_PREMIUM = "key_save_status_premium"
        private const val KEY_SAVE_STATUS_PREMIUM_DATA = "key_save_status_premium_data"
        private const val KEY_SAVE_NUMBER_FREE_CHAT = "key_save_number_free_chat"
        private lateinit var instance: CommonSharedPreferences

        fun init(context: Context) {
            instance = CommonSharedPreferences()
            instance.sharedPreferences = context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        @Synchronized
        fun getInstance(context: Context? = null): CommonSharedPreferences {
            if (!this::instance.isInitialized) {
                instance = CommonSharedPreferences()
                if (context != null) {
                    init(context)
                }
                return instance
            }
            return instance
        }
    }

    fun getSharedPreferences() =
        instance.sharedPreferences


    fun checkKey(key: String): Boolean? {
        return getSharedPreferences()?.contains(key)
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getSharedPreferences()?.getBoolean(key, defaultValue) ?: defaultValue

    fun putBoolean(key: String, value: Boolean) {
        val editor = getSharedPreferences()?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int =
        getSharedPreferences()?.getInt(key, defaultValue) ?: defaultValue

    fun putInt(key: String, value: Int) {
        val editor = getSharedPreferences()?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun getLong(key: String, defaultValue: Long): Long =
        getSharedPreferences()?.getLong(key, defaultValue) ?: defaultValue

    fun putLong(key: String, value: Long) {
        val editor = getSharedPreferences()?.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    fun getString(key: String, defaultValue: String? = Strings.EMPTY): String =
        getSharedPreferences()?.getString(key, defaultValue) ?: Strings.EMPTY

    fun putString(key: String, value: String) {
        val editor = getSharedPreferences()?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun removeKey(key: String) {
        getSharedPreferences()?.edit()?.remove(key)?.apply()
    }

    fun getLanguage(): String? {
        return getSharedPreferences()?.getString("xx_language", "en")
    }

    fun saveLanguage(language: String?) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putString("xx_language", language)
        editor?.apply()
    }

    fun getLanguageIndex(): Int {
        return getSharedPreferences()?.getInt("xx_languageindex", 0) ?: 0
    }

    fun saveLanguageIndex(language: Int) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putInt("xx_languageindex", language)
        editor?.apply()
    }

    fun getFirstLanguage(): Boolean? {
        return getSharedPreferences()?.getBoolean("key_first_language", false)
    }

    fun setFirstLanguage(boolean: Boolean) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putBoolean("key_first_language", boolean)
        editor?.apply()
    }
    fun getShowDirector(): Boolean? {
        return getSharedPreferences()?.getBoolean("key_show_director", false)
    }

    fun setShowDirector(boolean: Boolean) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putBoolean("key_show_director", boolean)
        editor?.apply()
    }

    fun getShowDialogWidget(): Boolean? {
        return getSharedPreferences()?.getBoolean("key_show_dialog_widget", false)
    }

    fun setShowDialogWidget(boolean: Boolean) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putBoolean("key_show_dialog_widget", boolean)
        editor?.apply()
    }

    fun getTimeFirstStartApp(): Long? {
        return getSharedPreferences()?.getLong("time_first_start_app", 0L)
    }

    fun setTimeFirstStartApp(time: Long) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putLong("time_first_start_app", time)
        editor?.apply()
    }

    fun getTimeStartApp(): Long? {
        return getSharedPreferences()?.getLong("time_start_app", System.currentTimeMillis())
    }

    fun setTimeStartApp(time: Long) {
        val editor: SharedPreferences.Editor? = getSharedPreferences()?.edit()
        editor?.putLong("time_start_app", time)
        editor?.apply()
    }

    var numberLimitedUse: Int
        get() = getSharedPreferences()?.getInt("number_limited_use", 1) ?: 1
        set(number) {
            getSharedPreferences()?.edit()?.putInt("number_limited_use", number)?.apply()
        }
    var numberChatReset: Int
        get() = getSharedPreferences()?.getInt(Constants.NUMBER_CHAT_RESET, 3) ?: 3
        set(number) {
            getSharedPreferences()?.edit()?.putInt(Constants.NUMBER_CHAT_RESET, number)?.apply()
        }
    var numberFreeChat: Int
        get() = getSharedPreferences()?.getInt(Constants.NUMBER_FREE_CHAT, 5) ?: 5
        set(number) {
            getSharedPreferences()?.edit()?.putInt(Constants.NUMBER_FREE_CHAT, number)?.apply()
        }

    var numberFreeChat1: Int?
        get() = getSharedPreferences()?.getInt(KEY_SAVE_NUMBER_FREE_CHAT, numberFreeChat)
        set(number) {
            getSharedPreferences()?.edit()?.putInt(KEY_SAVE_NUMBER_FREE_CHAT, number.orZero())?.apply()
        }

    var numberFreeGenerateArt: Int
        get() = getSharedPreferences()?.getInt(Constants.NUMBER_FREE_GENERATE_ART, 2) ?: 2
        set(number) {
            getSharedPreferences()?.edit()?.putInt(Constants.NUMBER_FREE_GENERATE_ART, number)?.apply()
        }

    var numberStartApp: Int
        get() = getSharedPreferences()?.getInt("number_start_app", 0) ?: 0
        set(number) {
            getSharedPreferences()?.edit()?.putInt("number_start_app", number)?.apply()
        }
    var userRate: String
        get() = getSharedPreferences()?.getString("key_user_rate", Constants.USER_RATE_HIGHER)
            ?: Constants.USER_RATE_HIGHER
        set(user_rate) {
            getSharedPreferences()?.edit()?.putString("key_user_rate", user_rate)?.apply()
        }

    var configEnableNewIap: Boolean
        get() = getSharedPreferences()?.getBoolean(Constants.CONFIG_ENABLE_NEW_IAP, false) ?: false
        set(value) {
            getSharedPreferences()?.edit()?.putBoolean(Constants.CONFIG_ENABLE_NEW_IAP, value)?.apply()
        }
    var configEnableScriptIap: Boolean
        get() = getSharedPreferences()?.getBoolean(Constants.CONFIG_ENABLE_SCRIPT_IAP, false) ?: false
        set(value) {
            getSharedPreferences()?.edit()?.putBoolean(Constants.CONFIG_ENABLE_SCRIPT_IAP, value)?.apply()
        }

    fun setTopics(list: ArrayList<TopicDto>) {
        val gson = Gson()
        val json = gson.toJson(list)
        getSharedPreferences()?.edit()?.putString("list_type_topic", json)?.apply()
    }

    fun getTopics(): ArrayList<TopicDto> {
        return try {
            val gson = Gson()
            val json = getSharedPreferences()?.getString("list_type_topic", null)
            val type = object : TypeToken<ArrayList<TopicDto>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            arrayListOf<TopicDto>()
        }
    }

    fun setListStyleArt(list: ArrayList<StyleArtDto>) {
        val gson = Gson()
        val json = gson.toJson(list)
        getSharedPreferences()?.edit()?.putString("list_style_art", json)?.apply()
    }

    var timeStamp: String
        get() = getSharedPreferences()?.getString("time_stamp", "") ?: ""
        set(time_stamp) {
            getSharedPreferences()?.edit()?.putString("time_stamp", time_stamp)?.apply()
        }
    var timeStartGetTime: Long
        get() = getSharedPreferences()?.getLong("time_start_get_time", 0L) ?: 0L
        set(time_stamp) {
            getSharedPreferences()?.edit()?.putLong("time_start_get_time", time_stamp)?.apply()
        }
    var timeSubscriptionWeekArt: Long
        get() = getSharedPreferences()?.getLong("time_subscription_week_art", 0L) ?: 0L
        set(time_stamp) {
            getSharedPreferences()?.edit()?.putLong("time_subscription_week_art", time_stamp)?.apply()
        }

    var limitSavePrevConversation: Int
    get() = getSharedPreferences()?.getInt(KEY_LIMIT_LAST_CONVERSATI, 10) ?: 10
    set(value) {
        getSharedPreferences()?.edit()?.putInt(KEY_LIMIT_LAST_CONVERSATI, value)?.apply()
    }

    var titleAppChat: String?
        get() = getSharedPreferences()?.getString(KEY_TITLE_APP_CHAT, "") ?: ""
        set(time_stamp) {
            getSharedPreferences()?.edit()?.putString(KEY_TITLE_APP_CHAT, time_stamp)?.apply()
        }

    var isShowBottomDialogExitApp: Boolean
    get() = getSharedPreferences()?.getBoolean(Constants.IS_SHOW_BOTTOM_DIALOG_EXIT_APP, false) ?: false
    set(value) {
        getSharedPreferences()?.edit()?.putBoolean(Constants.IS_SHOW_BOTTOM_DIALOG_EXIT_APP, value)?.apply()
    }

    var configSummaryFileSize: Int
        get() = getSharedPreferences()?.getInt(Constants.CONFIG_SUMMARY_FILE_SIZE, 1) ?: 1
        set(value) {
            getSharedPreferences()?.edit()?.putInt(Constants.CONFIG_SUMMARY_FILE_SIZE, value)?.apply()
        }

    var summaryConfigData: Int
        get() = getSharedPreferences()?.getInt(Constants.CONFIG_NUMBER_OF_FILE_SUMMARIES, 5) ?: 5
        set(value) {
            getSharedPreferences()?.edit()?.putInt(Constants.CONFIG_NUMBER_OF_FILE_SUMMARIES, value)?.apply()
        }

    var timeShowNextAds: Int
        get() = getSharedPreferences()?.getInt(Constants.CONFIG_TIME_SHOW_NEXT_ADS, 15) ?: 15
        set(value) {
            getSharedPreferences()?.edit()?.putInt(Constants.CONFIG_TIME_SHOW_NEXT_ADS, value)?.apply()
        }

    var numberSummaryWithoutPremium: Int
        get() = getSharedPreferences()?.getInt(KEY_NUMBER_SUMMARY_FILE_WITHOUT_PREMIUM, 1) ?: 1
        set(value) {
            getSharedPreferences()?.edit()?.putInt(KEY_NUMBER_SUMMARY_FILE_WITHOUT_PREMIUM, value)?.apply()
        }

    var timeSaveSummaryFile: Long?
    get() = getSharedPreferences()?.getLong(KEY_SAVE_SUMMARY_FILE_TIME, -1L)
    set(value) {
        getSharedPreferences()?.edit()?.putLong(KEY_SAVE_SUMMARY_FILE_TIME, value ?: -1L)?.apply()
    }

    var modelChatGpt: String
    get() = getSharedPreferences()?.getString(
        KEY_MODEL_CHAT_GPT,
        Constants.MODEL_CHAT.GPT_3_5
    ) ?: Constants.MODEL_CHAT.GPT_3_5
    set(value) {
        getSharedPreferences()?.edit()?.putString(KEY_MODEL_CHAT_GPT, value)?.apply()
    }


    var firstDisplayToolTipSummary: Boolean
    get() = getSharedPreferences()?.getBoolean(KEY_FIRST_DISPLAY_SUMMARY_FILE, true) ?: false
    set(value) {
        getSharedPreferences()?.edit()?.putBoolean(KEY_FIRST_DISPLAY_SUMMARY_FILE, value)?.apply()
    }

    fun getNumberSummaryFile(numberConfig: Int): Int {
        return getSharedPreferences()?.getInt(KEY_SAVE_NUMBER_SUMMARY_FILE, numberConfig) ?: numberConfig
    }

    fun setNumberSummaryFile(value: Int) {
        getSharedPreferences()?.edit()?.putInt(KEY_SAVE_NUMBER_SUMMARY_FILE, value)?.apply()
    }

    fun getListGeneratePhoto(): Set<String>? {
        return getSharedPreferences()?.getStringSet(KEY_SAVE_GENERATE_PHOTO, null)
    }

    fun setListGeneratePhoto(value: String?) {
        if(value.isNullOrBlank()) return
        val set: MutableSet<String> = HashSet()
        getListGeneratePhoto()?.let {
            set.addAll(it)
        }
        set.add(value)
        getSharedPreferences()?.edit()?.putStringSet(KEY_SAVE_GENERATE_PHOTO, set)?.apply()
    }

}