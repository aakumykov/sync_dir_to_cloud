package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

interface StorageWriter2_Factory {
    fun createStorageWriter2(targetStorageType: StorageType, targetAuthToken: String): StorageWriter2
}
