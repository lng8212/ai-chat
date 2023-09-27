package com.longkd.base_android.base

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.longkd.base_android.base.locale.LocaleManager
import com.longkd.base_android.ktx.hideKeyBoard
import com.longkd.base_android.navigation.IntentDirections
import com.longkd.base_android.navigation.Navigator
import com.longkd.base_android.navigation.ResultCallback
import com.longkd.base_android.utils.BindingReflex
import com.longkd.base_android.utils.ViewUtils
import kotlinx.coroutines.launch
import java.util.Locale


/**
 * @Author: longkd
 * @Since: 22:27 - 11/08/2023
 */

abstract class BaseActivity<out VB : ViewBinding, out VM : BaseViewModel<IntentDirections>> :
    AppCompatActivity(),
    Navigator<IntentDirections>,
    BaseViewController {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract val viewModel: VM

    @LayoutRes
    protected open val layoutId = -1

    @LayoutRes
    protected open val layoutLoadingId = ViewUtils.LOADING_DEFAULT

    private var loadingView: View? = null

    protected open val isAutoClearFocus: Boolean = true

    protected open val enableStateScreen: Boolean = false

    protected open val enableSupportLanguage: Boolean = false

    private var resultCallback: ResultCallback? = null

    private val startActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.extras
            resultCallback?.onResult(it.resultCode.toString(), data)
            resultCallback = null
        }

    private fun createBinding(): VB {
        return if (layoutId == -1) {
            BindingReflex.reflexViewBinding(javaClass, layoutInflater)
        } else {
            try {
                DataBindingUtil.inflate(layoutInflater, layoutId, null, false)
            } catch (e: Exception) {
                BindingReflex.reflexViewBinding<VB>(javaClass, layoutInflater)
            }
        }
    }

    private fun getLoadingView(): View? {
        return try {
            when (layoutLoadingId) {
                -1 -> null
                ViewUtils.LOADING_DEFAULT -> ViewUtils.getDefaultLoadingView(this)
                else -> layoutInflater.inflate(layoutLoadingId, _binding?.root as? ViewGroup, true)
                    .apply {
                        isFocusable = false
                        isClickable = false
                    }
            }.apply {
                this?.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    protected fun setVariable(variableId: Int, value: Any): Boolean {
        return if (binding is ViewDataBinding) {
            (binding as ViewDataBinding).setVariable(variableId, value)
        } else false
    }

    override fun attachBaseContext(newBase: Context) {
        if (enableSupportLanguage) {
            val newContext =
                LocaleManager.of(newBase).updateBaseContextLanguage(getLanguageCode(newBase))
            super.attachBaseContext(newContext)
        } else {
            super.attachBaseContext(newBase)
        }
    }

    @Suppress("DEPRECATION")
    private fun fullScreen() {
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
        _binding = createBinding()
        setContentView(_binding!!.root)
        setupBindingLifeCycle()
        initView()
        setupUiState()
        setupNavigator()
        setupViewController()
        observer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        loadingView = null
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isAutoClearFocus) {
            ev?.let {
                if (ev.action == MotionEvent.ACTION_DOWN) {
                    currentFocus?.let { viewFocus ->
                        if (viewFocus is EditText) {
                            val outRect = Rect()
                            viewFocus.getGlobalVisibleRect(outRect)
                            if (!outRect.contains(it.rawX.toInt(), it.rawY.toInt())) {
                                viewFocus.clearFocus()
                                hideKeyBoard()
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun navigateTo(directions: IntentDirections) {
        val intent = directions.intent
        intent.setClass(this, directions.destination.java)
        startActivity(intent)
    }

    override fun navigateOff(directions: IntentDirections) {
        navigateTo(directions)
        finish()
    }

    override fun navigateOffAll(directions: IntentDirections) {
        val intent = directions.intent
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun navigateForResult(
        directions: IntentDirections,
        key: String,
        result: ResultCallback
    ) {
        val intent = directions.intent
        intent.setClass(this, directions.destination.java)
        resultCallback = result
        startActivityResult.launch(intent)
    }

    override fun back(key: String?, data: Bundle?) {
        if (data != null) {
            val intent = Intent()
            intent.putExtras(data)
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    override fun backTo(id: Int, inclusive: Boolean, key: String?, data: Bundle?) {
        if (data != null) {
            val intent = Intent()
            intent.putExtras(data)
            setResult(RESULT_OK, intent)
        }
        finish()
    }

    private fun setupNavigator() {
        viewModel.setupNavigator(this)
    }

    private fun setupViewController() {
        viewModel.setViewController(this)
    }

    private fun setupUiState() {
        if (enableStateScreen) {
            addLoadingView()
            collectUiState()
        }
    }

    private fun addLoadingView() {
        loadingView = getLoadingView()
        loadingView?.let {
            (binding.root as ViewGroup).addView(it)
        }
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.getValueIfNotHandled()?.let {
                        it.onState(
                            idle = { onStateIdle() },
                            success = { onStateSuccess() },
                            loading = { onStateLoading() },
                            error = { onStateError(viewModel.errorMessage) }
                        )
                    }
                }
            }
        }
    }

    private fun setupBindingLifeCycle() {
        if (binding is ViewDataBinding) {
            (binding as ViewDataBinding).lifecycleOwner = this
        }
    }

    abstract fun initView()

    open fun observer() {}

    open fun onStateIdle() {}

    open fun onStateSuccess() {
        loadingView?.visibility = View.GONE
    }

    open fun onStateLoading() {
        loadingView?.visibility = View.VISIBLE
    }

    open fun onStateError(message: String?) {
        loadingView?.visibility = View.GONE
    }

    open fun getLanguageCode(context: Context): String {
        return Locale.getDefault().language
    }

    companion object
}