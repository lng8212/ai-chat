package com.longkd.chatgpt_openai.feature.art.wallpaper

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.mvvm.DataViewModelFactory
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentGeneratePhotoBinding

class GeneratePhotoFragment: BaseFragment<FragmentGeneratePhotoBinding>(R.layout.fragment_generate_photo) {
    private var viewModel: WallPaperViewModel? = null
    private lateinit var mAdapterGenerate: WallPaperAdapter
    override fun initViews() {
    }

    override fun initActions() {
        mBinding?.baseHeaderBtnLeft?.setOnSingleClick {
            popBackStack()
        }
    }

    override var initBackAction: Boolean = true

    override fun initData() {
        viewModel = context?.let {
            ViewModelProvider(
                this, DataViewModelFactory(context = it)
            )[WallPaperViewModel::class.java]
        }
        mAdapterGenerate = WallPaperAdapter(viewModel?.listGeneratePhoto()?.toMutableList() ?: mutableListOf(), object :
            ItemClickListener<WallPaperData> {
            override fun onClick(item: WallPaperData?, position: Int) {
                val fragment = SetWallpaperFragment.newInstance(item, true).apply {
                        callBack = { popBackStack() }
                    }
                mainFragment?.pushScreenWithAnimate(
                    fragment,
                    SetWallpaperFragment::class.java.name
                )
            }
        })

        mBinding?.rclCreatePhoto?.adapter = mAdapterGenerate
        mBinding?.rclCreatePhoto?.layoutManager =
            GridLayoutManager(context, Constants.LAYOUT_SPAN_COUNT_3)
    }
    companion object {
        fun newInstance(): GeneratePhotoFragment{
            val args = Bundle()

            val fragment = GeneratePhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}