/*
 * Created by longkd on 12/1/23, 9:17 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 12/1/23, 9:17 PM
 */

package com.longkd.chatgpt_openai.feature.language

import com.longkd.chatgpt_openai.MyApp
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.model.LanguageItem
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.UtilsApp

/**
 * @Author: longkd
 * @Since: 21:17 - 01/12/2023
 */
class LanguageRepository {
    fun getListLanguage(): List<LanguageDto> {
        val checkedLanguage = CommonSharedPreferences.getInstance().getLanguage()
        val listLanguage: ArrayList<LanguageDto> = arrayListOf()
        LanguageItem.values().forEach {
            listLanguage.add(LanguageDto(it, checkedLanguage == it.code))
        }
        return listLanguage
    }

    fun saveLanguage(code: String) {
        CommonSharedPreferences.getInstance().saveLanguage(code)
        MyApp.context().let { it1 -> UtilsApp.setSystemLocale(code, it1) }
    }
}