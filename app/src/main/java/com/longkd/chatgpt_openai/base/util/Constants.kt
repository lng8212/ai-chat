package com.longkd.chatgpt_openai.base.util

class Constants {
    companion object {
        const val TAG = "xx"
        const val CHAT_MODE = "CHAT_MODE"
        const val THEME_MODE = "THEME_MODE"
        const val KEY_SHOW_RATE = "KEY_SHOW_RATE"
        const val ENABLE_ANIMATE_TEXT = "ENABLE_ANIMATE_TEXT"
        const val ENABLE_AUTO_SPEAK = "ENABLE_AUTO_SPEAK"
        const val ACTION_SUGGEST_RE_SUB = "ACTION_SUGGEST_RE_SUB"
        const val FIRST_SHOW_INTRO = "FIRST_SHOW_INTRO"
        const val URL_POLICY = ""
        const val URL_TERM_OF_SERVICE = ""
        const val USER_RATE_HIGHER = "user_rate_higher"
        const val KEY_WIDGET_CLICK = "key_widget_click"
        const val NUMBER_CHAT_RESET = "number_chat_reset"
        const val LIMIT_SAVE_PREV_CONVERSATION = "limit_save_prev_conversation"
        const val NUMBER_FREE_CHAT = "number_free_chat"
        const val NUMBER_FREE_GENERATE_ART = "number_free_generate_art"
        const val NUMBER_REWARD_CHAT = "number_reward_chat"
        const val SERVICE_DESTROY = "service_destroy"
        const val KEY_SHOW_GPT_4 = "key_show_gpt_4"
        const val TIME_8_MINUS = 60 * 8L
        const val TIME_7_DAY = 86400000L * 7 + 3600000L * 2
        const val LAYOUT_SPAN_COUNT_3 = 3
        const val LAYOUT_SPAN_COUNT_2 = 2
        const val TITLE_APP_CHAT = "title_app_chat"
        const val IS_SHOW_BOTTOM_DIALOG_EXIT_APP = "show_bottom_dialog_exit_app"
        const val CONFIG_SUMMARY_FILE_SIZE = "config_summary_file_size"
        const val CONFIG_NUMBER_OF_FILE_SUMMARIES = "config_number_of_file_summaries"
        const val CONFIG_TIME_SHOW_NEXT_ADS = "config_time_show_next_ads"
        const val CONFIG_IS_TIER_3 = "config_is_tier_3"
        const val CONFIG_ENABLE_NEW_IAP = "config_enable_new_iap"
        const val CONFIG_ENABLE_SCRIPT_IAP = "config_enable_script_iap"
        const val CONFIG_CHAT_GPT = "config_chat_gpt"
        const val TIME_OUT = 100L
        const val TIME_OUT_TOPIC = 120L
        const val LIMIT_INPUT_CHAT = 100
        const val MESSAGE_TIME_OUT = "HTTP 400 "
        const val DEFAULT_SIZE_FILE_SUMMARY = 1024L * 1024L
        const val DEFAULT_SUMMARY_FILE_SIZE = 1
        const val ERROR_CODE_400 = 400
    }

    annotation class INPUT_TYPE_TOPIC {
        companion object {
            const val INPUT = "input"
            const val SELECT = "select"
            const val ALL = "all"
        }
    }

    annotation class GALLERY_TYPE {
        companion object {
            const val OCR = "OCR"
            const val SUMMARY = "SUMMARY"
        }
    }

    annotation class TYPE_SUMMARY {
        companion object {
            const val SUMMARY_FILE = "summary_file"
            const val SUMMARY_TEXT = "summary_text"
        }
    }

    annotation class MODEL_CHAT {
        companion object {
            const val GPT_3_5 = "GPT-3.5"
            const val GPT_4 = "GPT_4"
        }
    }

    annotation class VERSION_GENERATE_ART {
        companion object {
            const val ADVANCED = "Advanced"
            const val PRIMARY = "Primary"
        }
    }

    object Copy
}