package com.github.aakumykov.sync_dir_to_cloud

import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.DummyCloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class CloudAuthenticatorFactory @Inject constructor() {

    fun createCloudAuthenticator(
        storageType: StorageType,
        fragment: Fragment,
        callbacks: CloudAuthenticator.Callbacks
    ): CloudAuthenticator {
        return when(storageType) {
            StorageType.LOCAL -> DummyCloudAuthenticator()
            StorageType.YANDEX_DISK -> YandexAuthenticator.create(fragment, callbacks)
        }
    }
}
