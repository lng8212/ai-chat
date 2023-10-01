package com.longkd.chatgpt_openai.feature.history

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.HistoryDto
import com.longkd.chatgpt_openai.base.mvvm.DataViewModelFactory
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.invisible
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.base.widget.header.BaseHeaderView
import com.longkd.chatgpt_openai.databinding.FragmentHistoryBinding
import com.longkd.chatgpt_openai.dialog.DialogConfirmDeleteChat
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import com.longkd.chatgpt_openai.feature.home.viewholder.ChatHistoryAdapter
import com.longkd.chatgpt_openai.feature.home.viewholder.ChatItemHistoryVH
import com.longkd.chatgpt_openai.feature.home_new.HomeNewFragment

class FragmentHistory : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history) {
    private var mHistoryAdapter: ChatHistoryAdapter? = null
    private var mViewModel: HomeViewModel? = null
    private val listHistory = ArrayList<HistoryDto>()
    private var homeFragment : HomeNewFragment? = null
    private var isSelectAll: Boolean = false
    var onBackPress: (() -> Unit) ? = null
    companion object {
        fun newInstance(): FragmentHistory {
            val args = Bundle()
            val fragment = FragmentHistory()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        homeFragment = activity?.supportFragmentManager?.fragments?.find {
            it is HomeNewFragment
        } as HomeNewFragment
        mHistoryAdapter = ChatHistoryAdapter(arrayListOf(),
            object : ItemClickListener<HistoryDto> {
                override fun onClick(item: HistoryDto?, position: Int) {
                    pushScreenWithAnimate(
                        ChatDetailFragment.newInstance(
                            chatId = item?.chatDetailDto?.parentId ?: 0,
                            fromHistory = true
                        ), ChatDetailFragment::class.java.name
                    )
                }

                override fun onLongClick(item: HistoryDto?, position: Int) {
                    super.onLongClick(item, position)
                }
            }, object : ChatItemHistoryVH.CheckBoxListener {
                override fun onCheckChange() {
                    updateData()
                }
            })
        mBinding?.fmHistoryRcv?.adapter = mHistoryAdapter
        mBinding?.fmHistoryRcv?.layoutManager = LinearLayoutManager(activity)
    }

    private fun updateData() {
        mBinding?.fmHistoryDelete?.isEnabled = listHistory.any { it.isSelected }
        mBinding?.fmHistoryDelete?.text = String.format(
            getStringRes(R.string.delete_x),
            listHistory.filter { it.isSelected }.size.toString()
        )
    }

    override fun initActions() {
        mBinding?.fmHistoryHeader?.setLeftAction(CommonAction {
            handleOnBackPress()
        })
        mBinding?.fmHistoryHeader?.setTitle(getStringRes(R.string.txt_history))
        mBinding?.fmHistoryHeader?.setRightAction(CommonAction {
            mHistoryAdapter?.setShowRemove(true)
            mBinding?.fmHistoryHeader?.invisible()
            mBinding?.fmHistoryDelete?.visible()
            mBinding?.fmHistoryHeaderRemove?.visible()
        }, R.drawable.ic_delete)

        mBinding?.fmHistoryHeaderRemove?.setTitle(getStringRes(R.string.txt_delete))
        mBinding?.fmHistoryHeaderRemove?.setLeftAction(CommonAction {
            mHistoryAdapter?.setShowRemove(false)
            mBinding?.fmHistoryHeader?.visible()
            mBinding?.fmHistoryDelete?.gone()
            mBinding?.fmHistoryHeaderRemove?.invisible()
        }, R.drawable.ic_close_white)
        mBinding?.fmHistoryHeaderRemove?.setRightAction(CommonAction {
            if (!isSelectAll) {
                listHistory.forEach { it.isSelected = true }
                mBinding?.fmHistoryHeaderRemove?.setRightIcon(R.drawable.ic_cb_selected)
            } else {
                listHistory.forEach { it.isSelected = false }
                mBinding?.fmHistoryHeaderRemove?.setRightIcon(R.drawable.ic_cb_unselect)
            }
            isSelectAll = !isSelectAll
            updateData()
            mHistoryAdapter?.notifyDataSetChanged()
        }, R.drawable.ic_cb_unselect)
        mBinding?.fmHistoryDelete?.setOnClickListener {
            val listSelected = listHistory.filter { it.isSelected }
            if (listSelected.isEmpty())
                return@setOnClickListener
            DialogConfirmDeleteChat.newInstance {
                listSelected.forEach {
                    kotlin.runCatching {
                        mViewModel?.removeChatHistory(it.chatDetailDto.parentId)
                        listHistory.remove(it)
                        mBinding?.fmHistoryRcv?.setItemViewCacheSize(listHistory.size)
                        mHistoryAdapter?.setDatas(listHistory)
                    }
                }
                mBinding?.fmHistoryDelete?.text = getStringRes(R.string.txt_delete)
                if (listHistory.isEmpty())
                    handleOnBackPress()
            }.show(activity?.supportFragmentManager)
        }

    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        onBackPress?.invoke()
        activity?.onBackPressed()
        return true
    }
    override fun initData() {
        mViewModel = context?.let {
            ViewModelProvider(
                this, DataViewModelFactory(context = it)
            )[HomeViewModel::class.java]
        }
        mViewModel?.getAllChatHistory()?.observe(this) {
            if (it.isNotEmpty()) {
                mBinding?.fmHistoryHeader?.setEnableBtnRightIcon(R.drawable.ic_delete, true)
                listHistory.clear()
                it.forEach { dto ->
                    listHistory.add(HistoryDto(dto, false))
                }
                mBinding?.fmHistoryRcv?.setItemViewCacheSize(listHistory.size)
                mHistoryAdapter?.setDatas(listHistory)
            } else {
                mBinding?.fmHistoryHeader?.setEnableBtnRightIcon(R.drawable.ic_delete, false)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        homeFragment?.updateDataChat()
    }

    override fun initHeaderView(): BaseHeaderView? = mBinding?.fmHistoryHeader
}