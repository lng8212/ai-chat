/*
 * Created by longkd on 12/1/23, 9:06 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 12/1/23, 9:06 PM
 */

package com.longkd.chatgpt_openai.feature.language

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.longkd.chatgpt_openai.base.model.LanguageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 21:06 - 01/12/2023
 */
@HiltViewModel
class LanguageViewModel @Inject constructor() : ViewModel() {
    val listLanguage = MutableLiveData<List<LanguageDto>>()
    private val languageRepository = LanguageRepository()
    fun getListLanguage() {
        val listLang = languageRepository.getListLanguage()
        listLanguage.value = listLang
    }

    fun selectLanguage(position: Int) {
        val data = listLanguage.value
        data?.forEachIndexed { index, languageDto ->
            languageDto.isSelected = index == position
        }
        listLanguage.value = data ?: emptyList()
    }

    fun saveLanguage(code: String, activity: LanguageActivity, intent: Intent) {
        languageRepository.saveLanguage(code, activity, intent)
    }
}