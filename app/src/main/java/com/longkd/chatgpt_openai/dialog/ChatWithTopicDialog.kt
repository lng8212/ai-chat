package com.longkd.chatgpt_openai.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.widget.BaseBottomSheetDialogFragment
import com.longkd.chatgpt_openai.feature.home_new.topic.DataFieldTopic
import com.longkd.chatgpt_openai.feature.home_new.topic.SelectFieldAdapter
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.databinding.DialogChatWithTopicBinding

class ChatWithTopicDialog: BaseBottomSheetDialogFragment<DialogChatWithTopicBinding>() {
    var onClickItem: ((promt: String?) -> Unit) ? = null
    override val layoutId: Int
        get() = R.layout.dialog_chat_with_topic

    override val backgroundTransparent: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dataFieldTopic = arguments?.getParcelable<DataFieldTopic>(KEY_LIST_SUGGESS)
        viewDataBinding.textTitle.text = dataFieldTopic?.field1
        val adapter = SelectFieldAdapter(
            dataFieldTopic?.option?.toMutableList() ?: mutableListOf(),
            arguments?.getString(KEY_CURRENT_FIELD) ?: "",
            object : ItemClickListener<String> {
                override fun onClick(item: String?, position: Int) {
                    onClickItem?.invoke(item)
                }
            }
        )
        viewDataBinding.selectRcl.adapter = adapter
        viewDataBinding.selectRcl.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        fun newInstance(dataFieldTopic: DataFieldTopic, currentField: String): ChatWithTopicDialog {
            val args = Bundle().apply {
                putParcelable(KEY_LIST_SUGGESS, dataFieldTopic)
                putString(KEY_CURRENT_FIELD, currentField)
            }
            val fragment = ChatWithTopicDialog()
            fragment.arguments = args
            return fragment
        }

        fun dismiss(fm: FragmentManager) {
            val fragment = fm.findFragmentByTag(ChatWithTopicDialog::class.simpleName)
            if (fragment is ChatWithTopicDialog) {
                fragment.dismiss()
            }
        }

        fun show(fm: FragmentManager, dataFieldTopic: DataFieldTopic, currentField: String): ChatWithTopicDialog {
            val fragment = newInstance(dataFieldTopic, currentField)
            fragment.show(fm, ChatWithTopicDialog::class.simpleName)
            return fragment
        }

        private const val KEY_LIST_SUGGESS = "key_list_suggess"
        private const val KEY_CURRENT_FIELD = "key_current_field"
    }
}