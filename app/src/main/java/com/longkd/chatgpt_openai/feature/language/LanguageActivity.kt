package com.longkd.chatgpt_openai.feature.language

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.MyApp
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseActivity
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.model.LanguageItem
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.databinding.ActivityLanguageBinding
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.language.view_holder.SelectLanguageFirstAdapter

class LanguageActivity : BaseActivity() {
    val list = arrayListOf<LanguageDto>()
    private var mBinding: ActivityLanguageBinding? = null
    private var code = "en"
    private val CODE_EN = "en"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_top)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        val checkedLanguage = CommonSharedPreferences.getInstance().getLanguage()
        code = checkedLanguage ?: CODE_EN
        val listLanguage: ArrayList<LanguageDto> = arrayListOf()
        LanguageItem.values().forEach {
            listLanguage.add(LanguageDto(it, checkedLanguage == it.code))
        }
        var adapter: SelectLanguageFirstAdapter? = null
        adapter = SelectLanguageFirstAdapter(arrayListOf(), object :
            ItemClickListener<LanguageDto> {
            override fun onClick(item: LanguageDto?, position: Int) {
                listLanguage.forEachIndexed { index, languageDto ->
                    languageDto.isSelected = index == position
                }
                mBinding?.actLanguageRcv?.post {
                    adapter?.updateDataDiff(listLanguage)
                }
                code = item?.data?.code ?: CODE_EN
            }
        })
        mBinding?.actLanguageRcv?.layoutManager = LinearLayoutManager(this)
        mBinding?.actLanguageRcv?.adapter = adapter
        adapter.updateDataDiff(listLanguage)
        mBinding?.idMenuToolbar?.setOnClickListener {
            CommonSharedPreferences.getInstance().saveLanguage(code)
            MyApp.context().let { it1 -> UtilsApp.setSystemLocale(code, it1) }
            val mIntent = Intent(this@LanguageActivity, MainActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            mIntent.data = intent.data
            mIntent.action = intent.action
            CommonSharedPreferences.getInstance().setFirstLanguage(true)
            intent?.extras?.let { mIntent.putExtras(it) }
            startActivity(mIntent)
            finish()
        }
    }

}