package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectClearer
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.repository.data_sources.SyncObjectLocalDataSource
import javax.inject.Inject

// TODO: LocalDataSource --> DataSource
// TODO: передавать диспетчер сопрограммы

@AppScope
class SyncObjectRepository @Inject constructor(private val syncObjectLocalDataSource: SyncObjectLocalDataSource)
    : SyncObjectAdder, SyncObjectReader, SyncObjectStateChanger, SyncObjectClearer
{
    override suspend fun addSyncObject(syncObject: SyncObject) {
        syncObjectLocalDataSource.addSyncObject(syncObject)
    }

    override suspend fun getSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectLocalDataSource.getSyncObjectsForTask(taskId)

    override suspend fun changeState(syncObjectId: String, state: SyncObject.State)
        = syncObjectLocalDataSource.setState(syncObjectId, state)

    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectLocalDataSource.getSyncObjectList(taskId)

    // FIXME: выполнять как транзакцию
    override suspend fun setErrorState(syncObjectId: String, errorMsg: String) {
        syncObjectLocalDataSource.setState(syncObjectId, SyncObject.State.ERROR)
        syncObjectLocalDataSource.setErrorMsg(syncObjectId, errorMsg)
    }

    override suspend fun clearSyncObjectsOfTask(taskId: String) {
        syncObjectLocalDataSource.deleteSyncObjectOfTask(taskId)
    }
}