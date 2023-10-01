package com.longkd.chatgpt_openai.feature.splash


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.text.*
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.ContextLanguageUtils
import com.longkd.chatgpt_openai.databinding.ActivitySplashBinding
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.feature.language.LanguageActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var mBinding: ActivitySplashBinding? = null
    private var mOpenMainAction: (() -> Unit)? = null
    private var timerWaitAds: CountDownTimer? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !NotificationManagerCompat.from(
                this
            ).areNotificationsEnabled()
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            initView()
        }
        initAction()

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        initView()
    }

    override fun attachBaseContext(base: Context?) {
        var context = base
        CommonSharedPreferences.getInstance().getLanguage()?.let { lang ->
            context = context?.let { it1 -> ContextLanguageUtils.updateLocale(it1, lang) }
        }
        super.attachBaseContext(context)
    }

    private fun initAction() {
        lifecycleScope.launch {
            delay(3000)
            moveToMain()
        }
    }


    private fun initView() {
        mOpenMainAction = {
            startMainAtc()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timerWaitAds?.cancel()
    }

    private fun moveToMain() {
        mOpenMainAction?.invoke()
        mOpenMainAction = null

    }

    private fun startMainAtc() {
        if (CommonSharedPreferences.getInstance().getFirstLanguage() == false){
            val mIntent = Intent(this@SplashActivity, LanguageActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            mIntent.data = intent.data
            mIntent.action = intent.action
            intent?.extras?.let { mIntent.putExtras(it) }
            startActivity(mIntent)
            finish()
            return
        } else {
            timerWaitAds?.cancel()
            val mIntent = Intent(this, MainActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            mIntent.data = intent.data
            mIntent.action = intent.action
            intent?.extras?.let { mIntent.putExtras(it) }
            startActivity(mIntent)
        }
    }
}
