package com.github.aakumykov.sync_dir_to_cloud.di.factories

import androidx.fragment.app.Fragment
import com.github.aakumykov.sync_dir_to_cloud.CloudAuthenticatorFactory
import com.github.aakumykov.sync_dir_to_cloud.cloud_auth.CloudAuthenticator
import dagger.assisted.AssistedFactory

@AssistedFactory
interface CloudAuthenticatorFactoryAssistedFactory {
    fun createCloudAuthenticatorFactory(
        fragment: Fragment,
        cloudAuthenticatorCallbacks: CloudAuthenticator.Callbacks
    ): CloudAuthenticatorFactory
}