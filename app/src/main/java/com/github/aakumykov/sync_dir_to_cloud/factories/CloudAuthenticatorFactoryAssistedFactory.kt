package com.github.aakumykov.sync_dir_to_cloud.factories

import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticatorFactory
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import dagger.assisted.AssistedFactory

// FIXME: нужно ли отделять фабрики от производимых ими классов, если Dagger2 накрепко
//  интегрирован в систему?

@AssistedFactory
interface CloudAuthenticatorFactoryAssistedFactory {
    fun createCloudAuthenticatorFactory(
        fragment: Fragment,
        cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks
    ): CloudAuthenticatorFactory
}