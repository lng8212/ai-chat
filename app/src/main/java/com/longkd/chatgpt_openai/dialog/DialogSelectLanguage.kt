package com.longkd.chatgpt_openai.dialog

import android.view.LayoutInflater
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.model.LanguageItem
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.base.widget.BaseDialog
import com.longkd.chatgpt_openai.databinding.DialogSelectLanguageBinding
import com.longkd.chatgpt_openai.dialog.language.SelectLanguageAdapter

class DialogSelectLanguage : BaseDialog<DialogSelectLanguageBinding>() {
    private var actionOk: ((value: LanguageDto?) -> Unit)? = null


    override fun initView() {
        val checkedLanguage = CommonSharedPreferences.getInstance().getLanguage()
        val listLanguage: ArrayList<LanguageDto> = arrayListOf()
        LanguageItem.values().forEach {
            listLanguage.add(LanguageDto(it, checkedLanguage == it.code))
        }
        var checkedDto = listLanguage.find {
            it.isSelected
        }
        var adapter: SelectLanguageAdapter? = null
        adapter = SelectLanguageAdapter(arrayListOf(), object : ItemClickListener<LanguageDto> {
            override fun onClick(item: LanguageDto?, position: Int) {
                listLanguage.forEachIndexed { index, languageDto ->
                    languageDto.isSelected = index == position
                }
                mBinding?.dialogSelectLanguageRadioRcv?.post {
                    adapter?.updateDataDiff(listLanguage)
                }
                checkedDto = item
            }

        })
        mBinding?.dialogSelectLanguageRadioRcv?.adapter = adapter
        adapter.updateDataDiff(listLanguage)
        mBinding?.dialogSelectLanguageTvSubmit?.setOnSingleClick {
            actionOk?.invoke(checkedDto)
            closeDialog()
        }
        mBinding?.dialogSelectLanguageTvCancel?.setOnSingleClick {
            closeDialog()
        }
    }

    companion object {

        fun newInstance(
            actionOk: ((value: LanguageDto?) -> Unit)?
        ): DialogSelectLanguage {
            val mDialog = DialogSelectLanguage()
            mDialog.actionOk = actionOk
            return mDialog
        }
    }

    override fun initBinding(): DialogSelectLanguageBinding {
        return DialogSelectLanguageBinding.inflate(LayoutInflater.from(context))
    }
}