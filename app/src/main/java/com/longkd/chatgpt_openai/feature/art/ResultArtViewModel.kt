/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultArtViewModel @Inject constructor(
    val dataRepository: DataRepository
) : BaseViewModel(dataRepository)