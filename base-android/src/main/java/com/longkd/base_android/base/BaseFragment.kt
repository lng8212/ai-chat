package com.longkd.base_android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.viewbinding.ViewBinding
import com.longkd.base_android.R
import com.longkd.base_android.base.toolbar.FragmentConfiguration
import com.longkd.base_android.navigation.Navigator
import com.longkd.base_android.navigation.ResultCallback
import com.longkd.base_android.utils.BindingReflex
import com.longkd.base_android.utils.ViewUtils
import kotlinx.coroutines.launch

/**
 * @Author: longkd
 * @Since: 10:10 - 12/08/2023
 */
abstract class BaseFragment<VB : ViewBinding, out VM : BaseViewModel<NavDirections>> : Fragment(),
    Navigator<NavDirections>, BaseViewController {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract val viewModel: VM

    @LayoutRes
    protected open val layoutId = -1

    @LayoutRes
    protected open val layoutLoadingId = ViewUtils.LOADING_DEFAULT

    protected open val enableStateScreen: Boolean = false

    protected open val enableOnBackPressed: Boolean = false

    protected open val enableAnimationFragment: Boolean = false

    protected open val fragmentConfiguration: FragmentConfiguration = FragmentConfiguration()

    private fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        return if (layoutId == -1) {
            BindingReflex.reflexViewBinding(javaClass, inflater)
        } else {
            try {
                DataBindingUtil.inflate(inflater, layoutId, container, false)
            } catch (e: Exception) {
                BindingReflex.reflexViewBinding<VB>(javaClass, inflater)
            }
        }
    }

    private fun getLoadingView(): View? {
        return try {
            when (layoutLoadingId) {
                -1 -> null
                ViewUtils.LOADING_DEFAULT -> ViewUtils.getDefaultLoadingView(requireContext())
                else -> layoutInflater.inflate(layoutLoadingId, _binding?.root as? ViewGroup)
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

    private var loadingView: View? = null

    protected fun setVariable(variableId: Int, value: Any): Boolean {
        return if (binding is ViewDataBinding) {
            (binding as ViewDataBinding).setVariable(variableId, value)
        } else false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = createBinding(inflater = inflater, container = container)
        setupBindingLifeCycle()
        initView()
        setupUiState()
        setupNavigator()
        setupViewController()
        setupOnBackPressed()
        observer()
        return _binding!!.root
    }

    override fun onDestroyView() {
        _binding = null
        loadingView = null
        super.onDestroyView()
    }

    override fun navigateTo(directions: NavDirections) {
        findNavController().navigate(
            directions,
            navOptions = navOptions { navOptionsBuilder(this) }
        )
    }

    override fun navigateOff(directions: NavDirections) {
        findNavController().run {
            navigate(
                directions,
                navOptions = navOptions {
                    navOptionsBuilder(this)
                    popUpTo(currentDestination?.id ?: 0) {
                        inclusive = true
                    }
                }
            )
        }
    }

    override fun navigateOffAll(directions: NavDirections) {
        findNavController().run {
            navigate(
                directions,
                navOptions = navOptions {
                    navOptionsBuilder(this)
                    popUpTo(graph.id) {
                        inclusive = true
                    }
                }
            )
        }
    }

    override fun navigateForResult(
        directions: NavDirections,
        key: String,
        result: ResultCallback
    ) {
        navigateTo(directions)
        setFragmentResultListener(key) { _, bundle ->
            clearFragmentResultListener(key)
            result.onResult(key, bundle)
        }
    }

    override fun back(key: String?, data: Bundle?) {
        findNavController().navigateUp()
        if (data != null) {
            require(key is String) { "Key result in fragment required String not null" }
            setFragmentResult(key, data)
        }
    }

    override fun backTo(id: Int, inclusive: Boolean, key: String?, data: Bundle?) {
        findNavController().popBackStack(id, inclusive)
        if (data != null) {
            require(key is String) { "Key result in fragment required String not null" }
            setFragmentResult(key, data)
        }
    }

    private fun setupOnBackPressed() {
        if (enableOnBackPressed) {
            activity?.onBackPressedDispatcher?.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            onBackPressed()
                        }
                    }
                })
        }
    }

    private fun setupBindingLifeCycle() {
        _binding?.let {
            if (it is ViewDataBinding) {
                (it as ViewDataBinding).lifecycleOwner = this
            }
        }
    }

    private fun setupUiState() {
        if (enableStateScreen) {
            addLoadingView()
            collectUiState()
        }
    }

    private fun setupNavigator() {
        viewModel.setupNavigator(this)
    }

    private fun setupViewController() {
        viewModel.setViewController(this)
    }

    private fun addLoadingView() {
        loadingView = getLoadingView()
        loadingView?.let {
            (_binding!!.root as ViewGroup).addView(it)
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
                            error = { onStateError() }
                        )
                    }
                }
            }
        }
    }

    private fun defaultNavOptionsBuilder(builder: NavOptionsBuilder) {
        if (enableAnimationFragment) {
            builder.anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
            }
        }
    }

    open fun initData() {}

    abstract fun initView()

    open fun observer() {}

    open fun onStateIdle() {}

    open fun navOptionsBuilder(builder: NavOptionsBuilder) {
        defaultNavOptionsBuilder(builder)
    }

    open fun onStateSuccess() {
        loadingView?.visibility = View.GONE
    }

    open fun onStateLoading() {
        loadingView?.visibility = View.VISIBLE
    }

    open fun onStateError() {
        loadingView?.visibility = View.GONE
    }

    open fun onBackPressed() {
        findNavController().navigateUp()
    }
}