package com.longkd.chatai.ui.main.frist

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.base.toolbar.FragmentConfiguration
import com.longkd.base_android.base.toolbar.ToolbarConfiguration
import com.longkd.base_android.data.CommonSharedPreference
import com.longkd.base_android.ktx.TAG
import com.longkd.chatai.R
import com.longkd.chatai.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 20:48 - 12/08/2023
 */
@AndroidEntryPoint
class FirstFragment :
    BaseFragment<FragmentFirstBinding, FirstViewModel>() {
    private val toolbarConfiguration = ToolbarConfiguration(titleResId = R.string.first_fragment)
    override val viewModel: FirstViewModel by viewModels()

    @Inject
    lateinit var preference: CommonSharedPreference

    private val userAdapter: UserAdapter by lazy {
        UserAdapter(requireContext())
    }

    override val enableStateScreen: Boolean
        get() = true
    override val fragmentConfiguration: FragmentConfiguration
        get() = FragmentConfiguration(toolbarConfiguration)

    override fun initView() {
        binding.tbMain.configure(toolbarConfiguration)
        binding.rcvMain.adapter = userAdapter
//        preference.put(TEST, "5555")
        viewModel.getListData(1)
    }

    override fun observer() {
        lifecycleScope.launch {
            viewModel.listData.collectLatest {
                userAdapter.submitList(it)
                Log.e(TAG, "observer: ${it.size}")
            }
        }
    }

    override fun onStateError() {
        super.onStateError()
    }

    override fun onStateLoading() {
        super.onStateLoading()
    }
}