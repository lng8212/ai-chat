package com.longkd.chatgpt_openai.base.util

import android.app.Activity
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.model.ChatBaseDto
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object UtilsApp {
    var mDisableFragmentAnimations = false
    fun isInternetAvailable(): Boolean {
        try {
            val address = InetAddress.getByName("www.google.com")
            return !address.equals(Strings.EMPTY)
        } catch (e: UnknownHostException) {
            // Log error
        }

        return false
    }


    fun hideKeyboard(activity: Activity?) {
        val imm: InputMethodManager? =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as? InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity?.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(activity: Activity?){
        (activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(2, 1)
    }

    fun copyToClipboard(context: Context?, text: String) {
        val clipboard: ClipboardManager? =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip: ClipData =
            ClipData.newPlainText(context?.getString(R.string.app_name) ?: Strings.EMPTY, text)
        clipboard?.setPrimaryClip(clip)
    }


    private fun createEmailIntent(context: Context, appName: String): Intent {
        val toEmail = context.getString(R.string.email_feedback)
        val release = Build.VERSION.RELEASE
        val SDK = Build.VERSION.SDK_INT
        val yourName = "customer"
        val phoneName = Build.MODEL
        val manager = context.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(
                context.packageName, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val version1 = info?.versionName
        val subject = "$appName feedback:"
        val message =
            """
        --------------------
        Device information:
        
        Phone name: $phoneName
        API Level: $SDK
        Version: $release
        App version: $version1
        Username: $yourName
        --------------------
        
        Content: 
        
        """.trimIndent()
        val sendTo = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode(toEmail) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(message)
        val uri = Uri.parse(uriText)
        sendTo.data = uri
        val resolveInfo =
            context.packageManager.queryIntentActivities(sendTo, 0)

        // Emulators may not like this check...
        if (resolveInfo.isNotEmpty()) {
            return sendTo
        }

        // Nothing resolves send to, so fallback to send...
        val send = Intent(Intent.ACTION_SEND)
        send.type = "text/plain"
        send.putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
        send.putExtra(Intent.EXTRA_SUBJECT, subject)
        send.putExtra(Intent.EXTRA_TEXT, message)
        return Intent.createChooser(send, "Send feedback for developer")
    }

    fun createCrashEmailIntent(context: Context, appName: String, content: String): Intent {
        val toEmail = context.getString(R.string.app_name)
        val release = Build.VERSION.RELEASE
        val SDK = Build.VERSION.SDK_INT
        val yourName = "customer"
        val phoneName = Build.MODEL
        val manager = context.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(
                context.packageName, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val version1 = info?.versionName
        val subject = "$appName Crash feedback:"
        val message =
            """
        --------------------
        Device information:
        
        Phone name: $phoneName
        API Level: $SDK
        Version: $release
        App version: $version1
        Username: $yourName
        --------------------
        
        Content: $content
        
        """.trimIndent()
        val sendTo = Intent(Intent.ACTION_SENDTO)
        val uriText = "mailto:" + Uri.encode(toEmail) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(message)
        val uri = Uri.parse(uriText)
        sendTo.data = uri
        val resolveInfo =
            context.packageManager.queryIntentActivities(sendTo, 0)

        // Emulators may not like this check...
        if (resolveInfo.isNotEmpty()) {
            return sendTo
        }

        // Nothing resolves send to, so fallback to send...
        val send = Intent(Intent.ACTION_SEND)
        send.type = "text/plain"
        send.putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
        send.putExtra(Intent.EXTRA_SUBJECT, subject)
        send.putExtra(Intent.EXTRA_TEXT, message)
        return Intent.createChooser(send, "Send feedback for developer")
    }


    fun setSystemLocale(lang: String, context: Context): Context {
        val myLocale = Locale(lang)
        val resources = context.resources
        val configuration = Configuration()
        setSystemLocale(myLocale, configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            applyLocaleChange(context, configuration)
        } else {
            applyLocaleChangeLegacy(resources, configuration)
        }
        return context
    }

    private fun setSystemLocale(locale: Locale, configuration: Configuration) {
        Locale.setDefault(locale)
        configuration.setLocale(locale)
    }

    private fun applyLocaleChange(context: Context, configuration: Configuration) {
        context.createConfigurationContext(configuration)
    }

    private fun applyLocaleChangeLegacy(resources: Resources, configuration: Configuration) {
        val displayMetrics = resources.displayMetrics
        resources.updateConfiguration(configuration, displayMetrics)
    }

    fun openStore(mContext: Context, packageName: String) {
        try {
            mContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            mContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }

    fun getIconOfApkFile(context: Context, path: String?): Drawable? {
        return if (path.isNullOrBlank()) {
            null
        } else try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageArchiveInfo(path, 0) ?: return null
            packageInfo.applicationInfo.sourceDir = path
            packageInfo.applicationInfo.publicSourceDir = path
            packageInfo.applicationInfo.loadIcon(pm)
        } catch (e: java.lang.Exception) {
            null
        }
    }


    fun setTextHtml(textView: TextView?, value: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView?.text =
                    Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
            } else {
                textView?.text = Html.fromHtml(value)
            }
        } catch (ex: Exception) {
            LoggerUtil.e(ex.stackTraceToString())
        }
    }


    fun openBrowser(context: Context?, url: String?) {
        if (context == null)
            return
        try {
            val webpage: Uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            LoggerUtil.e("openBrowser,${e.stackTraceToString()}")
        }
    }
    fun isConnectWifi(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true)
                return true
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
        return false
    }

    fun isNightSight(
        startDatetime: String = "23:00",
        endDatetime: String = "07:00"
    ): Boolean {
        val simpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        try {
            val startDate: Date? = simpleDateFormat.parse(startDatetime)
            val endDate: Date? = simpleDateFormat.parse(endDatetime)
            val timeForBooking: Date? = simpleDateFormat.parse(simpleDateFormat.format(Date()))
            if (timeForBooking?.after(startDate) == true && timeForBooking.before(endDate)) {
                return true
            }
            if (startDate?.before(endDate) == true) { // they are on the same day
                if (timeForBooking?.after(startDate) == true && timeForBooking.before(endDate)) {
                    return true
                }
            } else { // interval crossing midnight
                if (timeForBooking?.after(startDate) == true && timeForBooking.before(endDate)) {
                    return true
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    fun getActivityIntent(context: Context?, requestCode: Int, intent: Intent): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context, requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                context, requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }


    @JvmStatic
    fun sendFeedBack(context: Context, appName: String) {
        context.startActivity(
            createEmailIntent(
                context,
                appName
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
fun Activity.nextScreen(){
    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)

}

fun Activity.backScreen() {
    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
}
fun View.setAllParentsClip(enabled: Boolean) {
    var parent = parent
    while (parent is ViewGroup) {
        parent.clipChildren = enabled
        parent.clipToPadding = enabled
        parent = parent.parent
    }
}

fun SummaryHistoryDto.convertToChatBaseDto(): ChatBaseDto {
    return ChatBaseDto(
        chatDetail = this.chatDetail,
        chatId = System.currentTimeMillis(),
        lastTimeUpdate = this.lastTimeUpdate
    )
}