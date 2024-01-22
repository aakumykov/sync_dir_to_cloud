package com.github.aakumykov.storage_access_helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

class IntentHelper private constructor() {

    companion object {

        @RequiresApi(Build.VERSION_CODES.R)
        fun manageAllFilesIntent(context: Context): Intent {
            return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        }

        fun appSettingsIntent(context: Context): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                resolveActivity(context.packageManager)?.also { context.startActivity(this) }
            }
        }
    }
}