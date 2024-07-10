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
import com.github.aakumykov.sync_dir_to_cloud.repository.room.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy
import javax.inject.Inject

@AppScope
class SyncObjectRepository @Inject constructor(
    private val syncObjectDAO: SyncObjectDAO,
    private val syncObjectStateDAO: SyncObjectStateDAO,
    private val syncObjectBadStateResettingDAO: SyncObjectBadStateResettingDAO,
    private val badObjectStateResettingDAO: BadObjectStateResettingDAO,
)
    : SyncObjectAdder,
        SyncObjectReader,
        SyncObjectStateChanger,
        SyncObjectStateResetter,
        SyncObjectDeleter,
        SyncObjectUpdater
{
    override suspend fun addSyncObject(syncObject: SyncObject)
        = syncObjectDAO.add(syncObject)


    override suspend fun getObjectsNeedsToBeSynced(taskId: String): List<SyncObject> {

        val neverSyncedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithSyncState(taskId, ExecutionState.NEVER)
        val newObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, ModificationState.NEW)
        val modifiedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, ModificationState.MODIFIED)

        return (neverSyncedObjects + newObjects + modifiedObjects)
            .distinctBy { syncObject -> syncObject.id }
    }


    override suspend fun getAllObjectsForTask(
        taskId: String
    ): List<SyncObject> {
        return syncObjectDAO.getAllObjectsForTask(taskId)
    }


    override suspend fun getObjectsForTaskWithModificationState(
        taskId: String,
        modificationState: ModificationState
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsWithModificationState(taskId, modificationState)
    }

    override suspend fun getObjectsForTaskWithSyncState(
        taskId: String,
        syncState: ExecutionState
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsWithSyncState(taskId, syncState)
    }

    override suspend fun getInTargetMissingObjects(taskId: String): List<SyncObject> {
        return syncObjectDAO.getObjectsNotDeletedInSourceButDeletedInTarget(taskId)
    }

    override suspend fun getList(
        taskId: String,
        readingStrategy: ReadingStrategy
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsForTask(taskId).filter {
            readingStrategy.isAcceptedForSync(it)
        }
    }

    override suspend fun markAsBusy(objectId: String) {
        changeSyncState(objectId, ExecutionState.RUNNING)
    }

    @Deprecated("Нужна транзакция")
    override suspend fun markAsSuccessfullySynced(objectId: String) {
        changeSyncState(objectId, ExecutionState.SUCCESS)
        setIsExistsInTarget(objectId, true)
    }

    override suspend fun setTargetReadingState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateDAO.setTargetReadingState(objectId, state, errorMsg)
    }

    override suspend fun setBackupState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateDAO.setBackupState(objectId, state, errorMsg)
    }

    override suspend fun setDeletionState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateDAO.setDeletionState(objectId, state, errorMsg)
    }

    override suspend fun setRestorationState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateDAO.setRestorationState(objectId, state, errorMsg)
    }

    override suspend fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String) {
        syncObjectStateDAO.setSyncState(objectId, state, errorMsg)
    }

    override suspend fun markAsError(objectId: String, errorMsg: String) {
        changeSyncState(objectId, ExecutionState.ERROR, errorMsg)
    }


    override suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String) {
        syncObjectDAO.setSyncState(objectId, syncState, errorMsg)
    }


    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectListAsLiveData(taskId)


    override suspend fun setSyncDate(objectId: String, date: Long)
        = syncObjectDAO.setSyncDate(objectId, date)


    override suspend fun markAllObjectsAsDeleted(taskId: String)
        = syncObjectDAO.setStateOfAllItems(taskId, ModificationState.DELETED)


    override suspend fun getSyncObject(taskId: String, name: String, relativeParentDirPath: String): SyncObject?
        = syncObjectDAO.getSyncObject(taskId, name, relativeParentDirPath)


    override suspend fun clearObjectsWasSuccessfullyDeleted(taskId: String)
        = syncObjectDAO.deleteObjectsWithModificationAndSyncState(taskId, ModificationState.DELETED, ExecutionState.SUCCESS)

    override suspend fun deleteObjectWithDeletedState(objectId: String)
        = syncObjectDAO.deleteDeletedObject(objectId)


    override suspend fun updateSyncObject(modifiedSyncObject: SyncObject)
        = syncObjectDAO.updateSyncObject(modifiedSyncObject)

    override suspend fun setIsExistsInTarget(objectId: String, isExists: Boolean)
        = syncObjectDAO.setExistsInTarget(objectId, isExists)


    override suspend fun changeModificationState(
        syncObject: SyncObject,
        modificationState: ModificationState
    ) {
        syncObjectDAO.changeModificationState(
            name = syncObject.name,
            relativeParentDirPath = syncObject.relativeParentDirPath,
            taskId = syncObject.taskId,
            modificationState = modificationState
        )
    }

    override suspend fun markBadStatesAsNeverSynced(taskId: String) {
        badObjectStateResettingDAO.markRunningStateAsNeverSynced(taskId)
        badObjectStateResettingDAO.markErrorStateAsNeverSynced(taskId)
    }

    override suspend fun resetTargetReadingErrorStateForTask(taskId: String) {
        syncObjectBadStateResettingDAO.resetTargetReadingErrorState(taskId)
    }

    override suspend fun resetBackupErrorStateForTask(taskId: String) {
        syncObjectBadStateResettingDAO.resetBackupErrorState(taskId)
    }

    override suspend fun resetDeletionStateForTask(taskId: String) {
        syncObjectBadStateResettingDAO.resetDeletionErrorState(taskId)
    }

    override suspend fun resetRestorationErrorStateForTask(taskId: String) {
        syncObjectBadStateResettingDAO.resetRestorationErrorState(taskId)
    }
}