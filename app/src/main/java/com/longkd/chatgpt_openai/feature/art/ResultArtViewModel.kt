/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.longkd.chatgpt_openai.open.State
import com.longkd.chatgpt_openai.open.dto.image.CreateImageRequest
import com.longkd.chatgpt_openai.open.dto.image.ImageResult
import com.longkd.chatgpt_openai.open.image.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultArtViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _imageURL: MutableStateFlow<State<ImageResult>> =
        MutableStateFlow(State.Loading())
    val imageURL = _imageURL.asStateFlow()


    fun getImage(content: String, size: String) {
        viewModelScope.launch {
            val request = CreateImageRequest(content, 1, size, "url")

            _imageURL.update {
                State.Loading()
            }
            when (val response = imageRepository.generateImage(request)) {
                is State.Success -> {
                    _imageURL.update {
                        State.Success(response.data)
                    }
                }

                is State.Error -> {
                    _imageURL.update {
                        State.Error(response.message)
                    }
                }

                else -> {}
            }

        }

    }

}