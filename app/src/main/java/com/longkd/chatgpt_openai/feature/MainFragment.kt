package com.longkd.chatgpt_openai.feature

import BottomFloatingType
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.databinding.FragmentMainBinding
import com.longkd.chatgpt_openai.dialog.DialogBottomExitApp
import com.longkd.chatgpt_openai.feature.home_new.HomeNewFragment
import com.longkd.chatgpt_openai.feature.setting.FragmentSetting

@Suppress("DEPRECATION")
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    companion object {
        fun newInstance(): MainFragment {
            val args = Bundle()

            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var mAdapter: ViewPagerAdapter? = null
    private var listFm: ArrayList<Fragment> = arrayListOf()
    private var mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0 -> {
                    mBinding?.mainFragmentBottomBar?.setCurrentTab(BottomFloatingType.TYPE_HOME)
                    val homeFragment = listFm.getOrNull(position) as? HomeNewFragment
                }

//                1 -> {
//                    mBinding?.mainFragmentBottomBar?.setCurrentTab(BottomFloatingType.TYPE_ART)
//                }

                else -> {
                    mBinding?.mainFragmentBottomBar?.setCurrentTab(BottomFloatingType.TYPE_SETTING)
                }
            }
        }
    }

    override fun initViews() {
        val homeFragment = HomeNewFragment.newInstance()
        val fragmentSetting = FragmentSetting.newInstance()
//        val homeArtFragment = HomeArtPageFragment.newInstance()

        listFm = arrayListOf(
            homeFragment,
//            homeArtFragment,
            fragmentSetting,
        )
        mAdapter = activity?.let {
            ViewPagerAdapter(
                it,
                listFm
            )
        }
        mBinding?.mainFragmentViewPager?.adapter = mAdapter
        mBinding?.mainFragmentViewPager?.isUserInputEnabled = false
//        mBinding?.mainFragmentViewPager?.offscreenPageLimit = 3
        mBinding?.mainFragmentBottomBar?.onItemSelected = { it ->
            when (it) {
                BottomFloatingType.TYPE_HOME -> mBinding?.mainFragmentViewPager?.setCurrentItem(
                    0,
                    false
                )

//                BottomFloatingType.TYPE_ART -> mBinding?.mainFragmentViewPager?.setCurrentItem(
//                    1,
//                    false
//                )

                BottomFloatingType.TYPE_SETTING -> mBinding?.mainFragmentViewPager?.setCurrentItem(
                    2,
                    false
                )

                else -> {
                }
            }
        }
        mBinding?.mainFragmentViewPager?.setCurrentItem(0, false)
        mBinding?.mainFragmentBottomBar?.enableState(BottomFloatingType.TYPE_HOME)
        mBinding?.mainFragmentBottomBar?.disableState(BottomFloatingType.TYPE_ART)
        mBinding?.mainFragmentBottomBar?.disableState(BottomFloatingType.TYPE_SETTING)
    }

    override fun initActions() {
        mBinding?.mainFragmentViewPager?.registerOnPageChangeCallback(mOnPageChangeCallback)
    }

    fun enableScrollPaper(isEnable: Boolean = true) {
        mBinding?.mainFragmentViewPager?.isUserInputEnabled = isEnable
    }

    override var initBackAction: Boolean = true
    override fun onDestroyView() {
        kotlin.runCatching {
            mBinding?.mainFragmentViewPager?.unregisterOnPageChangeCallback(mOnPageChangeCallback)
        }
        super.onDestroyView()
    }

    override fun initData() {}

    override fun hideLoading() {
        super.hideLoading()
        listFm.forEach {
            (it as? BaseFragment<*>)?.hideLoading()
        }
    }

    override fun handleOnBackPress(): Boolean {
        UtilsApp.hideKeyboard(activity)
        if ((activity as MainActivity).supportFragmentManager.backStackEntryCount <= 1) {
            actionBackApp()
        } else {
            activity?.onBackPressed()
        }
        return true
    }

    private fun actionBackApp() {
        DialogBottomExitApp.show(childFragmentManager).apply {
            onClickExit = {
                activity?.finish()
            }
        }
    }
}