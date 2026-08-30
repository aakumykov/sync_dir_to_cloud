package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi

object StorageAccessPermissionChecker {

    fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasManageExternalStoragePermission(context)
        } else {
            hasReadExternalStoragePermission(context) && hasWriteExternalStoragePermission(context)
        }
    }

    fun hasNoPermission(context: Context): Boolean = !hasPermission(context)

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasManageExternalStoragePermission(context: Context): Boolean {
        return PERMISSION_GRANTED == context.checkSelfPermission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    }

    private fun hasReadExternalStoragePermission(context: Context): Boolean {
        return PERMISSION_GRANTED == context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun hasWriteExternalStoragePermission(context: Context): Boolean {
        return PERMISSION_GRANTED == context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}