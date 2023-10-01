
package com.longkd.chatgpt_openai

import android.app.backup.BackupAgent
import android.app.backup.BackupAgentHelper
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.app.backup.SharedPreferencesBackupHelper
import android.os.ParcelFileDescriptor


class CustomBackupAgent : BackupAgentHelper() {
    companion object {
        private const val PREFERENCES_KEY = "chat_app_sf"
    }
    private lateinit var preferencesBackupHelper: SharedPreferencesBackupHelper

    override fun onCreate() {
        super.onCreate()
    }

}