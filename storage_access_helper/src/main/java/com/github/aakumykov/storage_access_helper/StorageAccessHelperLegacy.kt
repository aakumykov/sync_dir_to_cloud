package com.github.aakumykov.storage_access_helper

import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class StorageAccessHelperLegacy(private val activity: FragmentActivity): StorageAccessHelper {

    private val readingStoragePermissionsRequester: PermissionsRequester
    private var resultCallback: ((isGranted: Boolean) -> Unit)? = null // TODO: в интерфейс...

    init {
        readingStoragePermissionsRequester = activity.constructPermissionsRequest(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            requiresPermission = { resultCallback?.invoke(true) },
            onPermissionDenied = { resultCallback?.invoke(false) },
            onNeverAskAgain = { resultCallback?.invoke(true) }
        )
    }

    override fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit) {
        this.resultCallback = resultCallback
        readingStoragePermissionsRequester.launch()
    }

    override fun hasStorageAccess(): Boolean {
        return PackageManager.PERMISSION_GRANTED == activity.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun openStorageAccessSettings() {
        IntentHelper.appSettingsIntent(activity)
    }
}