package com.longkd.chatai.ui.main.chat

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.ktx.gone
import com.longkd.base_android.ktx.visible
import com.longkd.chatai.R
import com.longkd.chatai.data.TopicDetail
import com.longkd.chatai.databinding.FragmentChatBinding
import com.longkd.chatai.ui.main.chat.adapter.AllTopicAdapter
import com.longkd.chatai.ui.main.chat.adapter.DetailTopicAdapter
import com.longkd.chatai.ui.main.chat.adapter.ListTopicAdapter
import com.longkd.chatai.util.DataUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @Author: longkd
 * @Since: 20:48 - 12/08/2023
 */
@AndroidEntryPoint
class ChatFragment :
    BaseFragment<FragmentChatBinding, ChatViewModel>() {
    override val viewModel: ChatViewModel by viewModels()


    private val userAdapter: ListTopicAdapter by lazy {
        ListTopicAdapter(requireContext()) {
            viewModel.setCurrentType(it)
        }
    }

    private val allTopicAdapter: AllTopicAdapter by lazy {
        AllTopicAdapter(requireContext()) {

        }
    }
    private val topicDetailAdapter: DetailTopicAdapter by lazy {
        DetailTopicAdapter()
    }


    override fun initView() {
        binding.run {
            rclTitleTopic.adapter = userAdapter
            rclDetailAllTopic.adapter = allTopicAdapter
            rclDetailTopic.adapter = topicDetailAdapter
            titleChatAI.setCharacterDelay(200)
            titleChatAI.animateText(getString(R.string.text_animate))
        }
    }

    override fun observer() {
        userAdapter.submitList(viewModel.getListTopic())
        allTopicAdapter.submitList(viewModel.getAllDetailTopic(requireContext()))
        lifecycleScope.launch {
            viewModel.currentType.collectLatest { type ->
                handleUpdateData(type)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleUpdateData(type: DataUtils.ListTopic) {
        when (type) {
            DataUtils.ListTopic.ALL -> {
                binding.rclDetailTopic.gone()
                binding.rclDetailAllTopic.visible()
            }

            DataUtils.ListTopic.BUSINESS_EXPERT -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.BUSINESS_EXPERT
                    ) as List<TopicDetail>
                )
            }

            DataUtils.ListTopic.CONTENT_CREATOR -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.CONTENT_CREATOR
                    ) as List<TopicDetail>
                )
            }

            DataUtils.ListTopic.LIFESTYLE_BUDDY -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.LIFESTYLE_BUDDY
                    ) as List<TopicDetail>
                )
            }

            DataUtils.ListTopic.LEARN_WITH_CHAT_AI -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.LEARN_WITH_CHAT_AI
                    ) as List<TopicDetail>
                )
            }

            DataUtils.ListTopic.COOKING_MASTER -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.COOKING_MASTER
                    ) as List<TopicDetail>
                )
            }

            DataUtils.ListTopic.TRAVEL -> {
                binding.rclDetailTopic.visible()
                binding.rclDetailAllTopic.gone()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.TRAVEL
                    ) as List<TopicDetail>
                )
            }
        }
    }

}