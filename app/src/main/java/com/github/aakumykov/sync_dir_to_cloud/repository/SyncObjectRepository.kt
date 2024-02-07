package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import javax.inject.Inject

@AppScope
class SyncObjectRepository @Inject constructor(
    private val syncObjectDAO: SyncObjectDAO
)
    : SyncObjectAdder, SyncObjectReader, SyncObjectStateChanger, SyncObjectStateResetter
{
    override suspend fun addSyncObject(syncObject: SyncObject) {
        syncObjectDAO.add(syncObject)
    }

    override suspend fun getNewAndChangedSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectDAO.getSyncObjectsForTaskWithModificationState(taskId, arrayOf(ModificationState.NEW, ModificationState.MODIFIED))

    // TODO: state --> executionState
    override suspend fun changeExecutionState(syncObjectId: String, state: ExecutionState, errorMsg: String) {
        syncObjectDAO.setExecutionState(syncObjectId, state, errorMsg)
    }

    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectList(taskId)

    override suspend fun setSyncDate(id: String, date: Long)
        = syncObjectDAO.setSyncDate(id, date)

    override suspend fun resetSyncObjectsStateOfTask(taskId: String) {
        syncObjectDAO.setStateOfAllItems(taskId, ModificationState.DELETED)
    }

    override suspend fun getSyncObject(name: String, relativeParentDirPath: String): SyncObject? {
        return syncObjectDAO.getSyncObject(name, relativeParentDirPath)
    }
}