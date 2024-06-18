package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_auth

@Deprecated("Переименовать в StorageAuthenticator")
interface CloudAuthenticator {

    fun startAuth()

    interface Callbacks {
        fun onCloudAuthSuccess(authToken: String)
        fun onCloudAuthFailed(throwable: Throwable)
    }
}