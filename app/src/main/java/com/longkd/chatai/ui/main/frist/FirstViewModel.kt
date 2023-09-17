package com.longkd.chatai.ui.main.frist

import androidx.navigation.NavDirections
import com.longkd.base_android.base.BaseGetDataViewModel
import com.longkd.base_android.ktx.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 21:09 - 12/08/2023
 */

@HiltViewModel
class FirstViewModel @Inject constructor(private val userRepository: com.longkd.chatai.domain.UserRepository) :
    BaseGetDataViewModel<NavDirections>() {

    private val _listData = MutableStateFlow(listOf<com.longkd.chatai.data.api.response.UserResponse.Data>())
    val listData = _listData.asStateFlow()

    fun getListData(page: Int) = bindUiState {
        userRepository.getAllData(page).onSuccess { data ->
            _listData.update {
                data.data
            }
        }
    }
}