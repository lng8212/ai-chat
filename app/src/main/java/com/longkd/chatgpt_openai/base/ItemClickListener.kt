package com.longkd.chatgpt_openai.base

interface ItemClickListener<T> {
    fun onClick(item: T?, position: Int = 0)
     fun onLongClick(item: T?, position: Int = 0){}
}

