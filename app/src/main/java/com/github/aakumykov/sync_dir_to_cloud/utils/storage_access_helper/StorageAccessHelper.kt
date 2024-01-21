package com.github.aakumykov.sync_dir_to_cloud.utils.storage_access_helper

interface StorageAccessHelper {
    fun requestStorageAccess(resultCallback: (isGranted: Boolean) -> Unit)
    fun hasStorageAccess(): Boolean
}