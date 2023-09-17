package com.longkd.chatai.ui.main.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.longkd.base_android.base.BaseListAdapter
import com.longkd.base_android.base.BaseViewHolder
import com.longkd.chatai.databinding.ItemUserBinding

/**
 * @Author: longkd
 * @Since: 20:49 - 13/08/2023
 */
class UserAdapter(private val context: Context) :
    BaseListAdapter<com.longkd.chatai.data.api.response.UserResponse.Data, UserAdapter.UserViewHolder>() {

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<com.longkd.chatai.data.api.response.UserResponse.Data, *> {
        return UserViewHolder(ItemUserBinding.inflate(layoutInflater, parent, false))
    }

    inner class UserViewHolder(binding: ItemUserBinding) :
        BaseViewHolder<com.longkd.chatai.data.api.response.UserResponse.Data, ItemUserBinding>(binding) {
        @SuppressLint("SetTextI18n")
        override fun onBind(item: com.longkd.chatai.data.api.response.UserResponse.Data) {
            Glide.with(context).load(item.avatar).into(binding.imgUser)
            binding.txtName.text = "${item.firstName} ${item.lastName}"
            binding.txtEmail.text = item.email
        }
    }
}