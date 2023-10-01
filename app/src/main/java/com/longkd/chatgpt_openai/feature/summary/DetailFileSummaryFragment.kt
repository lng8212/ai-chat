package com.longkd.chatgpt_openai.feature.summary

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkd.chatgpt_openai.R
import com.longkd.chatgpt_openai.base.BaseFragment
import com.longkd.chatgpt_openai.base.model.SummaryData
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import com.longkd.chatgpt_openai.base.util.setOnSingleClick
import com.longkd.chatgpt_openai.databinding.FragmentDetailFileSummaryBinding
import java.io.File

class DetailFileSummaryFragment: BaseFragment<FragmentDetailFileSummaryBinding>(R.layout.fragment_detail_file_summary) {
    private lateinit var adapter: SummaryFileAdapter
    override fun initViews() {
        val mArrSummaryFile = arrayListOf<SummaryData>()
        mBinding?.detailFmNameFile?.text = arguments?.getString(KEY_TITLE_SUMMARY_FILE).toString()
       arguments?.getParcelable<SummaryHistoryDto>(KEY_FROM_SUMMARY_FILE)?.let {
            it.filePaths.forEach {
                mArrSummaryFile.add(SummaryData(uri = it))
            }
           if (mArrSummaryFile.size == 1 && it.fileName?.contains("pdf") == true) {
               mArrSummaryFile.firstOrNull()?.uri?.let {
                   val listBitmap = mutableListOf<Bitmap>()
                   val pdfRenderer = PdfRenderer(
                       ParcelFileDescriptor.open(
                           File(it),
                           ParcelFileDescriptor.MODE_READ_ONLY
                       )
                   )
                   for (i in 0 until  pdfRenderer.pageCount) {
                       val page = pdfRenderer.openPage(i)
                       val bitmap = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888)
                       val canvas = Canvas(bitmap)
                       canvas.drawColor(Color.WHITE)
                       page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                       listBitmap.add(bitmap)
                       page.close()
                   }
                   adapter = SummaryFileAdapter(listBitmap.toMutableList())
               }
           } else if (mArrSummaryFile.size > 0) {
               adapter = SummaryFileAdapter(mArrSummaryFile.toMutableList())
           } else {
               adapter = SummaryFileAdapter(mutableListOf(it))
           }
       }
        if (this::adapter.isInitialized) {
            mBinding?.detailFmRcl?.adapter = adapter
            mBinding?.detailFmRcl?.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun initActions() {
        mBinding?.detailFmCloseFile?.setOnSingleClick {
            handleOnBackPress()
        }
    }

    override var initBackAction: Boolean = true

    override fun handleOnBackPress(): Boolean {

            activity?.onBackPressed()

        return true
    }

    override fun initData() {

    }

    companion object {
        private const val KEY_FROM_SUMMARY_FILE = "key_from_summary_file"
        private const val KEY_TITLE_SUMMARY_FILE = "key_titlesummary_file"
        fun newInstance(summaryData: SummaryHistoryDto? = null, title: String): DetailFileSummaryFragment{
            val args = Bundle()
            summaryData?.let { args.putParcelable(KEY_FROM_SUMMARY_FILE, it) }
            args.putString(KEY_TITLE_SUMMARY_FILE, title)
            val fragment = DetailFileSummaryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}