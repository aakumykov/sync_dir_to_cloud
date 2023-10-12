package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.content.Intent

interface CloudAuthenticator {

    fun startCloudAuth()
    fun processCloudAuthResult(requestCode: Int, resultCode: Int, data: Intent?)

    interface Callbacks {
        fun onCloudAuthSuccess(authToken: String)
        fun onCloudAuthFailed(errorMsg: String)
    }
}