/*
 * Created by longkd on 10/15/23, 11:33 PM
 * Copyright (c) by Begamob.com 2023 . All rights reserved.
 * Last modified 10/15/23, 9:08 PM
 */

package com.longkd.chatgpt_openai.feature.art.vyro

import androidx.lifecycle.MutableLiveData
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.ModelGenerateArt
import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.feature.art.vyro.adapter.AspectRatioData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenerateArtViewModel @Inject constructor(val dataRepository: DataRepository) : BaseViewModel(
    dataRepository
) {
    val versionGenerate = MutableLiveData<String>()
    val listModelArt: ArrayList<ModelGenerateArt> = arrayListOf()
    init {
        versionGenerate.value = Constants.VERSION_GENERATE_ART.ADVANCED
        setListModelArt()
    }

    fun setDataAspectRatio(): ArrayList<AspectRatioData> {
        val mArr = arrayListOf<AspectRatioData>()
        mArr.add(AspectRatioData("1:1", true))
        mArr.add(AspectRatioData("4:3", false))
        mArr.add(AspectRatioData("3:2", false))
        mArr.add(AspectRatioData("2:3", false))
        mArr.add(AspectRatioData("16:9", false))
        mArr.add(AspectRatioData("9:16", false))
        mArr.add(AspectRatioData("5:4", false))
        mArr.add(AspectRatioData("4:5", false))
        mArr.add(AspectRatioData("3:1", false))
        mArr.add(AspectRatioData("3:4", false))
        return mArr
    }

    fun setListModelArt() {
        if (listModelArt.isNullOrEmpty()) {
            listModelArt.add(ModelGenerateArt(33, "Imagine V5", "Generate anything, from HD realism to anime using a text as a prompt guiding the image...", R.drawable.img_model_imagine_v5))
            listModelArt.add(ModelGenerateArt(34, "Anime V5", "Generate visually popping anime images with Anime V5. The images are vibrant, fresh, and...", R.drawable.img_model_anime_v5))
            listModelArt.add(ModelGenerateArt(32, "ImagineV4.1", "Built upon Imagine V4, Imagine V4.1 offers a lot better realism than its predecessor, but it ca...", R.drawable.img_model_imagine_v4_1))
            listModelArt.add(ModelGenerateArt(31, "Creative (V4)", "With keen attention to detail, creativity offers limitless possibilities. Best used for...", R.drawable.img_model_creative_v4))
            listModelArt.add(ModelGenerateArt(30, "Imagine V4", "Built upon imagine V3, V4 offers better realism and anime generation. This model can do...", R.drawable.img_model_imagine_v4))
            listModelArt.add(ModelGenerateArt(28, "Imagine V3", "The third iteration in the imagine series, Imagine V3 does what you can imagine. From realistic...", R.drawable.img_model_imagine_v3))
            listModelArt.add(ModelGenerateArt(27, "Imagine V1", "The first model in the Imagination series, Imagine V1 offers whimsical drawings of...", R.drawable.img_model_imagine_v1))
            listModelArt.add(ModelGenerateArt(29, "Realistic", "Realistic aims to bring your prompt to reality. With a focus on character portraits in a...", R.drawable.img_model_realistic))
        }
    }

}