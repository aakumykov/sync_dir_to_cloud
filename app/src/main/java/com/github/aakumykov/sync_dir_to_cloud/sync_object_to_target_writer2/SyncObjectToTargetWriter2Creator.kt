package com.github.aakumykov.sync_dir_to_cloud.sync_object_to_target_writer2

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.storage_writer2.StorageWriter2_Creator
import javax.inject.Inject

// FIXME: не нравится мне то, что нужно часто передавать cloudAuthReader
class SyncObjectToTargetWriter2Creator @Inject constructor(
    private val cloudAuthReader: CloudAuthReader,
    private val storageWriter2Creator: StorageWriter2_Creator,
    private val syncObjectToTargetWriterFactory: SyncObjectToTargetWriter2Factory
){
    suspend fun create(syncTask: SyncTask): SyncObjectToTargetWriter2? {
        return syncObjectToTargetWriterFactory.create(
            storageWriter2Creator.createStorageWriter(
                syncTask.targetStorageType,
                cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken
            )
        )
    }
}