package com.github.aakumykov.sync_dir_to_cloud.utils.storage_access_helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class ManageAllFilesContract(private val packageName: String) : ActivityResultContract<Unit, Boolean>() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${packageName}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return Environment.isExternalStorageManager()
    }
}