package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class StorageWriter2_Creator @Inject constructor(
    private val map: Map<StorageType, @JvmSuppressWildcards StorageWriter2_Factory>
) {
    fun createStorageWriter(targetStorageType: StorageType?, targetAuthToken: String?): StorageWriter2?
        = map[targetStorageType]?.createStorageWriter2(targetStorageType, targetAuthToken)
}

