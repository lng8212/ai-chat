package com.longkd.chatgpt_openai.base.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.longkd.chatgpt_openai.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class WatermarkImageDownloader {
    fun load(context: Context, string: String): Bitmap? {
        val url = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection?
            val inputStream: InputStream? = connection?.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    fun saveMediaToStorage(context: Context, bitmap: Bitmap?, onSuccess: () -> Unit) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            onSuccess.invoke()

        }

    }

    fun shareImage(imageUrl: String, context: Context) {
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

                    outputBitmap =
                        Bitmap.createBitmap(
                            resource.width,
                            resource.height,
                            Bitmap.Config.ARGB_8888
                        )
                    val canvas = Canvas(outputBitmap)
                    canvas.drawBitmap(resource, 0f, 0f, null)
                    val outputDir =
                        File(context.cacheDir, "images")
                    val outputFileName = "Ai_Art_${System.currentTimeMillis()}.jpg"
                    val outputFile = File(outputDir, outputFileName)
                    kotlin.runCatching {
                        outputFile.parentFile?.mkdirs()
                        val outputStream = FileOutputStream(outputFile)
                        outputBitmap?.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        context.packageName?.let {
                            val uri = FileProvider.getUriForFile(context, it, outputFile)
                            shareImage(context, uri)
                        }
                    }.onFailure {
                        it.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
    }


    fun shareImage(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.sharing_image))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        intent.type = "image/png"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.sharing_image)
            )
        )
    }


}
