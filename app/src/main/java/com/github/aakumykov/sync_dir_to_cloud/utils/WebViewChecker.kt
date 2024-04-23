package com.github.aakumykov.sync_dir_to_cloud.utils

import android.content.Context

class WebViewChecker {
    companion object {
        fun isAvailable(context: Context): Boolean
            = context.packageManager.hasSystemFeature("android.software.webview")
    }
}
