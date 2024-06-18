package com.github.aakumykov.sync_dir_to_cloud.factories.cloud_auth

class DummyCloudAuthenticator(private val callbacks: CloudAuthenticator.Callbacks) :
    CloudAuthenticator {
    override fun startAuth() {
        callbacks.onCloudAuthSuccess("")
    }
}