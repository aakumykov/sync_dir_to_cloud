package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.os.Bundle

interface CloudAuthProcessor {
    fun startCloudAuth()
    fun processCloudAuthResult(data: Bundle?)
}