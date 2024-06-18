package com.github.aakumykov.sync_dir_to_cloud.factories.storage_auth

import androidx.fragment.app.Fragment
import dagger.assisted.AssistedFactory

// FIXME: нужно ли отделять фабрики от производимых ими классов, если Dagger2 накрепко
//  интегрирован в систему?

@AssistedFactory
interface CloudAuthenticatorFactoryAssistedFactory {
    fun createCloudAuthenticatorFactory(
        fragment: Fragment,
        storageAuthenticatorCallbacks: StorageAuthenticator.Callbacks
    ): StorageAuthenticatorFactory
}