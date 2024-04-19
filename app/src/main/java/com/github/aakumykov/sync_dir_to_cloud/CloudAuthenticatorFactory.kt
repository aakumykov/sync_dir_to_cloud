package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.DummyCloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.invisible_auth_fragment.YandexInvisibleAuthFragment
import javax.inject.Inject

class CloudAuthenticatorFactory @Inject constructor() {

    fun createCloudAuthenticator(storageType: StorageType)
    : CloudAuthenticator
    {
        return when(storageType) {
            StorageType.LOCAL -> DummyCloudAuthenticator()
            StorageType.YANDEX_DISK -> YandexInvisibleAuthFragment.create()
        }
    }
}
