package com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth

interface StorageAuthenticator {

    fun startAuth()

    interface Callbacks {
        fun onCloudAuthSuccess(authToken: String)
        fun onCloudAuthFailed(throwable: Throwable)
    }
}