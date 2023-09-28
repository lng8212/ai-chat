package com.longkd.chatai.ui.main

import android.annotation.SuppressLint
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.longkd.base_android.base.BaseActivity
import com.longkd.base_android.base.EmptyViewModel
import com.longkd.base_android.ktx.gone
import com.longkd.base_android.ktx.visible
import com.longkd.base_android.navigation.IntentDirections
import com.longkd.chatai.R
import com.longkd.chatai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, EmptyViewModel<IntentDirections>>() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MainActivity
            private set
    }

    private lateinit var navController: NavController
    override val viewModel: EmptyViewModel<IntentDirections>
        get() = EmptyViewModel()


    private val listFragmentShowBottomNav = listOf(
        R.id.chatFragment, R.id.settingFragment
    )

    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        if (destination.id in listFragmentShowBottomNav) {
            showBottomNav()
        } else {
            goneBottomNav()
        }
    }

    override fun initView() {
        instance = this
        //        navigateTo(MainActivity::class.createActionIntentDirections())
        val navHost = supportFragmentManager.findFragmentById(R.id.fcvMain) as NavHostFragment
        navController = navHost.navController
        binding.bnMain.itemIconTintList = null
        NavigationUI.setupWithNavController(
            binding.bnMain,
            navController
        )
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }

    private fun goneBottomNav() {
        if (binding.bnMain.isVisible) {
            binding.bnMain.gone()
        }
    }

    private fun showBottomNav() {
        if (binding.bnMain.isGone) {
            binding.bnMain.visible()
        }
    }
}
