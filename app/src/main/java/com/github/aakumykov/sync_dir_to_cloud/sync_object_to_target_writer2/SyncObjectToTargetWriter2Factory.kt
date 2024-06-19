package com.github.aakumykov.sync_dir_to_cloud.sync_object_to_target_writer2

import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriter2
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SyncObjectToTargetWriter2Factory {
    fun create(storageWriter2: StorageWriter2?): SyncObjectToTargetWriter2?
}