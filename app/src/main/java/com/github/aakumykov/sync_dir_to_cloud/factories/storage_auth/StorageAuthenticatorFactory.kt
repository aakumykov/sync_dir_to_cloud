package com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth

import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class StorageAuthenticatorFactory @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    @Assisted private val storageAuthenticatorCallbacks: StorageAuthenticator.Callbacks
) {
    fun createCloudAuthenticator(storageType: StorageType)
    : StorageAuthenticator
    {
        return when(storageType) {
            StorageType.YANDEX_DISK -> YandexAuthenticator.create(fragment, storageAuthenticatorCallbacks)
            StorageType.LOCAL -> LocalAuthenticator(fragment, storageAuthenticatorCallbacks)
            else -> DummyStorageAuthenticator(storageAuthenticatorCallbacks)
        }
    }
}
