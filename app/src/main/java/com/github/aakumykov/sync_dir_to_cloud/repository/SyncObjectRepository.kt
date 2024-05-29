package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ModificationState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageHalf
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
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


    override suspend fun getObjectsNeedsToBeSynced(storageHalf: StorageHalf, taskId: String): List<SyncObject> {

        val neverSyncedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithSyncState(storageHalf, taskId, ExecutionState.NEVER)
        val newObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(storageHalf, taskId, ModificationState.NEW)
        val modifiedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(storageHalf, taskId, ModificationState.MODIFIED)

        return (neverSyncedObjects + newObjects + modifiedObjects)
            .distinctBy { syncObject -> syncObject.id }
    }

    override suspend fun getObjectsForTask(
        storageHalf: StorageHalf,
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsWithModificationState(storageHalf, taskId, modificationState)
    }

    override suspend fun getObjectsForTask(
        storageHalf: StorageHalf,
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsWithSyncState(storageHalf, taskId, syncState)
    }

    override suspend fun getInTargetMissingObjects(taskId: String): List<SyncObject> {
        return syncObjectDAO.getObjectsNotDeletedInSourceButDeletedInTarget(taskId)
    }

    override suspend fun getList(
        taskId: String,
        storageHalf: StorageHalf,
        readingStrategy: ReadingStrategy
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsForTask(storageHalf, taskId).filter {
            readingStrategy.isAcceptedForSync(it)
        }
    }


    override suspend fun changeSyncState(
        objectId: String,
        syncState: ExecutionState,
        errorMsg: String
    )
        = syncObjectDAO.setSyncState(objectId, syncState, errorMsg)


    override suspend fun getSyncObjectListAsLiveData(storageHalf: StorageHalf, taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectListAsLiveData(storageHalf, taskId)


    override suspend fun setSyncDate(objectId: String, date: Long)
        = syncObjectDAO.setSyncDate(objectId, date)


    override suspend fun markAllObjectsAsDeleted(storageHalf: StorageHalf, taskId: String)
        = syncObjectDAO.setStateOfAllItems(storageHalf, taskId, ModificationState.DELETED)


    override suspend fun getSyncObject(storageHalf: StorageHalf, taskId: String, name: String): SyncObject?
        = syncObjectDAO.getSyncObject(storageHalf, taskId, name)


    override suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
        = syncObjectDAO.deleteObjectsWithModificationAndSyncState(taskId, ModificationState.DELETED, ExecutionState.SUCCESS)


    override suspend fun updateSyncObject(modifiedSyncObject: SyncObject)
        = syncObjectDAO.updateSyncObject(modifiedSyncObject)


    override suspend fun changeModificationState(
        syncObject: SyncObject,
        storageHalf: StorageHalf,
        modificationState: ModificationState
    ) {
        syncObjectDAO.changeModificationState(
            storageHalf = storageHalf,
            name = syncObject.name,
            relativeParentDirPath = syncObject.relativeParentDirPath,
            taskId = syncObject.taskId,
            modificationState = modificationState
        )
    }

    override suspend fun setErrorState(
        syncObject: SyncObject,
        storageHalf: StorageHalf,
        throwable: Throwable?
    ) {
        syncObjectDAO.setErrorState(
            storageHalf = storageHalf,
            name = syncObject.name,
            relativeParentDirPath = syncObject.relativeParentDirPath,
            taskId = syncObject.taskId,
            errorMessage = ExceptionUtils.getErrorMessage(throwable)
        )
    }


    override suspend fun markBadStatesAsNeverSynced(taskId: String) {
        badObjectStateResettingDAO.markRunningStateAsNeverSynced(taskId)
        badObjectStateResettingDAO.markErrorStateAsNeverSynced(taskId)
    }
}