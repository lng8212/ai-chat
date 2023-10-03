package com.longkd.chatgpt_openai.feature.art

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.ItemClickListener
import com.longkd.chatgpt_openai.base.util.*
import com.longkd.chatgpt_openai.databinding.FragmentHomeArtBinding
import com.longkd.chatgpt_openai.feature.home.HomeViewModel
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.longkd.chatgpt_openai.dialog.DialogFailArt
import com.google.android.flexbox.FlexboxLayout
import com.longkd.chatgpt_openai.base.util.Constants
import com.longkd.chatgpt_openai.base.util.UtilsApp
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class HomeArtFragment : BaseFragment<FragmentHomeArtBinding>(R.layout.fragment_home_art) {
    private val mViewModel: HomeViewModel by viewModels()
    private var adapter: StyleArtAdapter? = null
    private var currentListStyleArt = arrayListOf<StyleArtDto>()
    private var selectedItems: SizeImageData? = null
    private var prevSelectView: TextView? = null
    private var numberArt = 0

    companion object {
        private const val DEFAULT_SIZE = 512
        fun newInstance(): HomeArtFragment {
            val args = Bundle()

            val fragment = HomeArtFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViews() {
        activity?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            activity?.window?.decorView?.getWindowVisibleDisplayFrame(r)
            val height: Int =
                mBinding?.root?.rootView?.context?.resources?.displayMetrics?.heightPixels ?: 0
            val diff = height - r.bottom - 120
            if (diff != 0) {
                if (mBinding?.llnViewArtContainer?.paddingBottom !== diff) {
                    mBinding?.llnViewArtContainer?.setPadding(0, 0, 0, diff)
                }
            } else {
                if (mBinding?.llnViewArtContainer?.paddingBottom !== 0) {
                    mBinding?.llnViewArtContainer?.setPadding(0, 0, 0, 0)
                }
            }
        }
        initAdapter()
        mBinding?.fmHomeArtEdtPrompt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s == "") {
                    mBinding?.fmHomeArtLlGenerate?.isEnabled = false
                    mBinding?.fmHomeArtLlGenerate?.alpha = 0.5f
                } else {
                    mBinding?.fmHomeArtLlGenerate?.isEnabled = true
                    mBinding?.fmHomeArtLlGenerate?.alpha = 1f
                }
            }
        })

        SizeImageData.values().forEachIndexed { index, data ->
            if (index == 0) selectedItems = data
            mBinding?.flexboxLayout?.addView(TextView(context).apply {
                text = getString(data.title)
                if (index != 0) {
                    setBackgroundResource(R.drawable.bg_border_select_box)
                } else {
                    setBackgroundResource(R.drawable.bg_border_select_box_select)
                    prevSelectView = this
                }
                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 20, 20)
                setTextColor(ContextCompat.getColor(context, R.color.color_white))
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    context.resources.getDimension(R.dimen._16sdp)
                )
                layoutParams = params

                setOnClickListener {
                    onItemSelected(it as TextView, data)
                }
            })
        }
    }

    private fun onItemSelected(view: TextView, item: SizeImageData) {
        if (item != selectedItems) {
            prevSelectView?.setBackgroundResource(R.drawable.bg_border_select_box)
            view.setBackgroundResource(R.drawable.bg_border_select_box_select)
            selectedItems = item
            prevSelectView = view
        }
    }

    private fun initAdapter() {
        adapter = StyleArtAdapter(arrayListOf(), object : ItemClickListener<StyleArtDto> {
            override fun onClick(item: StyleArtDto?, position: Int) {
                currentListStyleArt.forEachIndexed { index, it ->
                    it.isSelected = if (index == position) !it.isSelected else false
                }
                mBinding?.rcv?.post {
                    adapter?.updateData(currentListStyleArt)
                }
            }
        })
        mBinding?.rcv?.layoutManager = GridLayoutManager(activity, 3)
        mBinding?.rcv?.adapter = adapter
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initActions() {
        mBinding?.fmHomeArtLlGenerate?.setOnSingleClick {

            if (mBinding?.fmHomeArtEdtPrompt?.text?.isBlank() == true) {
                showDialogError(R.string.input_empty_error)
                return@setOnSingleClick
            }
            if (checkBannedWord(mBinding?.fmHomeArtEdtPrompt?.text.toString())) {
                val dialog = DialogFailArt.newInstance()
                dialog.show(activity?.supportFragmentManager)
                return@setOnSingleClick
            }
            handeGenerateArt()

        }

        mBinding?.fmHomeArtLLPolicy?.setOnSingleClick {
            UtilsApp.openBrowser(activity, Constants.URL_POLICY)
        }
        mBinding?.fmHomeArtTvInspired?.setOnSingleClick {
            UtilsApp.hideKeyboard(activity)
            val list = StyleArtHelper.getListInspired(context)
            mBinding?.fmHomeArtEdtPrompt?.setText(list.random())
        }
        mBinding?.fmHomeArtLlMore?.setOnSingleClick {
            pushScreen(MoreStyleArtFragment.newInstance(), MoreStyleArtFragment::class.java.name)
        }

        mBinding?.fmHomeArtEdtPrompt?.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }
    }

    private fun handeGenerateArt() {
        mViewModel?.callGetTimeStamp()
        val mStyleSelected = currentListStyleArt.findLast { it.isSelected }
        mBinding?.fmHomeArtEdtPrompt?.text?.toString()?.let { prompt ->
            pushScreen(
                ResultArtFragment.newInstance(
                    prompt,
                    mStyleSelected?.name ?: getStringRes(R.string.no_style),
                    mStyleSelected?.modelId ?: "midjourney",
                    width = selectedItems?.width ?: DEFAULT_SIZE,
                    height = selectedItems?.height ?: DEFAULT_SIZE
                ), ResultArtFragment::javaClass.name
            )
        }
    }

    override var initBackAction: Boolean = false

    override fun initData() {
        mViewModel?.getGenerateNumber()
        mViewModel?.getListStyleArt(context)
        mViewModel?.currentStyleArt?.observe(this) { list ->
            currentListStyleArt.clear()
            list.forEach {
                currentListStyleArt.add(it)
            }
            adapter?.updateData(list)
        }
        mViewModel?.getGenerateNumber()
        mViewModel?.mGenerateNumber?.observe(this) {
            numberArt = it
            mBinding?.fmHomeArtTvNumber?.visibility = View.GONE
            mBinding?.fmHomeArtTvNumber?.text =
                String.format(getString(R.string.left_s, it.toString()))


        }
    }

    private fun checkBannedWord(keyWord: String): Boolean {
        val listWord = keyWord.split(" ").map { it }
        val listBannedWord = resources.getStringArray(R.array.banner_word)
        listWord.forEach {
            listBannedWord.forEach { word ->
                if (word.lowercase(Locale.getDefault()) == it.lowercase(Locale.getDefault())) {
                    return true
                }
            }
        }
        return false
    }
}