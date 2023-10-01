package com.longkd.chatgpt_openai.base.crashreport

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.longkd.chatgpt_openai.feature.MainActivity
import com.longkd.chatgpt_openai.base.BaseActivity
import com.longkd.chatgpt_openai.BuildConfig
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.CommonAction
import com.longkd.chatgpt_openai.base.util.LoggerUtil
import com.longkd.chatgpt_openai.base.util.OnSingleClickListener
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.databinding.ActivityErrorBinding
import kotlinx.parcelize.Parcelize
import org.acra.ReportField
import org.acra.data.CrashReportData
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExErrorActivity : BaseActivity() {
    private var errorList: Array<String> = arrayOf()
    private var errorInfo: ErrorInfo? = null
    private var returnActivity: Class<*>? = null
    private var currentTimeStamp: String? = null
    private var mBinding: ActivityErrorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_error)
        mBinding?.crashHeaderView?.apply {
            setTitle(getStringRes(R.string.error_report_title))
            setRightAction(CommonAction() {
                sendReportEmail()
            }, R.drawable.ic_logo_app)
            setLeftAction(CommonAction() {
                goToReturnActivity()
            },R.drawable.ic_back_black)
        }

        returnActivity = MainActivity::class.java
        errorInfo = intent.getParcelableExtra(ERROR_INFO)
        errorList = intent.getStringArrayExtra(ERROR_LIST) ?: arrayOf()

        // important add guru meditation
        addGuruMeditation()
        currentTimeStamp = getCurrentTimeStamp()
        mBinding?.errorReportEmailButton?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                sendReportEmail()
            }

        })
        mBinding?.errorReportCopyButton?.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                UtilsApp.copyToClipboard(this@ExErrorActivity, buildMarkdown())
                Toast.makeText(
                    this@ExErrorActivity,
                    R.string.crash_report_copied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // normal bugreport
        buildInfo(errorInfo)
        if (errorInfo?.message != 0) {
            errorInfo?.message?.let { mBinding?.errorMessageView?.setText(it) }
        } else {
            mBinding?.errorMessageView?.visibility = View.GONE
            mBinding?.messageWhatHappenedView?.visibility = View.GONE
        }
        mBinding?.errorView?.text = formErrorText(errorList)

        // print stack trace once again for debugging:
        errorList.forEach {
            LoggerUtil.e("FATAL:$it")
        }

    }

    private fun sendReportEmail() {
        val i =
            UtilsApp.createCrashEmailIntent(this, getStringRes(R.string.app_name), buildMarkdown())
        if (i.resolveActivity(packageManager) != null) {
            startActivity(i)
        }
    }

    private fun formErrorText(el: Array<String>?): String {
        val text = StringBuilder()
        if (el != null) {
            for (e in el) {
                text.append("-------------------------------------\n").append(e)
            }
        }
        text.append("-------------------------------------")
        return text.toString()
    }

    private fun goToReturnActivity() {
        val intent = Intent(this, returnActivity)
        NavUtils.navigateUpTo(this, intent)
        startActivity(intent)
    }

    private fun buildInfo(info: ErrorInfo?) {

        var text = ""
        mBinding?.errorInfoLabelsView?.text =
            getString(R.string.info_labels).replace("\\n", "\n")
        text += """
            ${errorInfo?.userAction}
            ${info?.request}
            $currentTimeStamp
            $packageName
            ${BuildConfig.VERSION_NAME}
            $osString
            ${Build.DEVICE}
            ${Build.MODEL}
            ${Build.PRODUCT}
            """.trimIndent()
        mBinding?.errorInfosView?.text = text
    }

    private fun buildJson(): String {
        try {
            val jsonMap: MutableMap<String?, String?> = HashMap()
            jsonMap["user_action"] = errorInfo?.userAction
            jsonMap["request"] = errorInfo?.request
            jsonMap["package"] = packageName
            jsonMap["version"] = BuildConfig.VERSION_NAME
            jsonMap["os"] = osString
            jsonMap["device"] = Build.DEVICE
            jsonMap["model"] = Build.MODEL
            jsonMap["product"] = Build.PRODUCT
            jsonMap["time"] = currentTimeStamp
            jsonMap["exceptions"] = listOf(*errorList).toString()
            jsonMap["user_comment"] = mBinding?.errorCommentBox?.text.toString()
            return JSONObject(jsonMap.toMap()).toString()
        } catch (e: Throwable) {
            Log.e(TAG, "Could not build json")
            e.printStackTrace()
        }
        return ""
    }

    private fun buildMarkdown(): String {
        return try {
            val htmlErrorReport = StringBuilder()
            var userComment = ""
            if (!TextUtils.isEmpty(mBinding?.errorCommentBox?.text)) {
                userComment = mBinding?.errorCommentBox?.text.toString()
            }

            // basic error info
            htmlErrorReport
                .append(
                    String.format(
                        "## Issue explanation (write below this line)\n\n%s\n\n",
                        userComment
                    )
                )
                .append("## Exception")
                .append("\n* __App Name:__ ")
                .append(getString(R.string.app_name))
                .append("\n* __Package:__ ")
                .append(BuildConfig.APPLICATION_ID)
                .append("\n* __Version:__ ")
                .append(BuildConfig.VERSION_NAME)
                .append("\n* __User Action:__ ")
                .append(errorInfo?.userAction)
                .append("\n* __Request:__ ")
                .append(errorInfo?.request)
                .append("\n* __OS:__ ")
                .append(osString)
                .append("\n* __Device:__ ")
                .append(Build.DEVICE)
                .append("\n* __Model:__ ")
                .append(Build.MODEL)
                .append("\n* __Product:__ ")
                .append(Build.PRODUCT)
                .append("\n")

            // Collapse all logs to a single paragraph when there are more than one
            // to keep the GitHub issue clean.
            if (errorList.size > 1) {
                htmlErrorReport
                    .append("<details><summary><b>Exceptions (")
                    .append(errorList.size)
                    .append(")</b></summary><p>\n")
            }

            // add the logs
            for (i in errorList.indices) {
                htmlErrorReport.append("<details><summary><b>Crash log ")
                if (errorList.size > 1) {
                    htmlErrorReport.append(i + 1)
                }
                htmlErrorReport
                    .append("</b>")
                    .append("</summary><p>\n")
                    .append("\n```\n")
                    .append(errorList[i])
                    .append("\n```\n")
                    .append("</details>\n")
            }

            // make sure to close everything
            if (errorList.size > 1) {
                htmlErrorReport.append("</p></details>\n")
            }
            htmlErrorReport.append("<hr>\n")
            htmlErrorReport.toString()
        } catch (e: Throwable) {
            Log.e(TAG, "Error while erroring: Could not build markdown")
            e.printStackTrace()
            ""
        }
    }

    private val osString: String
        get() {
            val osBase =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Build.VERSION.BASE_OS else "Android"
            val osName = try {
                System.getProperty("os.name")
            } catch (ex: Exception) {
                Strings.EMPTY
            }
            return osName + " " + (if (osBase.isEmpty()) "Android" else osBase) + " " + Build.VERSION.RELEASE + " - " + Build.VERSION.SDK_INT
        }

    private fun addGuruMeditation() {
        // just an easter egg
        var text = mBinding?.errorSorryView?.text.toString()
        text += """
            
            """.trimIndent()
        mBinding?.errorSorryView?.text = text
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToReturnActivity()
    }

    private fun getCurrentTimeStamp(): String {
        return try {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm")
            df.timeZone = TimeZone.getTimeZone("GMT")
            df.format(Date())
        } catch (ex: Exception) {
            Strings.EMPTY
        }
    }

    @Parcelize
    class ErrorInfo(
        val userAction: String = Strings.EMPTY,
        val request: String = Strings.EMPTY,
        @StringRes
        val message: Int = 0
    ) : Parcelable {


        companion object {
            fun make(
                userAction: String, request: String, @StringRes message: Int
            ): ErrorInfo {
                return ErrorInfo(userAction, request, message)
            }
        }
    }

    companion object {
        // LOG TAGS
        val TAG = ExErrorActivity::class.java.toString()

        // BUNDLE TAGS
        const val ERROR_INFO = "error_info"
        const val ERROR_LIST = "error_list"

        // Error codes
        const val ERROR_UI_ERROR = "UI Error"
        const val ERROR_USER_REPORT = "User report"
        const val ERROR_UNKNOWN = "Unknown"
        fun reportError(
            context: Context,
            el: List<Throwable>?,
            rootView: View?,
            errorInfo: ErrorInfo
        ) {
            if (rootView != null) {
                Snackbar.make(rootView, R.string.error_snackbar_message, 3 * 1000)
                    .setActionTextColor(Color.BLACK)
                    .setAction(
                        context.getString(R.string.error_snackbar_action)
                            .uppercase(Locale.getDefault())
                    ) { v: View? -> startErrorActivity(context, errorInfo, el) }
                    .show()
            } else {
                startErrorActivity(context, errorInfo, el)
            }
        }

        private fun startErrorActivity(
            context: Context, errorInfo: ErrorInfo, el: List<Throwable>?
        ) {
            val intent = Intent(context, ExErrorActivity::class.java)
            intent.putExtra(ERROR_INFO, errorInfo)
            intent.putExtra(ERROR_LIST, elToSl(el))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        fun reportError(
            context: Context, e: Throwable?, rootView: View?, errorInfo: ErrorInfo
        ) {
            var el: MutableList<Throwable>? = null
            if (e != null) {
                el = Vector()
                el.add(e)
            }
            reportError(context, el, rootView, errorInfo)
        }

        fun reportError(
            context: Context, report: CrashReportData, errorInfo: ErrorInfo?
        ) {
            println("ErrorActivity reportError")
            val el = arrayOf(report.getString(ReportField.STACK_TRACE))
            val intent = Intent(context, ExErrorActivity::class.java)
            intent.putExtra(ERROR_INFO, errorInfo)
            intent.putExtra(ERROR_LIST, el)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        private fun getStackTrace(throwable: Throwable): String {
            val sw = StringWriter()
            val pw = PrintWriter(sw, true)
            throwable.printStackTrace(pw)
            return sw.buffer.toString()
        }

        // errorList to StringList
        private fun elToSl(stackTraces: List<Throwable>?): Array<String> {
            val out = ArrayList<String>()
            stackTraces?.forEach {
                out.add(getStackTrace(it))

            }
            return out.toTypedArray()
        }
    }
}