package com.longkd.chatgpt_openai.feature.home_new

import android.annotation.SuppressLint
import android.content.Context.SHORTCUT_SERVICE
import android.content.Intent
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.*
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.base.util.UtilsApp.toCelsius
import com.longkd.chatgpt_openai.databinding.FragmentNewHomeBinding
import com.longkd.chatgpt_openai.dialog.*
import com.longkd.chatgpt_openai.feature.ShareDataViewModel
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.chat.viewholder.AllTopicAdapter
import com.longkd.chatgpt_openai.feature.chat.viewholder.DetailTopicAdapter
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import com.longkd.chatgpt_openai.feature.home_new.topic.DetailTopicFragment
import com.longkd.chatgpt_openai.feature.home_new.topic.TopicProvider
import com.longkd.chatgpt_openai.feature.home_new.topic.TopicsAdapter
import com.longkd.chatgpt_openai.feature.intro.IntroFirstFragment
import com.longkd.chatgpt_openai.feature.intro.IntroFragment
import com.longkd.chatgpt_openai.feature.summary.SummaryFileFragment
import com.longkd.chatgpt_openai.feature.widget.WidgetFragment
import com.longkd.chatgpt_openai.feature.widget.WidgetTopic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class HomeNewFragment : BaseFragment<FragmentNewHomeBinding>(R.layout.fragment_new_home) {
    private val mViewModel: HomeViewModel by viewModels()
    private val mShareDataViewModel: ShareDataViewModel by activityViewModels()
    private lateinit var topicAdapter: TopicsAdapter
    private lateinit var detailTopicAdapter: DetailTopicAdapter
    private lateinit var allTopicAdapter: AllTopicAdapter
    private var isCheckDuplicate: Boolean = false

    override fun initViews() {
        initShortCut()
        mViewModel.getCurrentWeather()
        CommonSharedPreferences.getInstance().titleAppChat.let {
            if (!it.isNullOrBlank()) {
                mBinding?.homeFmSelectTitle?.text = it
            }
        }

        mBinding?.homeFmTitleChatAI?.setCharacterDelay(150)
        mBinding?.homeFmTitleChatAI?.animateText(getString(R.string.str_title_start_chat))
        initViewTopic()
    }

    private fun initShortCut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager: ShortcutManager? =
                activity?.getSystemService(SHORTCUT_SERVICE) as? ShortcutManager
            if (shortcutManager?.dynamicShortcuts?.size.orZero() > 0) shortcutManager?.removeAllDynamicShortcuts()

        }
    }


    private val listTopic: ArrayList<TopicData> = arrayListOf()
    private fun initViewTopic() {
        listTopic.addAll(
            TopicProvider.getListTopics(
                resources.getStringArray(R.array.topic_title).asList()
            ).toMutableList()
        )
        listTopic.firstOrNull()?.isSelect = true
        topicAdapter = TopicsAdapter(listTopic, object : ItemClickListener<TopicData> {
            override fun onClick(item: TopicData?, position: Int) {
                updateViewClickTopic(item)
            }
        })

        with(mBinding?.rclTitleTopic) {
            this?.adapter = topicAdapter
            this?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        TopicProvider.listSubHaveOption(DetailTopicData.values(), requireContext())
        detailTopicAdapter =
            DetailTopicAdapter(mutableListOf(), object : ItemClickListener<DetailTopicData> {
                override fun onClick(item: DetailTopicData?, position: Int) {
                    handleTopic(item)
                }
            })
        mBinding?.rclDetailTopic?.adapter = detailTopicAdapter
        mBinding?.rclDetailTopic?.layoutManager =
            GridLayoutManager(requireContext(), Constants.LAYOUT_SPAN_COUNT_2)

        allTopicAdapter = AllTopicAdapter(arrayListOf())
        mBinding?.rclDetailAllTopic?.adapter = allTopicAdapter
        allTopicAdapter.updateData(listTopic)
        allTopicAdapter.clickItemTopic = {
            handleTopic(it)
        }

        allTopicAdapter.clickArrowRight = { title ->
            updateViewClickTopic(listTopic.firstOrNull { it.title == title })
            mBinding?.homeFmScrollerLayout?.scrollToChildWithOffset(
                mBinding?.homeFmLlnBottomView, 0
            )
            mBinding?.rclTitleTopic?.layoutManager?.scrollToPosition(listTopic.indexOf(listTopic.firstOrNull { it.title == title }))
        }
    }

    private fun handleTopic(data: DetailTopicData?) {
        val dataField = TopicProvider.listSubHaveOption.firstOrNull { it.subTopic == data?.name }
        dataField?.listOption?.let {
            lifecycleScope.launchWhenStarted {
                if (isCheckDuplicate) return@launchWhenStarted
                isCheckDuplicate = true
                mainFragment?.pushScreenWithAnimate(
                    DetailTopicFragment.newInstance(
                        it,
                        data?.question.orZero(),
                        context?.getString(data?.title ?: R.string.str_marketing_plan) ?: ""
                    ),
                    DetailTopicFragment::class.java.name
                )
                delay(500L)
                isCheckDuplicate = false
            }
        }
    }

    private fun updateViewClickTopic(item: TopicData?) {
        item?.let {
            topicAdapter.updateView(it)
            if (it.topicId == 0) {
                mBinding?.rclDetailAllTopic?.visible()
                mBinding?.rclDetailTopic?.gone()
            } else {
                mBinding?.rclDetailAllTopic?.gone()
                mBinding?.rclDetailTopic?.visible()
                val arrTopicDetail =
                    DetailTopicData.values().toMutableList()
                        .filter { it1 -> it1.topicId == item.topicId }
                detailTopicAdapter.setDatas(arrTopicDetail.toMutableList())
            }
        }
    }


    override fun onResume() {
        TopicProvider.listSubHaveOption(DetailTopicData.values(), requireContext())
        super.onResume()
    }

    override fun initActions() {
        mBinding?.homeFmStartChat?.setOnSingleClick {
            showChatFragment()
        }

        mBinding?.homeFmSummaryFile?.setOnSingleClick {
            showSummaryFileFragment()

        }

        mBinding?.homeFmImvWidget?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(WidgetFragment(), WidgetFragment::class.java.name)
        }

        mBinding?.homeFmIconOcr?.setOnSingleClick {
            showDialogOcr()
        }

        mBinding?.toolTipOcr?.setOnSingleClick {
            showLlnToolTip(false)
            showDialogOcr()
        }

        mBinding?.homeFmViewDirector?.setOnSingleClick {
            showLlnToolTip(false)
        }
        mBinding?.homeFmViewDirector1?.setOnSingleClick {
            showLlnToolTip(false)
        }
    }

    private fun showSummaryFileFragment() {
        val fragment = SummaryFileFragment.newInstance().apply {
            onCallBackWhenPurchase = {
            }
        }
        mainFragment?.pushScreenWithAnimate(fragment, SummaryFileFragment::class.java.name)
    }


    private fun showChatFragment() {
        val fragment = ChatDetailFragment.newInstance().apply {
            onCallBackWhenPurchase = {
            }
        }
        mainFragment?.pushScreenWithAnimate(fragment, ChatDetailFragment::class.java.name)
    }

    private fun showDirector(isShowToolTip: Boolean) {
        if (isShowToolTip) {
            showLlnToolTip(true)
            mBinding?.homeFmAddWidget?.setOnSingleClick {
                showLlnToolTip(false)
                mainFragment?.pushScreenWithAnimate(
                    WidgetFragment.newInstance(),
                    WidgetFragment::class.java.name
                )
            }
        }
    }

    private fun showLlnToolTip(isShow: Boolean) {
        if (isShow) {
            mBinding?.homeFmViewDirector?.visible()
            mBinding?.homeFmViewDirector1?.visible()
            mBinding?.homeFmImvDirector?.visible()
            mBinding?.homeFmAddWidget?.visible()
            mBinding?.toolTipOcr?.visible()
            mBinding?.homeFmImvDirector1?.visible()
        } else {
            mBinding?.homeFmViewDirector?.gone()
            mBinding?.homeFmViewDirector1?.gone()
            mBinding?.homeFmImvDirector?.gone()
            mBinding?.homeFmAddWidget?.gone()
            mBinding?.toolTipOcr?.gone()
            mBinding?.homeFmImvDirector1?.gone()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val isShowIntro = CommonSharedPreferences.getInstance(context)
            .getBoolean(Constants.FIRST_SHOW_INTRO, true)
        if (isShowIntro) {
            pushScreen(IntroFragment.newInstance().apply {
                mOnViewDestroyedListener = {

                }
            }, IntroFirstFragment::class.java.name)
        } else {
            lifecycleScope.launch(Dispatchers.Main) {

                when (activity?.intent?.action) {
                    Intent.ACTION_PROCESS_TEXT -> {
                        activity?.intent?.getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.let {
                            mainFragment?.pushScreenWithAnimate(
                                ChatDetailFragment.newInstance(
                                    textCopy = "It appears that you have copied:\n$it\n\nWhat would you like me to do with it"
                                ),
                                ChatDetailFragment::class.java.name
                            )
                        }
                    }

                    Constants.KEY_WIDGET_CLICK -> {
                        if (activity?.intent?.getBooleanExtra(
                                WidgetTopic.TYPE_HISTORY,
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
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.dataWeather.collect { state ->
                    if (state is com.longkd.chatgpt_openai.open.State.Success) {
                        mBinding?.txtCurrentWeather?.visible()
                        val maxTemp =
                            state.data?.dailyForecasts?.get(0)?.temperature?.maximum?.value?.toInt()
                        val minTemp =
                            state.data?.dailyForecasts?.get(0)?.temperature?.minimum?.value?.toInt()
                        mBinding?.txtCurrentWeather?.text = getString(R.string.text_today) +
                                " ${minTemp?.toCelsius()} - ${maxTemp?.toCelsius()}°C"
                    } else {
                        mBinding?.txtCurrentWeather?.invisible()
                    }

                }
            }

        }

        mShareDataViewModel.mNotifyUpdateChatHistory.observe(this) {
            if (context != null)
                mViewModel.getAllChatHistory()
        }

        if (isShowIntro) {
            showDirector(isShowIntro)
        }

    }

    fun updateDataChat() {
        mViewModel.getAllChatHistory()
    }


    override var initBackAction: Boolean = false


    companion object {
        fun newInstance(): HomeNewFragment {
            val args = Bundle()

            val fragment = HomeNewFragment()
            fragment.arguments = args
            return fragment
        }
    }
}