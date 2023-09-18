package com.longkd.chatai.ui.main.chat

import androidx.fragment.app.viewModels
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.data.CommonSharedPreference
import com.longkd.chatai.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @Author: longkd
 * @Since: 20:48 - 12/08/2023
 */
@AndroidEntryPoint
class ChatFragment :
    BaseFragment<FragmentChatBinding, ChatViewModel>() {
    override val viewModel: ChatViewModel by viewModels()

    @Inject
    lateinit var preference: CommonSharedPreference

//    private val userAdapter: UserAdapter by lazy {
//        UserAdapter(requireContext())
//    }


    override fun initView() {
//        preference.put(TEST, "5555")
//        viewModel.getListData(1)
    }

    override fun observer() {

    }

}