package com.longkd.chatgpt_openai.feature.art.wallpaper

import com.longkd.chatgpt_openai.base.mvvm.BaseViewModel
import com.longkd.chatgpt_openai.base.mvvm.DataRepository
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.Constants

class WallPaperViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel(dataRepository) {

    val listWallPaper = arrayListOf(
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wallpaper_first.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_second.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_third.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_paper_four.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_five.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_six.png"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_seven.jpg"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_eight.jpg"),
        WallPaperData(url = "https://gitlab.longkd.com/DinhHV/chat-gpt-data/-/raw/develop/img_set_wall_paper_nine.jpg"),
    )

    fun listGeneratePhoto(): List<WallPaperData> {
        return CommonSharedPreferences.getInstance().getListGeneratePhoto()?.map {
            WallPaperData(url = it)
        } ?: run { listOf() }
    }

    fun setListGeneratePhoto(): List<WallPaperData> {
        if (listGeneratePhoto().size > Constants.LAYOUT_SPAN_COUNT_3) {
            return listGeneratePhoto().subList(0, 3)
        } else {
            return listGeneratePhoto()
        }
    }
}