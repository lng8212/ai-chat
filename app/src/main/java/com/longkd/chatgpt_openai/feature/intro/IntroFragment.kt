package com.longkd.chatgpt_openai.feature.intro

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentIntroBinding
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.ViewPagerAdapter


class IntroFragment : BaseFragment<FragmentIntroBinding>(R.layout.fragment_intro) {

    companion object {
        fun newInstance(): IntroFragment {
            val args = Bundle()

            val fragment = IntroFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var mAdapter: ViewPagerAdapter? = null
    private var listFm: ArrayList<Fragment> = arrayListOf()

    override fun initViews() {
        val introFirstFragment = IntroFirstFragment.newInstance()
        val introSecondFragment = IntroSecondFragment.newInstance()

        listFm = arrayListOf(
            introFirstFragment,
            introSecondFragment,
        )
        mAdapter = activity?.let {
            ViewPagerAdapter(
                it,
                listFm
            )
        }
        val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        introFirstFragment.playAnimation()
                        introSecondFragment.stopAnimation()
                        mBinding?.fmIntroTvDes?.text = getString(R.string.text_intro_first)
                    }

                    1 -> {
                        introFirstFragment.stopAnimation()
                        introSecondFragment.playAnimation()
                        mBinding?.fmIntroTvDes?.text = getString(R.string.text_intro_second)
                    }

                    else -> {
                        mBinding?.fmIntroTvDes?.text = getString(R.string.text_intro_first)
                    }
                }
            }
        }
        mBinding?.fmIntroVp?.registerOnPageChangeCallback(mOnPageChangeCallback)
        mBinding?.fmIntroVp?.adapter = mAdapter

        mBinding?.fmIntroIndicator?.let {
            mBinding?.fmIntroVp?.let { it1 ->
                TabLayoutMediator(it, it1) { tab, position ->

                }.attach()
            }
        }
        initTextPolicy()
    }

    override fun initActions() {
        mBinding?.fmIntroConfirm?.setOnSingleClick {
            val mIntent = Intent(context, MainActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            CommonSharedPreferences.getInstance().setFirstLanguage(true)
            startActivity(mIntent)
            activity?.finish()
        }
        mBinding?.fmIntroCb?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                mBinding?.fmIntroConfirm?.alpha = 1f
                mBinding?.fmIntroConfirm?.isEnabled = true
            } else {

                mBinding?.fmIntroConfirm?.alpha = 0.5f
                mBinding?.fmIntroConfirm?.isEnabled = false
            }
        }
    }

    private fun initTextPolicy() {
        val firstString = getStringRes(R.string.text_des_policy)
        val secondString = getStringRes(R.string.text_policy)
        val endString = getStringRes(R.string.text_policy_terms)

        val resultString = SpannableStringBuilder(Strings.EMPTY)
        val secondSpannableString = SpannableString(secondString)
        val endSpannableString = SpannableString(endString)

        val clickableSpanSecond: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                UtilsApp.openBrowser(context, Constants.URL_POLICY)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ResourcesCompat.getColor(resources, R.color.color_green_base, null)
            }
        }

        secondSpannableString.setSpan(
            clickableSpanSecond, 0, secondSpannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        secondSpannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            secondSpannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val clickableSpanEnd: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                UtilsApp.openBrowser(context, Constants.URL_TERM_OF_SERVICE)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ResourcesCompat.getColor(resources, R.color.color_green_base, null)
            }
        }
        endSpannableString.setSpan(
            clickableSpanEnd, 0, endSpannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        endSpannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            endSpannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        resultString.append(firstString)
        resultString.append(" ")
        resultString.append(endSpannableString)
        resultString.append(" & ")
        resultString.append(secondSpannableString)

        mBinding?.fmIntroTvPolicy?.text = resultString
        mBinding?.fmIntroTvPolicy?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.fmIntroTvPolicy?.highlightColor = Color.TRANSPARENT
    }

    override var initBackAction: Boolean = true
    override fun initData() {
    }
}