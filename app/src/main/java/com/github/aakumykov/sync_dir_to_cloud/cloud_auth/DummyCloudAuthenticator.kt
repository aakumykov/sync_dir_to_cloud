package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

class DummyCloudAuthenticator : CloudAuthenticator {
    override fun startAuth() {
        throw RuntimeException("Не используется")
    }
}