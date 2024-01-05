package com.longkd.chatgpt_openai.feature.home

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.ChatDetailDto
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.HomeFragmentBinding
import com.longkd.chatgpt_openai.feature.ShareDataViewModel
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.history.FragmentHistory
import com.longkd.chatgpt_openai.feature.widget.WidgetFragment
import com.longkd.chatgpt_openai.feature.widget.WidgetTopic
import com.longkd.chatgpt_openai.feature.widget.WidgetTopic.Companion.ACTION_UPDATE
import com.longkd.chatgpt_openai.feature.widget.WidgetTopic.Companion.TYPE_HISTORY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeFragmentBinding>(R.layout.home_fragment) {
    private val mViewModel: HomeViewModel by viewModels()
    private val mShareDataViewModel: ShareDataViewModel by activityViewModels()
    private var chatDetailDto: ChatDetailDto? = null

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun initViews() {
        mBinding?.fmHomeCtViewGroup?.layoutTransition?.setAnimateParentHierarchy(false)
        mHeaderView?.apply {
            setVisibleLeft(false)
            setTitle(getStringRes(R.string.app_name))
            setRightAction(CommonAction {
                Toast.makeText(context, "This is right action!", Toast.LENGTH_SHORT).show()
            })
        }
        mShareDataViewModel.setTimeStartApp {}
        mShareDataViewModel.setNumberStartApp()
    }


    override fun initActions() {
        mBinding?.homeFmStartChat?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                ChatDetailFragment.newInstance(), ChatDetailFragment::class.java.name
            )
        }

        mBinding?.homeFmImvWidget?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(WidgetFragment(), WidgetFragment::class.java.name)
            mBinding?.homeFmViewDirector?.gone()
            mBinding?.homeFmImvDirector?.gone()
            mBinding?.homeFmAddWidget?.gone()
        }

        mBinding?.homeFmTvSeeAll?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(FragmentHistory(), FragmentHistory::class.java.name)
        }
        mBinding?.homeFmCtConversion?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                ChatDetailFragment.newInstance(
                    chatId = chatDetailDto?.parentId ?: 0
                ), ChatDetailFragment::class.java.name
            )
        }

        mBinding?.homeFmTvTitle?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                ChatDetailFragment.newInstance(), ChatDetailFragment::class.java.name
            )
        }
        showDirector()
    }

    private fun showDirector() {
        if (mShareDataViewModel.getNumberStartApp() >= 2 && CommonSharedPreferences.getInstance()
                .getShowDirector() == false
        ) {
            mBinding?.homeFmViewDirector?.visible()
            mBinding?.homeFmImvDirector?.visible()
            mBinding?.homeFmAddWidget?.visible()
        }
        mBinding?.homeFmViewDirector?.setOnSingleClick {
            mBinding?.homeFmViewDirector?.gone()
            mBinding?.homeFmImvDirector?.gone()
            mBinding?.homeFmAddWidget?.gone()
        }

        mBinding?.homeFmAddWidget?.setOnSingleClick {
            mBinding?.homeFmViewDirector?.gone()
            mBinding?.homeFmImvDirector?.gone()
            mBinding?.homeFmAddWidget?.gone()
            mainFragment?.pushScreenWithAnimate(
                WidgetFragment.newInstance(),
                WidgetFragment::class.java.name
            )
        }

    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        mViewModel.callGetTimeStamp()
        lifecycleScope.launch(Dispatchers.Main) {
            when (activity?.intent?.action) {
                Constants.KEY_WIDGET_CLICK -> {
                    if (activity?.intent?.getBooleanExtra(
                            TYPE_HISTORY,
                            false
                        ) == true
                    ) {
                        val topicType = activity?.intent?.getLongExtra(
                            Constants.KEY_WIDGET_CLICK,
                            -1
                        )
                        mainFragment?.pushScreenWithAnimate(
                            ChatDetailFragment.newInstance(
                                chatId = topicType ?: 0
                            ), ChatDetailFragment::class.java.name
                        )

                    } else {
                        val topicType = activity?.intent?.getIntExtra(
                            Constants.KEY_WIDGET_CLICK,
                            -1
                        )
                        val topics = TopicsUtils.listTopic(activity)
                        val topic = topics.find {
                            it.topicType == topicType
                        }
                        mainFragment?.pushScreenWithAnimate(
                            ChatDetailFragment.newInstance(
                                topicType = topic
                            ),
                            ChatDetailFragment::javaClass.name
                        )
                    }
                }

                else -> {}
            }
        }

        mBinding?.homeFmStartTvChatNumber?.gone()
        mViewModel.getAllChatHistory().observe(this) {
            if (it.isEmpty()) {
                mBinding?.homeFmLayoutEmpty?.visible()
                mBinding?.homeFmLayout?.gone()
            } else {

                mBinding?.homeFmLayoutEmpty?.gone()
                mBinding?.homeFmLayout?.visible()
                chatDetailDto = it[0]
                mBinding?.homeFmTvUserChat?.text = it[0].chatUserNane
                mBinding?.homeFmTvBotChat?.text = it[0].message
                val intent = Intent(
                    activity,
                    WidgetTopic::class.java
                )
                intent.action = ACTION_UPDATE
                activity?.sendBroadcast(intent)
            }
        }
        mShareDataViewModel.mNotifyUpdateChatHistory.observe(this) {
            if (context != null)
                mViewModel.getAllChatHistory()
        }
    }

    override var initBackAction: Boolean = false


    fun updateDataChat() {
        mViewModel.getAllChatHistory()
    }
}