package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.AppScope
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isModified
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNew
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSuccessSynced
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isTargetReadingOk
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isUnchanged
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.notExistsInTarget
import com.github.aakumykov.sync_dir_to_cloud.enums.Side
import com.github.aakumykov.sync_dir_to_cloud.extensions.nullIfEmpty
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncTaskDirObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.SyncTaskFileObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateChanger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectStateResetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.BadObjectStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectStateSetterDAO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectBadStateResettingDAO
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.ReadingStrategy
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

@AppScope
class SyncObjectRepository @Inject constructor(
    private val syncObjectDAO: SyncObjectDAO,
    private val syncObjectStateSetterDAO: SyncObjectStateSetterDAO,
    private val syncObjectBadStateResettingDAO: SyncObjectBadStateResettingDAO,
    private val badObjectStateResettingDAO: BadObjectStateResettingDAO,
)
    : SyncObjectAdder,
        SyncObjectReader,
        SyncObjectStateChanger,
        SyncObjectStateResetter,
        SyncObjectDeleter,
        SyncObjectUpdater,

        SyncTaskFileObjectReader,
        SyncTaskDirObjectReader
{
    override suspend fun addSyncObject(syncObject: SyncObject)
        = syncObjectDAO.add(syncObject)


    override suspend fun getObjectsNeedsToBeSynced(taskId: String): List<SyncObject> {

        val neverSyncedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithSyncState(taskId, ExecutionState.NEVER)
        val newObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, StateInStorage.NEW)
        val modifiedObjects: List<SyncObject> = syncObjectDAO.getObjectsWithModificationState(taskId, StateInStorage.MODIFIED)

        return (neverSyncedObjects + newObjects + modifiedObjects)
            .distinctBy { syncObject -> syncObject.id }
    }


    override suspend fun getAllObjectsForTask(
        taskId: String
    ): List<SyncObject> {
        return syncObjectDAO.getAllObjectsForTask(taskId)
    }

    override suspend fun getAllObjectsForTask(
        side: Side,
        taskId: String,
    ): List<SyncObject> {
        return syncObjectDAO.getAllObjectsForTask(
            side = side,
            taskId = taskId,
        )
    }


    override suspend fun getObjectsForTaskWithModificationState(
        taskId: String,
        stateInStorage: StateInStorage
    ): List<SyncObject> {
        return syncObjectDAO.getObjectsWithModificationState(taskId, stateInStorage)
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
        syncObjectStateSetterDAO.setTargetReadingState(objectId, state, errorMsg)
    }

    override suspend fun setBackupState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateSetterDAO.setBackupState(objectId, state, errorMsg)
    }

    override suspend fun deleteAllObjectsForTask(taskId: String) {
        syncObjectDAO.deleteAllObjectsForTask(taskId)
    }

    override suspend fun setDeletionState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateSetterDAO.setDeletionState(objectId, state, errorMsg)
    }

    override suspend fun setRestorationState(
        objectId: String,
        state: ExecutionState,
        errorMsg: String
    ) {
        syncObjectStateSetterDAO.setRestorationState(objectId, state, errorMsg)
    }

    override suspend fun setSyncState(objectId: String, state: ExecutionState, errorMsg: String) {
        syncObjectStateSetterDAO.setSyncState(objectId, state, errorMsg)
    }

    override suspend fun markAsError(objectId: String, errorMsg: String) {
        changeSyncState(objectId, ExecutionState.ERROR, errorMsg)
    }

    override suspend fun markAsError(objectId: String, t: Throwable) {
        markAsError(objectId, ExceptionUtils.getErrorMessage(t))
    }


    override suspend fun changeSyncState(objectId: String, syncState: ExecutionState, errorMsg: String) {
        syncObjectDAO.setSyncState(objectId, syncState, errorMsg)
    }


    override suspend fun getSyncObjectListAsLiveData(taskId: String): LiveData<List<SyncObject>>
        = syncObjectDAO.getSyncObjectListAsLiveData(taskId)


    override suspend fun setSyncDate(objectId: String, date: Long)
        = syncObjectDAO.setSyncDate(objectId, date)


    override suspend fun markAllObjectsAsDeleted(taskId: String)
        = syncObjectDAO.markAllObjectsAsDeleted(taskId)

    override suspend fun getSyncObject(objectId: String): SyncObject?
        = syncObjectDAO.getSyncObject(objectId)

    override suspend fun getSyncObject(
        taskId: String,
        side: Side,
        name: String,
        relativeParentDirPath: String
    ): SyncObject?
        = syncObjectDAO.getSyncObject(taskId, side, name, relativeParentDirPath)


    override suspend fun deleteObjectWithDeletedState(objectId: String)
        = syncObjectDAO.deleteDeletedObject(objectId)


    override suspend fun updateSyncObject(modifiedSyncObject: SyncObject)
        = syncObjectDAO.updateSyncObject(modifiedSyncObject)

    override suspend fun setIsExistsInTarget(objectId: String, isExists: Boolean)
        = syncObjectDAO.setExistsInTarget(objectId, isExists)

    override suspend fun markAsUnchanged(objectId: String)
        = syncObjectDAO.setStateInStorage(objectId, StateInStorage.UNCHANGED)

    /*override suspend fun getAllObjectsForTask(
        side: Side,
        taskId: String,
        executionId: String
    ): List<SyncObject> {
        return syncObjectDAO.getAllObjectsForTask(side, taskId, executionId)
    }*/


    override suspend fun changeModificationState(
        syncObject: SyncObject,
        stateInStorage: StateInStorage
    ) {
        syncObjectDAO.changeModificationState(
            name = syncObject.name,
            relativeParentDirPath = syncObject.relativeParentDirPath,
            taskId = syncObject.taskId,
            stateInStorage = stateInStorage
        )
    }


    override suspend fun resetTargetReadingBadState(taskId: String) {
        syncObjectBadStateResettingDAO.resetTargetReadingBadState(taskId)
    }

    override suspend fun resetBackupBadState(taskId: String) {
        syncObjectBadStateResettingDAO.resetBackupBadState(taskId)
    }

    override suspend fun resetDeletionBadState(taskId: String) {
        syncObjectBadStateResettingDAO.resetDeletionBadState(taskId)
    }

    override suspend fun resetRestorationBadState(taskId: String) {
        syncObjectBadStateResettingDAO.resetRestorationBadState(taskId)
    }

    override suspend fun resetSyncBadState(taskId: String) {
        syncObjectBadStateResettingDAO.resetSyncBadState(taskId)
    }

    override suspend fun getNewFiles(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getObjectsForTask(taskId)
            .filter { it.isFile }
            .filter { it.isNew }
            .nullIfEmpty()
    }

    override fun getForgottenFiles(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getAllObjectsForTask(taskId)
            .filter { it.isFile }
            .filter { it.isNeverSynced }
            .nullIfEmpty()
    }

    override fun getInTargetLostFiles(taskId: String): List<SyncObject>? {
        return syncObjectDAO.getAllObjectsForTask(taskId)
            .filter { it.isFile }
            .filter { it.isSuccessSynced }
            .filter { it.notExistsInTarget }
            .filter { it.isTargetReadingOk }
            .nullIfEmpty()
    }

    override fun getModifiedFiles(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getAllObjectsForTask(taskId)
            .filter { it.isFile }
            .filter { it.isModified }
            .filter { it.isTargetReadingOk }
            .nullIfEmpty()
    }

    override fun getNewDirs(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getAllObjectsForTask(taskId)
            .filter { it.isDir }
            .filter { it.isNew }
            .nullIfEmpty()
    }

    override fun getNeverProcessedDirs(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getAllObjectsForTask(taskId)
            .filter { it.isDir }
            .filter { it.isNeverSynced && it.isUnchanged }
            .filter { it.isTargetReadingOk }
            .nullIfEmpty()
    }

    override fun getInTargetLostDirs(taskId: String): List<SyncObject>? {
        return syncObjectDAO
            .getAllObjectsForTask(taskId)
            .filter { it.isDir }
            .filter { it.isSuccessSynced }
            .filter { it.notExistsInTarget }
            .filter { it.isTargetReadingOk }
            .nullIfEmpty()
    }

}