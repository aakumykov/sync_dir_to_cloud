package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import javax.inject.Inject

class StorageWriter2_Creator @Inject constructor(
    private val map: Map<StorageType, StorageWriter2>
) {
    fun createStorageWriter(targetStorageType: StorageType?, targetAuthId: String?): StorageWriter2? = map[targetStorageType]
}

