package com.longkd.chatgpt_openai.feature.art

import android.content.Context
import androidx.annotation.StringRes
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.Strings

object StyleArtHelper {
    fun getListStyleArt(context: Context?) : ArrayList<StyleArtDto>{
        return arrayListOf(
            StyleArtDto(R.drawable.imv_style_random, getStringRes(context, R.string.txt_random), "midjourney", true),
            StyleArtDto(R.drawable.imv_style_painting, getStringRes(context, R.string.txt_painting), "wavy-diffusion"),
            StyleArtDto(R.drawable.img_style_magical,  getStringRes(context, R.string.txt_magical), "synthwave-diffusion"),
            StyleArtDto(R.drawable.img_style_streampunk,  getStringRes(context, R.string.txt_steampunk)),
            StyleArtDto(R.drawable.img_style_anime,  getStringRes(context, R.string.txt_anime), "wifu-diffusion"),
            StyleArtDto(R.drawable.img_style_digital_art,  getStringRes(context, R.string.txt_digital_art)),
            StyleArtDto(R.drawable.img_style_cyberpunk,  getStringRes(context, R.string.txt_cyberpunk)),
            StyleArtDto(R.drawable.img_style_ghibil,  getStringRes(context, R.string.txt_ghibli)),
            StyleArtDto(R.drawable.img_style_game_art,  getStringRes(context, R.string.txt_game_art), "gta5-artwork-diffusi"),
            StyleArtDto(R.drawable.img_style_comic,  getStringRes(context, R.string.txt_comic)),
            StyleArtDto(R.drawable.img_style_pixel_art,  getStringRes(context, R.string.txt_pixel_art)),
            StyleArtDto(R.drawable.img_style_3d,  getStringRes(context, R.string.txt_3d), "redshift-diffusion"),
            StyleArtDto(R.drawable.img_style_synthwave,  getStringRes(context, R.string.txt_synthwave)),
            StyleArtDto(R.drawable.img_style_watercolor,  getStringRes(context, R.string.txt_watercolor)),
            StyleArtDto(R.drawable.img_style_picaso,  getStringRes(context, R.string.txt_picasso), "wavy-diffusion"),
        )
    }

    fun getListInspired(context: Context?) : ArrayList<String> {
        return arrayListOf(
            getStringRes(context, R.string.inspired_1),
            getStringRes(context, R.string.inspired_2),
            getStringRes(context, R.string.inspired_3),
            getStringRes(context, R.string.inspired_4),
            getStringRes(context, R.string.inspired_5),
        )
    }

    fun getStringRes(context: Context?, @StringRes res: Int): String {
        return try {
            context?.resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }
}