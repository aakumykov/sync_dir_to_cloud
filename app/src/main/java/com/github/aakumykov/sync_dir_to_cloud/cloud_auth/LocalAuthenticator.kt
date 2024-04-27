package com.github.aakumykov.sync_dir_to_cloud.cloud_auth

import android.util.Log
import androidx.fragment.app.Fragment
import com.github.aakumykov.storage_access_helper.StorageAccessHelper

// TODO: BasicAuthenticator, содержащий коллбеки
class LocalAuthenticator(
    fragment: Fragment,
    private val callbacks: CloudAuthenticator.Callbacks
) : CloudAuthenticator {

    // TODO: внедрять StorageAccessHelper...
    private val storageAccessHelper: StorageAccessHelper = StorageAccessHelper.create(fragment)

    init {
        storageAccessHelper.prepareForReadAccess()
    }

    override fun startAuth() {
        storageAccessHelper.requestReadAccess { isGranted ->
            if (isGranted)
                callbacks.onCloudAuthSuccess(DUMMY_AUTH_TOKEN)
            else
                callbacks.onCloudAuthFailed(Exception("Storage access denied."))
        }
    }

    companion object {
        val TAG: String = LocalAuthenticator::class.java.simpleName
        const val DUMMY_AUTH_TOKEN = "DUMMY_AUTH_TOKEN"
    }
}