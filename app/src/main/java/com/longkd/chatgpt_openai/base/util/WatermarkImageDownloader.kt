package com.longkd.chatgpt_openai.base.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.longkd.chatgpt_openai.R
import java.io.File
import java.io.FileOutputStream


class WatermarkImageDownloader(private val context: Context) {

    fun downloadImageWithWatermark(
        imageUrl: String,
        action: CommonAction,
        isWaterMark: Boolean = true
    ) {
        if (!URLUtil.isValidUrl(imageUrl)) {
            return
        }

        // Load the original image using Glide
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .apply(requestOptions)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    var outputBitmap: Bitmap? = null
                    if (isWaterMark) {
                        val watermarkBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.water_mark)
                        val watermarkWidth = resource.width / 4
                        val watermarkHeight =
                            watermarkWidth * watermarkBitmap.height / watermarkBitmap.width
                        val watermarkResized = Bitmap.createScaledBitmap(
                            watermarkBitmap,
                            watermarkWidth,
                            watermarkHeight,
                            false
                        )

                        // Add watermark to the image
                        outputBitmap =
                            Bitmap.createBitmap(
                                resource.width,
                                resource.height,
                                Bitmap.Config.ARGB_8888
                            )
                        val canvas = Canvas(outputBitmap)
                        canvas.drawBitmap(resource, 0f, 0f, null)
                        canvas.drawBitmap(
                            watermarkResized,
                            10f,
                            resource.height.toFloat() - watermarkHeight,
                            null
                        )

                    } else {
                        outputBitmap =
                            Bitmap.createBitmap(
                                resource.width,
                                resource.height,
                                Bitmap.Config.ARGB_8888
                            )
                        val canvas = Canvas(outputBitmap)
                        canvas.drawBitmap(resource, 0f, 0f, null)
                    }
                    kotlin.runCatching {
                        val outputDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val outputFileName = "Ai_Art_${System.currentTimeMillis()}.jpg"
                        val outputFile = File(outputDir, outputFileName)

                        val outputStream = FileOutputStream(outputFile)
                        outputBitmap?.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(outputFile.absolutePath),
                            null,
                            null
                        )
                        action.action?.invoke()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
    }

    fun shareImageWithWatermark(imageUrl: String, isWaterMark: Boolean = true) {
        if (!URLUtil.isValidUrl(imageUrl)) {
            return
        }
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .apply(requestOptions)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    var outputBitmap: Bitmap? = null
                    if (isWaterMark) {
                        val watermarkBitmap =
                            BitmapFactory.decodeResource(context.resources, R.drawable.water_mark)
                        val watermarkWidth = resource.width / 4
                        val watermarkHeight =
                            watermarkWidth * watermarkBitmap.height / watermarkBitmap.width
                        val watermarkResized = Bitmap.createScaledBitmap(
                            watermarkBitmap,
                            watermarkWidth,
                            watermarkHeight,
                            false
                        )

                        // Add watermark to the image
                        outputBitmap =
                            Bitmap.createBitmap(
                                resource.width,
                                resource.height,
                                Bitmap.Config.ARGB_8888
                            )
                        val canvas = Canvas(outputBitmap)
                        canvas.drawBitmap(resource, 0f, 0f, null)
                        canvas.drawBitmap(
                            watermarkResized,
                            10f,
                            resource.height.toFloat() - watermarkHeight,
                            null
                        )

                    } else {
                        outputBitmap =
                            Bitmap.createBitmap(
                                resource.width,
                                resource.height,
                                Bitmap.Config.ARGB_8888
                            )
                        val canvas = Canvas(outputBitmap)
                        canvas.drawBitmap(resource, 0f, 0f, null)
                    }
                    val outputDir =
                        File(context.cacheDir, "images")
                    val outputFileName = "Ai_Art_${System.currentTimeMillis()}.jpg"
                    val outputFile = File(outputDir, outputFileName)
                    kotlin.runCatching {
                        outputFile.getParentFile().mkdirs()
                        val outputStream = FileOutputStream(outputFile)
                        outputBitmap?.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        context.packageName?.let {
                            val uri = FileProvider.getUriForFile(context, it, outputFile)
                            shareImage(context, uri)
                        }
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
    }

    private fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.sharing_image))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        intent.type = "image/png"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.sharing_image)
            )
        )
    }


}
