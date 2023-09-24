package com.longkd.chatai.ui.splash

import androidx.lifecycle.viewModelScope
import com.longkd.base_android.base.BaseViewModel
import com.longkd.base_android.ktx.createActionIntentDirections
import com.longkd.base_android.navigation.IntentDirections
import com.longkd.chatai.ui.main.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 22:50 - 17/09/2023
 */

@HiltViewModel
class SplashViewModel @Inject constructor() : BaseViewModel<IntentDirections>() {
    fun initToMain() {
        viewModelScope.launch {
            delay(3000)
            navigator.navigateOff(MainActivity::class.createActionIntentDirections())
        }
    }
}