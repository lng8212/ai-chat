package com.longkd.chatgpt_openai.feature.art

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.DataUtils
import com.longkd.chatgpt_openai.base.util.Size
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentGenerateArtBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateArtFragment :
    BaseFragment<FragmentGenerateArtBinding>(R.layout.fragment_generate_art) {
    private lateinit var aspectRatioAdapter: AspectRatioAdapter
    private var currentSelected = DataUtils.getListSize()[0].size
    override fun initViews() {
        aspectRatioAdapter = AspectRatioAdapter(DataUtils.getListSize().toMutableList(), object :
            ItemClickListener<Size> {
            override fun onClick(item: Size?, position: Int) {
                currentSelected = item?.size ?: "0"
                val currentListStyleArt = DataUtils.getListSize().toMutableList()
                currentListStyleArt.filter { it.isSelected }.forEach { it.isSelected = false }
                currentListStyleArt.firstOrNull { it.size == item?.size }?.isSelected = true
                mBinding?.generateFmRclRatio?.post {
                    aspectRatioAdapter.updateData(currentListStyleArt)
                }
            }
        })
        mBinding?.generateFmRclRatio?.adapter = aspectRatioAdapter
        mBinding?.generateFmRclRatio?.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun initActions() {
        mBinding?.fmHomeArtLlGenerate?.setOnSingleClick {
            val text = mBinding?.fmHomeArtEdtPrompt?.text
            if (text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Field is empty or invalid!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val data = Bundle()
                data.putString(IMAGE_CONTENT, text.toString())
                data.putString(IMAGE_SIZE, currentSelected)
                mainFragment?.pushScreenWithAnimate(
                    ResultArtFragment.newInstance(text.toString(), currentSelected),
                    ResultArtFragment::javaClass.name
                )

            }

        }
    }

    override var initBackAction: Boolean = true

    override fun initData() {
    }

    companion object {
        const val IMAGE_CONTENT = "IMAGE_CONTENT"
        const val IMAGE_SIZE = "IMAGE_SIZE"
        fun newInstance(): GenerateArtFragment {
            val args = Bundle()

            val fragment = GenerateArtFragment()
            fragment.arguments = args
            return fragment
        }
    }
}