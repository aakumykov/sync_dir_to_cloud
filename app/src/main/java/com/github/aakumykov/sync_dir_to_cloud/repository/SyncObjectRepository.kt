package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncObjectLocalDataSource
import javax.inject.Inject

// TODO: LocalDataSource --> DataSource
// TODO: передавать диспетчер сопрограммы
// TODO: избавиться от LocalDataSource
@AppScope
class SyncObjectRepository @Inject constructor(private val syncObjectLocalDataSource: SyncObjectLocalDataSource)
    : SyncObjectAdder, SyncObjectReader, SyncObjectStateChanger, SyncObjectStateResetter
{
    override suspend fun addSyncObject(syncObject: SyncObject) {
        syncObjectLocalDataSource.addSyncObject(syncObject)
    }

    override suspend fun getSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectLocalDataSource.getSyncObjectsForTask(taskId)

    override suspend fun getNewAndChangedSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectLocalDataSource.getNewAndChangedSyncObjectsForTask(taskId)

    override suspend fun changeExecutionState(syncObjectId: String, state: ExecutionState, errorMsg: String) {
        syncObjectLocalDataSource.setState(syncObjectId, state, errorMsg)
    }

    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectLocalDataSource.getSyncObjectList(taskId)

    override suspend fun setSyncDate(id: String, date: Long)
        = syncObjectLocalDataSource.setSyncDate(id, date)

    override suspend fun resetSyncObjectsStateOfTask(taskId: String) {
//        syncObjectLocalDataSource.deleteSyncObjectOfTask(taskId)
        syncObjectLocalDataSource.markAllItemsAsDeleted(taskId)
    }

    override suspend fun getSyncObject(name: String, relativeParentDirPath: String): SyncObject? {
        return syncObjectLocalDataSource.getSyncObject(name, relativeParentDirPath)
    }
}