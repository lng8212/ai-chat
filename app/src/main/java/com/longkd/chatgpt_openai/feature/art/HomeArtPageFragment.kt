
package com.longkd.chatgpt_openai.feature.art

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.gone
import com.longkd.chatgpt_openai.databinding.FragmentHomeArtPageBinding
import com.longkd.chatgpt_openai.feature.ViewPagerAdapter
import com.longkd.chatgpt_openai.feature.art.vyro.GenerateArtFragment
import com.longkd.chatgpt_openai.feature.art.wallpaper.WallPapersFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeArtPageFragment :
    BaseFragment<FragmentHomeArtPageBinding>(R.layout.fragment_home_art_page),
    TabLayoutMediator.TabConfigurationStrategy {
    private var mAdapter: ViewPagerAdapter? = null
    private var listFm: ArrayList<Fragment> = arrayListOf()
    private var tabLayoutMediator: TabLayoutMediator? = null
    override fun initViews() {
        val homeArtFragment = GenerateArtFragment.newInstance()
        val wallPapersFragment = WallPapersFragment.newInstance()
        listFm = arrayListOf(
            homeArtFragment,
            wallPapersFragment
        )
        mAdapter = activity?.let {
            ViewPagerAdapter(
                it,
                listFm
            )
        }
        mBinding?.viewPage?.adapter = mAdapter
        mBinding?.viewPage?.isUserInputEnabled = true
        tabLayoutMediator = TabLayoutMediator(mBinding?.tabLayout!!, mBinding?.viewPage!!, this)
        tabLayoutMediator?.attach()
    }

    override fun initActions() {
    }
    override fun onDestroyView() {
        mAdapter = null
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
    }

    override var initBackAction: Boolean = false

    override fun initData() {}

    companion object {
        fun newInstance(): HomeArtPageFragment {
            val args = Bundle()

            val fragment = HomeArtPageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        tab.text = resources.getStringArray(R.array.arr_art_page)[position]
        mBinding?.viewPage?.setCurrentItem(0, true)
    }
}