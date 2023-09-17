package com.longkd.chatai.ui.main.setting

import androidx.navigation.NavDirections
import com.longkd.base_android.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 21:08 - 12/08/2023
 */
@HiltViewModel
class SettingViewModel @Inject constructor() : BaseViewModel<NavDirections>() {

    fun navigateToThirdFragment() {
        navigator.navigateTo(SettingFragmentDirections.actionSettingFragmentToThirdFragment())
    }
}