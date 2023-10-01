package com.longkd.chatgpt_openai.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.model.ModelGenerateArt
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.widget.BaseBottomSheetDialogFragment
import com.longkd.chatgpt_openai.databinding.DialogSelectModelGenerateBinding
import com.longkd.chatgpt_openai.feature.art.vyro.adapter.ModelGenerateAdapter

class SelectModelGenerateDialog: BaseBottomSheetDialogFragment<DialogSelectModelGenerateBinding>() {
    var onClickItem: ((model: ModelGenerateArt?) -> Unit) ? = null
    override val layoutId: Int = R.layout.dialog_select_model_generate

    override val backgroundTransparent: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = arguments?.getParcelableArrayList<ModelGenerateArt>(KEY_LIST_SUGGESS)
        val adapter = ModelGenerateAdapter(data?.toMutableList()!!, object :
            ItemClickListener<ModelGenerateArt> {
            override fun onClick(item: ModelGenerateArt?, position: Int) {
                item?.let {
                    onClickItem?.invoke(item)
                }
            }
        })
        viewDataBinding.selectRcl.adapter = adapter
        viewDataBinding.selectRcl.layoutManager = GridLayoutManager(requireContext(), Constants.LAYOUT_SPAN_COUNT_2)
    }

    companion object {
        fun newInstance(data: ArrayList<ModelGenerateArt>): SelectModelGenerateDialog {
            val args = Bundle().apply {
                putParcelableArrayList(KEY_LIST_SUGGESS, data)
            }
            val fragment = SelectModelGenerateDialog()
            fragment.arguments = args
            return fragment
        }

        fun dismiss(fm: FragmentManager) {
            val fragment = fm.findFragmentByTag(SelectModelGenerateDialog::class.simpleName)
            if (fragment is SelectModelGenerateDialog) {
                fragment.dismiss()
            }
        }

        fun show(fm: FragmentManager, data: ArrayList<ModelGenerateArt>): SelectModelGenerateDialog {
            val fragment = newInstance(data)
            fragment.show(fm, SelectModelGenerateDialog::class.simpleName)
            return fragment
        }

        private const val KEY_LIST_SUGGESS = "key_list_model"
    }
}