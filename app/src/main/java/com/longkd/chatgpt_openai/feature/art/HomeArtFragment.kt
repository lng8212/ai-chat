/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art

import android.annotation.SuppressLint
import android.os.Bundle
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentHomeArtBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class HomeArtFragment : BaseFragment<FragmentHomeArtBinding>(R.layout.fragment_home_art) {

    companion object {
        fun newInstance(): HomeArtFragment {
            val args = Bundle()

            val fragment = HomeArtFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {

    }




    @SuppressLint("ClickableViewAccessibility")
    override fun initActions() {
    }


    override var initBackAction: Boolean = false

    override fun initData() {
    }
}