package com.longkd.chatgpt_openai.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.longkd.chatgpt_openai.dialog.DialogLostInternet
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.ContextUtilsLanguage
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.addFragmentWithAnimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {
    private var mDayUseApp: Long = 1L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                /** full screen làm android:windowSoftInputMode="adjustResize" not working*/
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
        initPremium()
    }

    private fun initPremium() {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val time = packageManager.getPackageInfo(packageName, 0).firstInstallTime
                mDayUseApp = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - time)
            } catch (_: Exception) {

            }
        }
    }


    override fun attachBaseContext(base: Context?) {
        var context = base
        CommonSharedPreferences.getInstance().getLanguage()?.let { lang ->
            context = context?.let { it1 -> ContextUtilsLanguage.updateLocale(it1, lang) }
        }
        super.attachBaseContext(context)
    }

    fun getStringRes(@StringRes res: Int): String {
        return try {
            baseContext?.resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }


    fun pushCleanerScreenWithAnimate(
        fragment: Fragment,
        fromFragment: Fragment?,
        tag: String,
        addToBackStack: Boolean = true,
        @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        addFragmentWithAnimation(
            fromFragment,
            fragment,
            tag,
            frameId,
            addToBackStack
        )
    }

    fun showDialogError(message: String?) {
        try {
            Toast.makeText(this, message ?: Strings.EMPTY, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogSuccess(message: String?) {
        try {
            Toast.makeText(this, message ?: Strings.EMPTY, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogError(@StringRes message: Int) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogSuccess(@StringRes message: Int) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun pushScreenWithAnimate(
        fragment: Fragment,
        fromFragment: Fragment?,
        tag: String, @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        addFragmentWithAnimation(fromFragment, fragment, tag, frameId)
    }

    fun checkInternetStatus(): Boolean {
        return DialogLostInternet.checkInternetStatus(
            DialogLostInternet.StatusCheckType.S_ALL,
            this,
            supportFragmentManager
        )
    }
}