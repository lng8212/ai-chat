/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.base.OpenAIHolder
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.open.client.TimeStampService
import com.longkd.chatgpt_openai.open.dto.generate.GenerateArtResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResultArtViewModel @Inject constructor(
    val dataRepository: DataRepository
) : BaseViewModel(dataRepository) {
    private var mTimeStampService = TimeStampService(TOKEN_PAKE, timeout = 60, type = 0)

    private var mGenerateArtResult: GenerateArtResult = GenerateArtResult()
    var mGenerateNumber: MutableLiveData<Int> = MutableLiveData()

    fun callGetTimeStamp() {
        val time = CommonSharedPreferences.getInstance().timeStartGetTime
        if (System.currentTimeMillis() - time > Constants.TIME_8_MINUS) {
            CommonSharedPreferences.getInstance().timeStartGetTime = System.currentTimeMillis()
            uiScope.launch(Dispatchers.Main) {
                val data = withContext(Dispatchers.Default) {
                    try {
                        OpenAIHolder.callGetTime(mTimeStampService)
                    } catch (e: Throwable) {
                        null
                    }
                }
                data?.token?.let {
                    CommonSharedPreferences.getInstance().timeStamp = it
                }
            }
        }
    }



    companion object {
        const val TOKEN_PAKE = "wIX1xqLnKnprsmNMw/bMiA=="
    }
}