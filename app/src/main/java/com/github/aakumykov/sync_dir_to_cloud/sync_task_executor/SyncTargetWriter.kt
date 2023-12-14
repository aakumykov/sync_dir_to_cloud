package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.StorageType
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncTargetWriter @AssistedInject constructor(
    @Assisted private val targetStorageType: StorageType,
    @Assisted private val targetAuthToken: String
){
    fun writeToTarget(syncTask: SyncTask) {
        TODO("Not yet implemented")
    }


    @AssistedFactory
    interface Factory {
        fun create(targetStorageType: StorageType, targetAuthToken: String): SyncTargetWriter
    }
}
