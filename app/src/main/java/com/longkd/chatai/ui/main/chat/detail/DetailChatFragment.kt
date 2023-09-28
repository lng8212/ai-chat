package com.longkd.chatai.ui.main.chat.detail

import androidx.fragment.app.viewModels
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.base.toolbar.FragmentConfiguration
import com.longkd.base_android.base.toolbar.ToolbarConfiguration
import com.longkd.chatai.R
import com.longkd.chatai.databinding.FragmentDetailChatBinding

/**
 * @Author: longkd
 * @Since: 14:50 - 28/09/2023
 */
class DetailChatFragment : BaseFragment<FragmentDetailChatBinding, DetailChatViewModel>() {

    private val toolbarConfiguration = ToolbarConfiguration(
        R.string.text_gpt,
        R.drawable.ic_back_white,
        startIconClick = { back() }

    )
    override val viewModel: DetailChatViewModel by viewModels()

    override val fragmentConfiguration: FragmentConfiguration
        get() = FragmentConfiguration(toolbarConfiguration)

    override fun initView() {
        binding.chatFmHeaderView.configure(fragmentConfiguration.toolbarConfiguration)
    }
}