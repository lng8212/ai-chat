package com.longkd.chatgpt_openai.feature.language

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseActivity
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.LanguageDto
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.databinding.ActivityLanguageBinding
import com.longkd.chatgpt_openai.feature.language.view_holder.SelectLanguageFirstAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LanguageActivity : BaseActivity() {
    private val viewModel: LanguageViewModel by viewModels()
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
        viewModel.getListLanguage()


        var adapter: SelectLanguageFirstAdapter? = null
        adapter = SelectLanguageFirstAdapter(arrayListOf(), object :
            ItemClickListener<LanguageDto> {
            override fun onClick(item: LanguageDto?, position: Int) {
                viewModel.selectLanguage(position)
                code = item?.data?.code ?: CODE_EN
            }
        })
        mBinding?.actLanguageRcv?.layoutManager = LinearLayoutManager(this)
        mBinding?.actLanguageRcv?.adapter = adapter
        mBinding?.idMenuToolbar?.setOnClickListener {
            viewModel.saveLanguage(code, this@LanguageActivity, intent)
        }

        viewModel.listLanguage.observe(this) {
            mBinding?.actLanguageRcv?.post {
                adapter.updateDataDiff(ArrayList(it))
            }
        }
    }

}