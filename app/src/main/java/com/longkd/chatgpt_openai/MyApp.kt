

package com.longkd.chatgpt_openai

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.longkd.chatgpt_openai.base.crashreport.ExErrorActivity
import com.longkd.chatgpt_openai.base.util.CommonSharedPreferences
import com.longkd.chatgpt_openai.base.util.ContextUtilsLanguage
import com.longkd.chatgpt_openai.feature.MainActivity
import org.acra.ACRA
import org.acra.config.ACRAConfigurationException
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import java.lang.ref.WeakReference


class MyApp : MultiDexApplication() {

    private lateinit var mContext: WeakReference<Context>
    private var mainNewActivityContext: WeakReference<Context>? = null

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(
            true
        ) // selector in srcCompat isn't supported without this
        CommonSharedPreferences.init(this)
        CommonSharedPreferences.getInstance().getLanguage()?.let { lang ->
            val context =
                applicationContext?.let { it1 -> ContextUtilsLanguage.updateLocale(it1, lang) }
                    ?: applicationContext


            mContext = WeakReference(context)
        }
        instance = this
        // disabling file exposure method check for api n+
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    override fun attachBaseContext(base: Context?) {
        base?.let {
            mContext = WeakReference(it)
        }
        var context = base
        CommonSharedPreferences.getInstance().getLanguage()?.let { lang ->
            context = context?.let { it1 ->
                ContextUtilsLanguage.updateLocale(
                    it1,
                    lang
                )
            }?.applicationContext ?: base
        }
        context?.let {
            mContext = WeakReference(it)
        }
        super.attachBaseContext(context)
        initACRA()
    }

    fun setMainNewActivityContext(activity: Activity) {
        mainNewActivityContext = WeakReference(activity)
    }

    fun getMainContext(): Context {
        return mainNewActivityContext?.get() ?: mContext.get() ?: applicationContext
    }

    fun getMainActivity(): MainActivity? {
        return mainNewActivityContext?.get() as? MainActivity
    }

    /**
     * Called in [.attachBaseContext] after calling the `super` method. Should be
     * overridden if MultiDex is enabled, since it has to be initialized before ACRA.
     */
    private fun initACRA() {
        if (ACRA.isACRASenderServiceProcess()) {
            return
        }
        try {
            initAcra {
                buildConfigClass = BuildConfig::class.java
                reportFormat = StringFormat.JSON
                sendReportsInDevMode = true
            }

        } catch (ace: ACRAConfigurationException) {
            ace.printStackTrace()
            ExErrorActivity.reportError(
                this,
                ace,
                null,
                ExErrorActivity.ErrorInfo.make(
                    ExErrorActivity.ERROR_UNKNOWN,
                    "Could not initialize ACRA crash report",
                    R.string.app_ui_crash
                )
            )
        }
    }

    fun changeLanguageContext() {
        var context = mContext.get()
        CommonSharedPreferences.getInstance().getLanguage()?.let { lang ->
            context = context?.let { it1 ->
                ContextUtilsLanguage.updateLocale(
                    it1,
                    lang
                )
            }?.applicationContext
        }
        context.let {
            mContext = WeakReference(it)
        }
    }


    companion object {
        val TAG = MyApp::class.java.simpleName
        private lateinit var instance: MyApp

        @JvmStatic
        @Synchronized
        fun getInstance(): MyApp {
            if (!this::instance.isInitialized) {
                instance = MyApp()
                return instance
            }
            return instance
        }

        fun context(): Context {
            return getInstance().applicationContext
        }

    }
}