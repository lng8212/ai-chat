package com.longkd.chatgpt_openai.base.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun checkPermissionCamera(context: Context?): Boolean {
        context?.let {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
        } ?: kotlin.run { return false }
    }

    fun checkPermissionReadExternalStorage(
        context: Context
    ): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            false
        }
    }

    fun checkPermissionReadExternalStorageTargetSDK33(
        context: Context
    ): Boolean {
        return if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            false
        }
    }
}