package com.longkd.chatai.ui.main.third

import androidx.navigation.NavDirections
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.base.EmptyViewModel
import com.longkd.base_android.base.toolbar.FragmentConfiguration
import com.longkd.base_android.base.toolbar.ToolbarConfiguration
import com.longkd.chatai.R
import com.longkd.chatai.databinding.FragmentThirdBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @Author: longkd
 * @Since: 20:47 - 12/08/2023
 */

@AndroidEntryPoint
open class ThirdFragment : BaseFragment<FragmentThirdBinding, EmptyViewModel<NavDirections>>() {
    private val toolbarConfiguration = ToolbarConfiguration(
        startIconResId = R.drawable.ic_back,
        startIconClick = ::navBack,
        titleResId = R.string.third_fragment
    )
    override val viewModel: EmptyViewModel<NavDirections>
        get() = EmptyViewModel()
    override val fragmentConfiguration: FragmentConfiguration
        get() = FragmentConfiguration(toolbarConfiguration)

    override fun initView() {
        binding.tbMain.configure(fragmentConfiguration.toolbarConfiguration)
    }

    private fun navBack() {
        back()
    }
}