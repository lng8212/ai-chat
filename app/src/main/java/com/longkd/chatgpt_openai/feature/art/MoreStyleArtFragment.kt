/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.widget.header.BaseHeaderView
import com.longkd.chatgpt_openai.databinding.FragmentMoreStyleArtBinding
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreStyleArtFragment :
    BaseFragment<FragmentMoreStyleArtBinding>(R.layout.fragment_more_style_art) {
    companion object {
        fun newInstance(): MoreStyleArtFragment {
            val args = Bundle()
            val fragment = MoreStyleArtFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private var currentStyleArt : StyleArtDto? = null

    private val mViewModel: HomeViewModel by viewModels()
    override fun initViews() {
        val list = arrayListOf<StyleArtDto>()
        list.addAll(StyleArtHelper.getListStyleArt(context))
        var adapter: StyleArtAdapter? = null
        adapter = StyleArtAdapter(arrayListOf(), object : ItemClickListener<StyleArtDto> {
            override fun onClick(item: StyleArtDto?, position: Int) {
                val newList = arrayListOf<StyleArtDto>()
                list.forEachIndexed { _, _ ->
//                    newList.add(StyleArtDto(it.resID, it.name, if (index == position) !it.isSelected else false))
                }
                currentStyleArt = item
                mBinding?.fmMoreStyleArtRcv?.post {
                    adapter?.updateDataDiff(newList)
                }
            }

        })
        adapter.updateDataDiff(list)
        mBinding?.fmMoreStyleArtRcv?.layoutManager = GridLayoutManager(activity, 3)
        mBinding?.fmMoreStyleArtRcv?.adapter = adapter
    }

    override fun initActions() {
        mHeaderView?.setTitle(getString(R.string.choose_style_art))
        mHeaderView?.setLeftAction(CommonAction {
            popBackStack()
        })

        mViewModel.currentStyleArt?.observe(this){ list ->
            println()
        }
    }

    override var initBackAction: Boolean = true

    override fun initData() {
    }

    override fun initHeaderView(): BaseHeaderView? {
        return mBinding?.fmMoreStyleArtHeader
    }

}