package com.longkd.chatgpt_openai.feature.summary

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.longkd.chatgpt_openai.base.model.SummaryHistoryDto
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.Normalizer
import java.util.*

object FileSummaryUtils {
    @SuppressLint("Range")
    fun getSummaryFile(
        context: Context?,
        uri: Uri?,
        listMd5: List<SummaryHistoryDto>?,
        maxSize: Long
    ): Pair<String, SummaryHistoryDto?> {
        if (uri == null) return Pair(StatusSummary.ERROR.name, null)
        val encryptedMD5 = getMD5EncryptedString(context, uri)
        listMd5?.forEach {
            if (it.md5 == encryptedMD5) {
                return Pair(StatusSummary.DUPLICATE.name, it)
            }
        }

        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE
        )
        val cursor = context?.contentResolver?.query(uri, projection, "${MediaStore.MediaColumns.DISPLAY_NAME}?=", null, null)
        cursor?.let {
            try {
                it.moveToFirst()
                val idName = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val name = it.getString(idName).split('.').first().reFormatCode()
                val extension = ".${it.getString(idName).split('.').last()}"
                val file = File.createTempFile(
                    name,
                    extension,
                    context.getExternalFilesDir(getMyDir(context))
                )
                copyfile(context, uri, file)
                file
                cursor.close()
                if (getFolderSize(file) > maxSize) {
                    return Pair(
                        StatusSummary.OUT_SIZE.name, SummaryHistoryDto(
                            encryptedMD5,
                            name + extension,
                            listOf(file.absolutePath),
                            listOf(),
                            listOf(),
                            System.currentTimeMillis()
                        )
                    )
                } else {
                    return Pair(
                        StatusSummary.CREATE.name, SummaryHistoryDto(
                            encryptedMD5,
                            name + extension,
                            listOf(file.absolutePath),
                            listOf(),
                            listOf(),
                            System.currentTimeMillis()
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("FILE ERROR", e.message ?: "")
            }
        }
        return Pair(StatusSummary.ERROR.name, null)
    }

    private fun String.reFormatCode(): String {
        val normalizer = Normalizer.normalize(this.replace(" ", "_"), Normalizer.Form.NFD)
        val REX = "\\p{InCombiningDiacriticalMarks}".toRegex()
        return REX.replace(normalizer, "").toLowerCase(Locale.getDefault()).replace('đ','d')
    }

    private fun getMyDir(context: Context): String {
        val packageManager = context.packageManager
        val stringName = context.packageName
        return packageManager.getPackageInfo(stringName, 0).applicationInfo.dataDir
    }

    private fun getMD5EncryptedString(context: Context?, uri: Uri): String {
        context?.contentResolver?.openInputStream(uri)?.use { input ->
            var digest: MessageDigest = try {
                MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                Log.e("File utils", "Exception while getting digest", e)
                return ""
            }
            val buffer = ByteArray(8192)
            var read: Int
            return try {
                while (input.read(buffer).also { read = it } > 0) {
                    digest.update(buffer, 0, read)
                }
                val md5sum = digest.digest()
                val bigInt = BigInteger(1, md5sum)
                var output: String = bigInt.toString(16)
                output = String.format("%32s", output).replace(' ', '0')
                output
            } catch (e: IOException) {
                throw RuntimeException("Unable to process file for MD5", e)
            } finally {
                input.close()
            }
        }
        return ""
    }

    private fun copyfile(context: Context?, uri: Uri, file: File) {
        context?.contentResolver?.openInputStream(uri)?.use { input ->
            input.use { input ->
                val output = FileOutputStream(file)
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
    }

    private fun getFolderSize(file: File): Long {
        var size: Long = 0L
        if (file.isDirectory) {
            for (child in file.listFiles()) {
                size += getFolderSize(child)
            }
        } else {
            size = file.length()
        }
        return size
    }
}