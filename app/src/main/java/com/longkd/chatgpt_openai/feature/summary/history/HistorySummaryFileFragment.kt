package com.longkd.chatgpt_openai.feature.summary.history

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.mvvm.DataViewModelFactory
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.util.visible
import com.longkd.chatgpt_openai.databinding.FragmentHistorySummaryFileBinding
import com.longkd.chatgpt_openai.dialog.DialogConfirmDeleteChat
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.longkd.chatgpt_openai.feature.summary.SummaryFileViewModel

class HistorySummaryFileFragment: BaseFragment<FragmentHistorySummaryFileBinding>(R.layout.fragment_history_summary_file) {
    private var mViewModel: SummaryFileViewModel? = null
    private var isDeleteSummary: Boolean = false
    private lateinit var adapter: HistorySummaryAdapter


    override fun initViews() {
        mBinding?.fmHistoryHeader?.setTitle(getString(R.string.str_your_chats))
        mBinding?.fmHistoryHeader?.setLeftAction(CommonAction {
            if (!isDeleteSummary) handleOnBackPress()
            else {
                isDeleteSummary = false
                handleHeaderViews()
            }
        })
        mBinding?.fmHistoryHeader?.setRightAction(
            CommonAction {
                isDeleteSummary = !isDeleteSummary
                handleHeaderViews()
            }, R.drawable.ic_delete
        )
    }

    private fun handleHeaderViews() {
        adapter.setShowRemove(isDeleteSummary)
        when(isDeleteSummary) {
            true -> {
                mBinding?.fmHistoryHeader?.setRightIcon(null)
                mBinding?.fmHistoryDelete?.visible()
            }
            false -> {
                mBinding?.fmHistoryHeader?.setRightIcon(R.drawable.ic_delete)
                mBinding?.fmHistoryDelete?.gone()
            }
        }
    }

    override fun initActions() {
        mBinding?.fmHistoryDelete?.setOnSingleClick {
            DialogConfirmDeleteChat.newInstance {
                mViewModel?.deleteHistory()
            }.show(activity?.supportFragmentManager)
        }
    }

    private fun handleClickHistorySummary(data: SummaryHistoryDto) {
        if (isDeleteSummary) {
            mViewModel?.arrRemoveSummary?.value?.firstOrNull { it.md5 == data.md5 }?.let {
                mViewModel?.arrRemoveSummary?.value?.remove(data)
            } ?: run {
                mViewModel?.arrRemoveSummary?.value?.add(data)
            }
            mViewModel?.arrRemoveSummary?.postValue(mViewModel?.arrRemoveSummary?.value)
        } else {
                        mainFragment?.pushScreenWithAnimate(
                            ChatDetailFragment.newInstance(summaryData = data, chatId = -10L),
                            ChatDetailFragment::class.java.name
                        )
        }
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {
        (activity as MainActivity).onRefeshDataSummary()
                    activity?.onBackPressed()
        return true
    }

    override fun initData() {
        mViewModel = context?.let {
            ViewModelProvider(
                this, DataViewModelFactory(context = it)
            )[SummaryFileViewModel::class.java]
        }
        mViewModel?.getAllSummaryFile()
        observerData()
    }

    private fun observerData() {
        mViewModel?.getListSummaryHistory()?.observe(this) {
            adapter = HistorySummaryAdapter(it)
            mBinding?.fmHistoryRcv?.adapter = adapter
            mBinding?.fmHistoryRcv?.layoutManager = LinearLayoutManager(context)
            adapter.onClickItem = {
                handleClickHistorySummary(it)
            }
        }

        mViewModel?.arrRemoveSummary?.observe(this) {
            mBinding?.fmHistoryDelete?.isEnabled = !it.isNullOrEmpty()
            mBinding?.fmHistoryDelete?.text = if (it.isNullOrEmpty()) getString(R.string.txt_delete) else getString(R.string.delete_x, it.size.toString())
        }

        mViewModel?.isRemoveSuccess?.observe(this) {
            (activity as MainActivity).onRefeshDataSummary()
            popBackStack()
        }
    }

    companion object {
        fun newInstance(): HistorySummaryFileFragment {
            val args = Bundle()
            val fragment = HistorySummaryFileFragment()
            fragment.arguments = args
            return fragment
        }
    }
}