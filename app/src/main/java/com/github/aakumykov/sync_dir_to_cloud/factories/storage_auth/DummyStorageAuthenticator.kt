package com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth

class DummyStorageAuthenticator(private val callbacks: StorageAuthenticator.Callbacks) :
    StorageAuthenticator {
    override fun startAuth() {
        callbacks.onCloudAuthSuccess("")
    }
}