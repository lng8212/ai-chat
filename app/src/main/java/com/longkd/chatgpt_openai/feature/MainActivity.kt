package com.longkd.chatgpt_openai.feature

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseActivity
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.util.MyActivityObserver
import com.longkd.chatgpt_openai.databinding.ActivityMainBinding
import com.longkd.chatgpt_openai.feature.summary.SummaryFileFragment
import com.longkd.chatgpt_openai.service.RemindAlarmReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("SameParameterValue")
class MainActivity : BaseActivity() {
    private var mBinding: ActivityMainBinding? = null
    private var mainFragment: MainFragment? = null
    private var mBackStackChangedListener: FragmentManager.OnBackStackChangedListener? = null
    private var myActivityObserver = MyActivityObserver({
    }, {
        setupAlarmDaily()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainFragment = MainFragment()

        supportFragmentManager.beginTransaction().replace(
            R.id.mainFrameLayoutContainer, mainFragment ?: MainFragment(),
            MainFragment::class.java.name
        ).commitAllowingStateLoss()
        mBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
            val listFm = supportFragmentManager.fragments
            val lastfm = listFm.findLast {
                it is BaseFragment<*> && it.initBackAction
            } as? BaseFragment<*>
            lastfm?.mActionKeyBack?.requestFocus()
        }
        mBackStackChangedListener?.let {
            supportFragmentManager.addOnBackStackChangedListener(it)
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(myActivityObserver)
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Default) {
                delay(1500)
            }
        }
    }


    private fun setupAlarmDaily() {
        kotlin.runCatching {
            val alarmMgr: AlarmManager? = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(this, RemindAlarmReceiver::class.java)
            intent.action = RemindAlarmReceiver.ACTION_DAILY
            val alarmIntent: PendingIntent =
                getBroadcastIntent(this, RemindAlarmReceiver.NOTIFY_REMIND_RECENT, intent)
            kotlin.runCatching {
                alarmMgr?.cancel(alarmIntent)
            }

            alarmMgr?.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR * 2,
                AlarmManager.INTERVAL_HOUR * 6,
                alarmIntent
            )
        }
    }

    private fun getBroadcastIntent(
        context: Context?,
        requestCode: Int,
        intent: Intent
    ): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context, requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getBroadcast(
                context, requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        setupAlarmDaily()
        kotlin.runCatching {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(myActivityObserver)
        }
    }

    fun onRefeshDataSummary() {
        val fragment =
            supportFragmentManager.findFragmentByTag(SummaryFileFragment::class.java.name)
        if (fragment != null) {
            (fragment as SummaryFileFragment).onRefeshData()
        }
    }
}