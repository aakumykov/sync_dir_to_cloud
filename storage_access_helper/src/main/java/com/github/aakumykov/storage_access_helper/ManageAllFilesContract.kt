package com.github.aakumykov.storage_access_helper

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi

class ManageAllFilesContract(private val packageName: String) : ActivityResultContract<Unit, Boolean>() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun createIntent(context: Context, input: Unit): Intent {
        return IntentHelper.manageAllFilesIntent(context)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return Environment.isExternalStorageManager()
    }
}