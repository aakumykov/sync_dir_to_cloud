package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncSourceReader @AssistedInject constructor(
    @Assisted private val sourceStorageType: StorageType,
    @Assisted private val sourceAuthToken: String
) {
    fun readSource(syncTask: SyncTask) {
        TODO("Not yet implemented")
    }


    @AssistedFactory
    interface Factory {
        fun create(sourceStorageType: StorageType, sourceAuthToken: String): SyncSourceReader
    }
}
