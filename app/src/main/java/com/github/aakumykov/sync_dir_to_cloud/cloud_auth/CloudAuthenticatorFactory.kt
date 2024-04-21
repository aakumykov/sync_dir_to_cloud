package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.DummyCloudAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.YandexAuthenticator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class CloudAuthenticatorFactory @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted private val cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks
) {
    fun createCloudAuthenticator(storageType: StorageType)
    : CloudAuthenticator
    {
        return when(storageType) {
            StorageType.YANDEX_DISK -> YandexAuthenticator.create(fragment,cloudAuthenticatorCallbacks)
            else -> DummyCloudAuthenticator()
        }
    }
}
