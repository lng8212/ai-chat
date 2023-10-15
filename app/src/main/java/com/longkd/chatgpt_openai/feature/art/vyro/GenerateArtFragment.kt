/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art.vyro

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.BR
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentGenerateArtBinding
import com.longkd.chatgpt_openai.dialog.SelectModelGenerateDialog
import com.longkd.chatgpt_openai.feature.art.ResultArtFragment
import com.longkd.chatgpt_openai.feature.art.vyro.adapter.AspectRatioAdapter
import com.longkd.chatgpt_openai.feature.art.vyro.adapter.AspectRatioData
import com.longkd.chatgpt_openai.feature.art.vyro.adapter.StyleGenerateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateArtFragment: BaseFragment<FragmentGenerateArtBinding>(R.layout.fragment_generate_art) {
    private val mViewModel: GenerateArtViewModel by viewModels()
    private lateinit var aspectRatioAdapter: AspectRatioAdapter
    private lateinit var styleAdapter: StyleGenerateAdapter
    override fun initViews() {
        mBinding?.setVariable(BR.mViewModel, mViewModel)
    }

    override fun initActions() {
        mBinding?.generateFmLlnAdvanceView?.setOnSingleClick {
            mViewModel.versionGenerate?.postValue(Constants.VERSION_GENERATE_ART.ADVANCED)
        }

        mBinding?.generateFmLlnPrimaryView?.setOnSingleClick {
            mViewModel.versionGenerate?.postValue(Constants.VERSION_GENERATE_ART.PRIMARY)
        }

        mBinding?.generateFmChooseModel?.setOnSingleClick {
            SelectModelGenerateDialog.show(childFragmentManager, mViewModel.listModelArt)
        }

        mBinding?.fmHomeArtLlGenerate?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                ResultArtFragment.newInstance(

                ), ResultArtFragment::javaClass.name
            )
        }
    }

    override var initBackAction: Boolean = true

    override fun initData() {
        aspectRatioAdapter = AspectRatioAdapter(mViewModel.setDataAspectRatio(), object :
            ItemClickListener<AspectRatioData> {
            override fun onClick(item: AspectRatioData?, position: Int) {
                val currentListStyleArt = mViewModel.setDataAspectRatio()
                currentListStyleArt.filter { it.isSelect == true }.forEach { it.isSelect = false }
                currentListStyleArt.firstOrNull{ it.ratio == item?.ratio}?.isSelect = true
                mBinding?.generateFmRclRatio?.post {
                    aspectRatioAdapter.updateData(currentListStyleArt)
                }
            }
        })
        mBinding?.generateFmRclRatio?.adapter = aspectRatioAdapter
        mBinding?.generateFmRclRatio?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        fun newInstance():GenerateArtFragment{
            val args = Bundle()

            val fragment = GenerateArtFragment()
            fragment.arguments = args
            return fragment
        }
    }
}