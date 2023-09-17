package com.longkd.chatai.ui.splash

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.longkd.base_android.base.BaseActivity
import com.longkd.chatai.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @Author: longkd
 * @Since: 22:27 - 17/09/2023
 */

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    override val viewModel: SplashViewModel by viewModels()

    override fun initView() {
        viewModel.initToMain()
    }

}