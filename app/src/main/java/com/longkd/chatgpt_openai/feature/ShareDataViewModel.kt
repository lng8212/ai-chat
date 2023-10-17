package com.longkd.chatgpt_openai.feature

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.text.SimpleDateFormat
import java.util.Date

class ShareDataViewModel : ViewModel() {
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var chatNumber: MutableLiveData<Int> = MutableLiveData()
        private set
    var themeMode: MutableLiveData<Int> = MutableLiveData()
    var mNotifyUpdateChatHistory: MutableLiveData<Int> = MutableLiveData()

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateChatNumber(chatNumber: Int) {
        this.chatNumber.value = chatNumber
    }

    fun updateThemeMode(mode: Int) {
        this.themeMode.value = mode
    }


    fun notifyUpdateChatHistory() {
        val new = (mNotifyUpdateChatHistory.value ?: 0) + arrayOf(1, -1, 2).random()
        mNotifyUpdateChatHistory.value = new
    }

    fun setTimeStartApp(action : (() -> Unit)? = null) {
        if (CommonSharedPreferences.getInstance().getTimeFirstStartApp() == 0L) {
            action?.invoke()
            CommonSharedPreferences.getInstance().setTimeFirstStartApp(System.currentTimeMillis())
        }
    }


    fun isResetLimitUse(): Boolean {
        val startTime = CommonSharedPreferences.getInstance().getTimeStartApp() ?: System.currentTimeMillis()
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val firstStartDate = Date(startTime)
        val currentDate = Date(System.currentTimeMillis())
        CommonSharedPreferences.getInstance().setTimeStartApp(System.currentTimeMillis())
        return sdf.format(currentDate).compareTo(sdf.format(firstStartDate)) != 0
    }

    fun setNumberStartApp(){
        CommonSharedPreferences.getInstance().numberStartApp = (CommonSharedPreferences.getInstance().numberStartApp +1)
    }
    fun getNumberStartApp() : Int{
        return CommonSharedPreferences.getInstance().numberStartApp
    }
}