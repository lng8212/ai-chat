package com.longkd.chatgpt_openai.base.mvvm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DataViewModelFactory(val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(DataRepository::class.java)
                .newInstance(DataRepository.getInstance(context))
        } catch (e: InstantiationException) {
            throw RuntimeException("${e.message} InstantiationException")
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("${e.message} IllegalArgumentException")
        }
    }
}
