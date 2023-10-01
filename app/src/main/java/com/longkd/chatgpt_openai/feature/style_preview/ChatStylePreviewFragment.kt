package com.longkd.chatgpt_openai.feature.style_preview

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.ThemeMode
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentChatStylePreviewBinding
import com.longkd.chatgpt_openai.feature.ShareDataViewModel
import com.longkd.chatgpt_openai.feature.chat.ChatDetailFragment
import com.bumptech.glide.Glide

class ChatStylePreviewFragment :
    BaseFragment<FragmentChatStylePreviewBinding>(R.layout.fragment_chat_style_preview) {
    private var themeMode: ThemeMode = ThemeMode.Mode5
    private val mShareDataViewModel: ShareDataViewModel by activityViewModels()

    companion object {
        const val THEME_MODE = "THEME_MODE"
        fun newInstance(themeMode: Int): ChatStylePreviewFragment {
            val args = Bundle()

            val fragment = ChatStylePreviewFragment()
            args.putInt(THEME_MODE, themeMode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        themeMode = ThemeMode.values().find {
            it.value == (arguments?.getInt(THEME_MODE, ThemeMode.Mode5.value)
                ?: ThemeMode.Mode5.value)
        } ?: ThemeMode.Mode5
        val imgPreview = when (themeMode) {
            ThemeMode.Mode1 -> {
                R.drawable.bg_character_preview_1
            }
            ThemeMode.Mode2 -> {
                R.drawable.bg_character_preview_2
            }
            ThemeMode.Mode3 -> {
                R.drawable.bg_character_preview_3
            }
            ThemeMode.Mode4 -> {
                R.drawable.bg_character_preview_4
            }
            ThemeMode.Mode6 -> {
                R.drawable.bg_character_preview_6
            }
            else -> {
                R.drawable.bg_character_preview_5
            }
        }
        kotlin.runCatching {
            context?.let {
                mBinding?.chatStylePreviewImg?.let { it1 ->
                    Glide.with(it).load(imgPreview).into(
                        it1
                    )
                }
            }
        }
    }

    override fun initActions() {
        mBinding?.apply {
            chatStylePreviewStartChat.setOnSingleClick {
                CommonSharedPreferences.getInstance().putInt(Constants.THEME_MODE, themeMode.value)
                mShareDataViewModel.updateThemeMode(themeMode.value)

                            pushScreenWithAnimate(
                                ChatDetailFragment.newInstance(true),
                                ChatDetailFragment::class.java.name
                            )
            }
            chatStylePreviewCancel.setOnSingleClick {
                popBackStack()
            }
        }
    }

    override fun initData() {

    }


    override fun handleOnBackPress(): Boolean {
        activity?.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        UtilsApp.hideKeyboard(activity)
    }

    override var initBackAction: Boolean = true
}