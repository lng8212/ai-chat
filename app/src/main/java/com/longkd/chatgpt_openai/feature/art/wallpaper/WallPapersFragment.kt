package com.longkd.chatgpt_openai.feature.art.wallpaper

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentWallpapersBinding
import com.longkd.chatgpt_openai.base.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WallPapersFragment: BaseFragment<FragmentWallpapersBinding>(R.layout.fragment_wallpapers) {
    private val viewModel: WallPaperViewModel by viewModels()
    private lateinit var mAdapter: WallPaperAdapter
    private lateinit var mAdapterGenerate: WallPaperAdapter
    override fun initViews() {
    }

    override fun initActions() {
        mBinding?.fmWallTvMore?.setOnSingleClick {
            mainFragment?.pushScreenWithAnimate(
                GeneratePhotoFragment.newInstance(),
                GeneratePhotoFragment::class.java.name
            )
        }
    }

    override fun onResume() {
        super.onResume()
        handleViewGenerateMore()
        mAdapterGenerate.setDatas(viewModel?.setListGeneratePhoto()?.toMutableList() ?: mutableListOf())
    }

    private fun handleViewGenerateMore() {
        val sizeListGenerate = (viewModel?.listGeneratePhoto()?.toMutableList()?.size ?: 0)
        if (viewModel?.listGeneratePhoto()?.toMutableList().isNullOrEmpty()) {
            mBinding?.fmWallTvTitleCreate?.gone()
            mBinding?.fmWallTvMore?.invisible()
        } else if (Constants.LAYOUT_SPAN_COUNT_3 > sizeListGenerate) {
            mBinding?.fmWallTvMore?.invisible()
        } else {
            mBinding?.fmWallTvMore?.visible()
        }
    }

    override var initBackAction: Boolean = false

    override fun initData() {

        mAdapter = WallPaperAdapter(viewModel?.listWallPaper ?: mutableListOf(), object :
            ItemClickListener<WallPaperData> {
            override fun onClick(item: WallPaperData?, position: Int) {
                mainFragment?.pushScreenWithAnimate(
                    SetWallpaperFragment.newInstance(item, false),
                    SetWallpaperFragment::class.java.name
                )
            }
        })
        mBinding?.fmWallRcvPopularPhoto?.adapter = mAdapter
        mBinding?.fmWallRcvPopularPhoto?.layoutManager =
            GridLayoutManager(context, Constants.LAYOUT_SPAN_COUNT_3)
        handleViewGenerateMore()
        mAdapterGenerate = WallPaperAdapter(viewModel?.setListGeneratePhoto()?.toMutableList() ?: mutableListOf(), object :
            ItemClickListener<WallPaperData> {
            override fun onClick(item: WallPaperData?, position: Int) {
                mainFragment?.pushScreenWithAnimate(
                    SetWallpaperFragment.newInstance(item, true),
                    SetWallpaperFragment::class.java.name
                )
            }
        })
        mBinding?.fmWallRcvCreatePhoto?.adapter = mAdapterGenerate
        mBinding?.fmWallRcvCreatePhoto?.layoutManager =
            GridLayoutManager(context, Constants.LAYOUT_SPAN_COUNT_3)
    }
    companion object {
        fun newInstance(): WallPapersFragment {
            val args = Bundle()

            val fragment = WallPapersFragment()
            fragment.arguments = args
            return fragment
        }
    }
}