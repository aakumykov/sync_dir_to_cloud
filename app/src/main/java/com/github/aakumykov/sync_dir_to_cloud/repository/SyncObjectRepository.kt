package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectChanges
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import javax.inject.Inject

@AppScope
class SyncObjectRepository @Inject constructor(
    private val syncObjectDAO: SyncObjectDAO
)
    : SyncObjectAdder, SyncObjectReader, SyncObjectStateChanger, SyncObjectStateResetter, SyncObjectDeleter,
    SyncObjectUpdater
{
    override suspend fun addSyncObject(syncObject: SyncObject)
        = syncObjectDAO.add(syncObject)


    override suspend fun getNewAndChangedSyncObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectDAO.getSyncObjectsForTaskWithModificationStates(taskId,
        arrayOf(ModificationState.NEW, ModificationState.MODIFIED))

    override suspend fun getObjectsForTask(
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject> {
        return syncObjectDAO.getSyncObjectsForTaskWithModificationStates(taskId, arrayOf(ModificationState.DELETED))
    }


    override suspend fun changeExecutionState(syncObjectId: String, syncState: SyncState, errorMsg: String)
        = syncObjectDAO.setExecutionState(syncObjectId, syncState, errorMsg)


    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectList(taskId)


    override suspend fun setSyncDate(id: String, date: Long)
        = syncObjectDAO.setSyncDate(id, date)


    override suspend fun markAllObjectsAsDeleted(taskId: String)
        = syncObjectDAO.setStateOfAllItems(taskId, ModificationState.DELETED)


    override suspend fun getSyncObject(name: String, relativeParentDirPath: String, taskId: String): SyncObject?
        = syncObjectDAO.getSyncObject(name, relativeParentDirPath, taskId)


    override suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
        = syncObjectDAO.deleteObjectsWithModificationAndSyncState(taskId, ModificationState.DELETED, SyncState.SUCCESS)


    override suspend fun updateSyncObject(syncObjectChanges: SyncObjectChanges)
        = syncObjectDAO.updateSyncObject(syncObjectChanges)
}