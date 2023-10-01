package com.longkd.chatgpt_openai.feature.exit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseActivity
import com.bumptech.glide.Glide

class ActivityExit : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                finish()
            } catch (e: Exception) {
                finish()
            }
        }, 1500)
        setContentView(R.layout.activity_thank_you)
        initView()
    }

    companion object {
        fun exitApplicationAndRemoveFromRecent(context: Context) {
            val intent = Intent(context, ActivityExit::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(intent)
        }
    }

    private fun initView() {
        Glide.with(this).load(R.drawable.img_screen_exit).into(
            findViewById(R.id.thankYou_imgSplash)
        )
        Glide.with(this).load(R.drawable.img_exit_app_blur).into(
            findViewById(R.id.thankYou_imgSplashBg)
        )
    }
}