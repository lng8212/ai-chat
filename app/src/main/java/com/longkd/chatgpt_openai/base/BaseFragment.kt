package com.longkd.chatgpt_openai.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.longkd.chatgpt_openai.base.widget.header.BaseHeaderView
import com.longkd.chatgpt_openai.dialog.DialogChooseActionOcr
import com.longkd.chatgpt_openai.feature.MainFragment
import com.longkd.chatgpt_openai.feature.home_new.gallery.ListGalleryFragment
import com.longkd.chatgpt_openai.feature.home_new.gallery.OCRResultFragment
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.LoggerUtil
import com.longkd.chatgpt_openai.base.util.PermissionUtils
import com.longkd.chatgpt_openai.base.util.Strings
import com.longkd.chatgpt_openai.base.util.UtilsApp
import com.longkd.chatgpt_openai.base.util.addFragmentWithAnimation
import com.longkd.chatgpt_openai.base.util.addFragmentWithAnimationZoom
import com.longkd.chatgpt_openai.base.util.addFragmentWithNoAnimation
import com.longkd.chatgpt_openai.base.util.replaceFragment
import com.longkd.chatgpt_openai.databinding.BaseFragmentBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


abstract class BaseFragment<VDB : ViewDataBinding>(@LayoutRes val contentViewResId: Int) :
    Fragment() {

    private var mOnViewCreatedListener: (() -> Unit)? = null
    var mOnViewDestroyedListener: (() -> Unit)? = null
    private var mIsShowLoading = false
    private var baseContentLayout: FrameLayout? = null
    private var mOnDestroyedListener: (() -> Unit)? = null
    private var mParentBinding: BaseFragmentBinding? = null
    var mainFragment: MainFragment? = null
    var mBinding: VDB? = null
        private set
    var mHeaderView: BaseHeaderView? = null

    var mActionKeyBack: View? = null
    private var mCurrentBannerLoadScreen: String = Strings.EMPTY
    private var cameraPhotoFilePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getExtractData()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (UtilsApp.mDisableFragmentAnimations) {
            val a: Animation = object : Animation() {}
            a.duration = 0
            return a
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    abstract fun initViews()
    abstract fun initActions()
    abstract var initBackAction: Boolean
    open fun getExtractData() {}

    private fun initChildView(inflater: LayoutInflater, container: ViewGroup?) {
        mParentBinding = BaseFragmentBinding.inflate(inflater)
        baseContentLayout = mParentBinding?.baseFmFrameLayoutMain
        mBinding = DataBindingUtil.inflate(inflater, contentViewResId, container, false)
        baseContentLayout?.apply {
            removeAllViews()
            addView(mBinding?.root)
        }
        mHeaderView = initHeaderView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initChildView(inflater, container)//init child layout fragment
        return mParentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (initBackAction)
            initActionKeyBack(view)
        initViews()
        initActions()
        initData()
        view.isClickable = true
        mOnViewCreatedListener?.invoke()
        mainFragment = activity?.supportFragmentManager?.fragments?.find {
            it is MainFragment
        } as? MainFragment
    }

    fun showLoadingContainer(isShow: Boolean) {
        mIsShowLoading = isShow
        mParentBinding?.rlLoadingContainer?.visibility = if (isShow) View.VISIBLE else
            View.GONE
    }

    open fun showLoading() {
        showLoadingContainer(true)
    }

    open fun hideLoading() {
        showLoadingContainer(false)
    }

    fun pushScreen(
        fragment: Fragment,
        tag: String,
        @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        (activity as? AppCompatActivity)?.addFragmentWithNoAnimation(this, fragment, tag, frameId)
    }

    fun replaceScreen(
        fragment: Fragment,
        tag: String,
        @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        (activity as? AppCompatActivity)?.replaceFragment(fragment, tag, frameId, false, false)
    }

    fun pushScreenWithAnimate(
        fragment: Fragment,
        tag: String, @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        (activity as? AppCompatActivity)?.addFragmentWithAnimation(this, fragment, tag, frameId)
    }

    fun pushScreenWithAnimateZoom(
        fragment: Fragment,
        tag: String, @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        (activity as? AppCompatActivity)?.addFragmentWithAnimationZoom(this, fragment, tag, frameId)
    }

    fun pushCleanerScreenWithAnimate(
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean = true,
        @IdRes frameId: Int = R.id.mainFrameLayoutContainer
    ) {
        (activity as? AppCompatActivity)?.addFragmentWithAnimation(
            this,
            fragment,
            tag,
            frameId,
            addToBackStack
        )
    }

    fun popBackStackImmediate(fragmentName: String?, flag: Int = 0) {
        try {
            activity?.supportFragmentManager?.executePendingTransactions()
            activity?.supportFragmentManager?.popBackStackImmediate(fragmentName, flag)
        } catch (e: Exception) {
            LoggerUtil.e("popBackStackImmediate,1 error=${e.stackTraceToString()}")
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    activity?.supportFragmentManager?.popBackStackImmediate(fragmentName, flag)
                } catch (e: Exception) {
                    LoggerUtil.e("popBackStackImmediate,2 error=${e.stackTraceToString()}")
                }
            }, 500)

        }
    }

    fun popBackstackAllFragment() {
        popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun popBackstackAllFragment(fragmentTag: String) {
        popBackStackImmediate(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun popBackStack(fragmentName: String?, flag: Int = 0) {
        try {
            activity?.supportFragmentManager?.executePendingTransactions()
            activity?.supportFragmentManager?.popBackStack(fragmentName, flag)
        } catch (e: Exception) {
            LoggerUtil.e("popBackStack,1 error=${e.stackTraceToString()}")
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    activity?.supportFragmentManager?.popBackStack(fragmentName, flag)
                } catch (e: Exception) {
                    LoggerUtil.e("popBackStack,2 error=${e.stackTraceToString()}")
                }
            }, 500)

        }
    }

    fun popBackStack() {
        if (!isStateSaved) {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    fun popBackStackImmediate() {
        if (!isStateSaved) {
            activity?.supportFragmentManager?.popBackStackImmediate()
        }
    }

    fun getStringRes(@StringRes res: Int): String {
        return try {
            context?.resources?.getString(res) ?: Strings.EMPTY
        } catch (e: Exception) {
            Strings.EMPTY
        }
    }

    private fun initActionKeyBack(view: View?) {
        mActionKeyBack = view
        view?.apply {
            isFocusableInTouchMode = true
            requestFocus()
            setOnKeyListener { _, keyCode, keyEvent ->
                when (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    true -> handleOnBackPress()
                    else -> false
                }
            }
        }

    }

    open fun handleOnBackPress(): Boolean = false

    override fun onDestroy() {
        mActionKeyBack?.setOnKeyListener(null)
        this.mOnDestroyedListener?.invoke()
        super.onDestroy()
    }

    abstract fun initData()

    open fun onRefresh() {
    }

    fun getParentBinding() = mParentBinding

    fun setShowHeader(isShowHeader: Boolean) {
        mHeaderView?.visibility = if (isShowHeader) View.VISIBLE else View.GONE
    }


    fun showDialogError(message: String?) {
        try {
            Toast.makeText(context, message ?: "", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogSuccess(message: String?) {
        try {
            Toast.makeText(context, message ?: "", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogError(@StringRes message: Int) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }

    fun showDialogSuccess(@StringRes message: Int) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mOnViewDestroyedListener?.invoke()
    }

    open fun initHeaderView(): BaseHeaderView? = null

    fun showDialogOcr() {
        DialogChooseActionOcr.show(childFragmentManager).apply {
            actionOpenCamera = {
                handleOpenCamera()
            }

            actionSelectImage = {
                mainFragment?.pushScreenWithAnimate(
                    ListGalleryFragment.newInstance(Constants.GALLERY_TYPE.OCR),
                    ListGalleryFragment::class.java.name
                )
            }
        }
    }

    private fun handleOpenCamera() {
        if (!PermissionUtils.checkPermissionCamera(context)) {
            pickImage()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickImage()
            }
        }

    private fun pickImage() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFileInAppDir()
        } catch (ex: IOException) {
            null
        }
        photoFile?.let { file ->
            val photoURI: Uri =
                FileProvider.getUriForFile(requireContext(), activity?.packageName ?: "", file)
            cameraPhotoFilePath = photoURI
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        cameraActivityResultLauncher.launch(cameraIntent)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFileInAppDir(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imagePath = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(imagePath, "JPEG_${timeStamp}_" + ".jpg")
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        cameraPhotoFilePath?.let {
            if (result.resultCode == Activity.RESULT_OK) {
                mainFragment?.pushScreenWithAnimate(
                    OCRResultFragment.newInstance(it.toString()),
                    OCRResultFragment::class.java.name
                )
            }
        }
    }
}
