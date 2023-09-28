package com.longkd.chatai.ui.main.chat.home

import android.content.Context
import androidx.navigation.NavDirections
import com.longkd.base_android.base.BaseGetDataViewModel
import com.longkd.chatai.data.Topic
import com.longkd.chatai.util.DataUtils
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
class ChatViewModel @Inject constructor(private val userRepository: com.longkd.chatai.domain.UserRepository) :
    BaseGetDataViewModel<NavDirections>() {

//    private val _listData =
//        MutableStateFlow(listOf<com.longkd.chatai.data.api.response.UserResponse.Data>())
//    val listData = _listData.asStateFlow()
//
//    fun getListData(page: Int) = bindUiState {
//        userRepository.getAllData(page).onSuccess { data ->
//            _listData.update {
//                data.data
//            }
//        }
//    }

    private val _currentType = MutableStateFlow(DataUtils.ListTopic.ALL)
    val currentType = _currentType.asStateFlow()
    fun getListTopic(): List<DataUtils.ListTopic> {
        return DataUtils.getAllTopic()
    }

    @Suppress("UNCHECKED_CAST")
    fun getAllDetailTopic(context: Context): List<Topic> {
        return DataUtils.getTypeTopic(context, DataUtils.ListTopic.ALL) as List<Topic>
    }

    fun setCurrentType(type: DataUtils.ListTopic) {
        _currentType.update {
            type
        }
    }

    fun navigateToDetail() {
        navigator.navigateTo(ChatFragmentDirections.actionChatFragmentToDetailChatFragment())
    }
}