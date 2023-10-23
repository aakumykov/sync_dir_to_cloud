package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

interface CloudAuthenticator {

    fun startAuth()

    interface Callbacks {
        fun onCloudAuthSuccess(authToken: String)
        fun onCloudAuthFailed(throwable: Throwable)
    }
}