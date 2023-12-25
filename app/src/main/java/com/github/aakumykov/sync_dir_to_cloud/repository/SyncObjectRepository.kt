package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncObjectLocalDataSource
import javax.inject.Inject

// TODO: LocalDataSource --> DataSource
class SyncObjectRepository @Inject constructor(private val syncObjectLocalDataSource: SyncObjectLocalDataSource)
    : SyncObjectAdder, SyncObjectReader
{
    override suspend fun addSyncObject(syncObject: SyncObject) {
        syncObjectLocalDataSource.addSyncObject(syncObject)
    }

    override suspend fun getSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectLocalDataSource.getSyncObjectsForTask(taskId)
}