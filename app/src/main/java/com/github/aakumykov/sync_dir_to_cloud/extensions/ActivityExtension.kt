package com.github.aakumykov.sync_dir_to_cloud.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

fun Activity.openAppProperties() {
    val uri = Uri.parse("package:$packageName")
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
    if (intent.resolveActivity(packageManager) != null) { startActivity(intent) }
}