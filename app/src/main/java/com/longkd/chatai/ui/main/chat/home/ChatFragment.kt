package com.longkd.chatai.ui.main.chat.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.longkd.base_android.base.BaseFragment
import com.longkd.base_android.ktx.gone
import com.longkd.base_android.ktx.setOnSingleClickAnim
import com.longkd.base_android.ktx.visible
import com.longkd.chatai.R
import com.longkd.chatai.data.TopicDetail
import com.longkd.chatai.databinding.FragmentChatBinding
import com.longkd.chatai.ui.main.chat.home.adapter.AllTopicAdapter
import com.longkd.chatai.ui.main.chat.home.adapter.DetailTopicAdapter
import com.longkd.chatai.ui.main.chat.home.adapter.ListTopicAdapter
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


    private val listTopicAdapter: ListTopicAdapter by lazy {
        ListTopicAdapter(requireContext()) {
            viewModel.setCurrentType(it)
        }
    }

    private val allTopicAdapter: AllTopicAdapter by lazy {
        AllTopicAdapter(requireContext()) { item ->
            listTopicAdapter.setSelected(item.type)
            viewModel.setCurrentType(item.type)
            binding.homeFmScrollerLayout.scrollToChildWithOffset(
                binding.homeFmLlnBottomView, 0
            )
            binding.rclTitleTopic.layoutManager?.scrollToPosition(
                viewModel.getListTopic().indexOf(item.type)
            )
        }
    }
    private val topicDetailAdapter: DetailTopicAdapter by lazy {
        DetailTopicAdapter()
    }


    override fun initView() {
        binding.run {
            rclTitleTopic.adapter = listTopicAdapter
            rclDetailAllTopic.adapter = allTopicAdapter
            rclDetailTopic.adapter = topicDetailAdapter
            titleChatAI.setCharacterDelay(200)
            titleChatAI.animateText(getString(R.string.text_animate))
        }
        initAction()
    }

    override fun observer() {
        listTopicAdapter.submitList(viewModel.getListTopic())
        allTopicAdapter.submitList(viewModel.getAllDetailTopic(requireContext()))
        lifecycleScope.launch {
            viewModel.currentType.collectLatest { type ->
                handleUpdateData(type)
            }
        }
    }

    private fun initAction() {
        binding.homeFmStartChat.setOnSingleClickAnim {
            viewModel.navigateToDetail()
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
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.BUSINESS_EXPERT
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }

            DataUtils.ListTopic.CONTENT_CREATOR -> {
                binding.rclDetailTopic.visible()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.CONTENT_CREATOR
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }

            DataUtils.ListTopic.LIFESTYLE_BUDDY -> {
                binding.rclDetailTopic.visible()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.LIFESTYLE_BUDDY
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }

            DataUtils.ListTopic.LEARN_WITH_CHAT_AI -> {
                binding.rclDetailTopic.visible()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.LEARN_WITH_CHAT_AI
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }

            DataUtils.ListTopic.COOKING_MASTER -> {
                binding.rclDetailTopic.visible()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.COOKING_MASTER
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }

            DataUtils.ListTopic.TRAVEL -> {
                binding.rclDetailTopic.visible()
                topicDetailAdapter.submitList(
                    DataUtils.getTypeTopic(
                        requireContext(),
                        DataUtils.ListTopic.TRAVEL
                    ) as List<TopicDetail>
                )
                binding.rclDetailAllTopic.gone()
            }
        }
    }

}