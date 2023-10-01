package com.longkd.chatgpt_openai.feature

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity, var listFm: ArrayList<Fragment>) :
    FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = listFm.size

    override fun createFragment(position: Int): Fragment {
        return listFm[position]
    }
}