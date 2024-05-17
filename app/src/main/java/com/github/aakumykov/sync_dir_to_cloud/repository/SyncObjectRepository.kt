package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.BadObjectStateResettingDAO
import javax.inject.Inject

@AppScope
class SyncObjectRepository @Inject constructor(
    private val syncObjectDAO: SyncObjectDAO,
    private val badObjectStateResettingDAO: BadObjectStateResettingDAO
)
    : SyncObjectAdder, SyncObjectReader, SyncObjectStateChanger, SyncObjectStateResetter, SyncObjectDeleter,
        SyncObjectUpdater
{
    override suspend fun addSyncObject(syncObject: SyncObject)
        = syncObjectDAO.add(syncObject)


    override suspend fun getObjectsNeedsToBeSynched(taskId: String): List<SyncObject> {
//        syncObjectDAO.getSyncObjectsForTaskWithModificationStates(taskId, arrayOf(ModificationState.NEW, ModificationState.MODIFIED))

        val neverSyncedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithSyncState(taskId, ExecutionState.NEVER)
        val newObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, ModificationState.NEW)
        val modifiedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, ModificationState.MODIFIED)

        return (neverSyncedObjects + newObjects + modifiedObjects)
            .distinctBy { syncObject -> syncObject.id }
    }

    override suspend fun getObjectsForTask(
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject> {
        return syncObjectDAO.getSyncObjectsForTaskWithModificationStates(taskId, arrayOf(ModificationState.DELETED))
    }


    override suspend fun getObjectsForTask(taskId: String): List<SyncObject>
        = syncObjectDAO.getSyncObjectsForTask(taskId)


    override suspend fun changeExecutionState(objectId: String, syncState: ExecutionState, errorMsg: String)
        = syncObjectDAO.setExecutionState(objectId, syncState, errorMsg)


    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectList(taskId)


    override suspend fun setSyncDate(objectId: String, date: Long)
        = syncObjectDAO.setSyncDate(objectId, date)


    override suspend fun markAllObjectsAsDeleted(taskId: String)
        = syncObjectDAO.setStateOfAllItems(taskId, ModificationState.DELETED)


    override suspend fun getSyncObject(taskId: String, name: String): SyncObject?
        = syncObjectDAO.getSyncObject(taskId, name)


    override suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
        = syncObjectDAO.deleteObjectsWithModificationAndSyncState(taskId, ModificationState.DELETED, ExecutionState.SUCCESS)

    override suspend fun updateSyncObject(modifiedSyncObject: SyncObject)
        = syncObjectDAO.updateSyncObject(modifiedSyncObject)

    override suspend fun changeSourceModificationState(objectId: String, modificationState: ModificationState)
        = syncObjectDAO.changeSourceModificationState(objectId, modificationState)

    override suspend fun changeTargetModificationState(objectId: String, modificationState: ModificationState)
            = syncObjectDAO.changeTargetModificationState(objectId, modificationState)

    override suspend fun markBadStatesAsNeverSynced(taskId: String) {
        badObjectStateResettingDAO.markRunningStateAsNeverSynced(taskId)
        badObjectStateResettingDAO.markErrorStateAsNeverSynced(taskId)
    }
}