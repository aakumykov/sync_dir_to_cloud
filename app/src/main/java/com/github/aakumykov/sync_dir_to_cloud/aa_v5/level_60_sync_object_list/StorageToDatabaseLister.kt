package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_60_sync_object_list

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.StateInStorage
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isNeverSynced
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.extensions.sourceBackupsDirPath
import com.github.aakumykov.sync_dir_to_cloud.extensions.targetBackupsDirPath
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskMetadataReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StorageToDatabaseLister @AssistedInject constructor(
    @Assisted private val taskId: String,

    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,

    private val syncTaskReader: SyncTaskReader,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,

    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val executionLogger: ExecutionLogger,

    private val resources: Resources,
) {
    suspend fun listFromPathToDatabase(
        syncSide: SyncSide,
        pathReadingFrom: String?,
        changesDetectionStrategy: ChangesDetectionStrategy,
        cloudAuth: CloudAuth?,
        executionId: String,
    ): Result<Boolean> {

        Log.d(TAG, "readFromPath('$pathReadingFrom')")

        val syncTask = syncTaskReader.getSyncTask(taskId)

        return try {

            logExecutionStarted(
                taskId,
                executionId,
                if (SyncSide.SOURCE == syncSide) getString(R.string.EXECUTION_LOG_reading_source)
                else getString(R.string.EXECUTION_LOG_reading_target)
            )

            if (null == pathReadingFrom)
                throw IllegalArgumentException("path argument is null")

            if (null == cloudAuth)
                throw IllegalArgumentException("cloudAuth argument is null")

            syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.RUNNING)

            recursiveDirReaderFactory.create(cloudAuth.storageType, cloudAuth.authToken)
                ?.listDirRecursively(
                    path = pathReadingFrom,
                    foldersFirst = true
                )
                ?.filterNot { fileListItem ->
                    filterOutBackupDir(
                        syncSide,
                        syncTask.sourceBackupsDirPath,
                        syncTask.targetBackupsDirPath,
                        fileListItem)
                }
                ?.let {
                    it
                }
                ?.apply {
                    syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.SUCCESS)
                }
                ?.also { list ->
                    Log.d(TAG, "list.size: ${list.size}")
                }
                ?.forEach { fileListItem ->
                    Log.d(TAG, "fileListItem: ${fileListItem.name} (${fileListItem.size} байт)")
                    addOrUpdateFileListItem(
                        executionId = executionId,
                        syncSide = syncSide,
                        fileListItem = fileListItem,
                        pathReadingFrom = pathReadingFrom,
                        taskId = taskId,
                        changesDetectionStrategy = changesDetectionStrategy
                    )
                }

            logExecutionFinished(taskId,executionId)

            Result.success(true)

        } catch (e: Exception) {
            ExceptionUtils.getErrorMessage(e).also { errorMsg ->
                syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.ERROR, errorMsg)
                Log.e(TAG, errorMsg, e)
                logExecutionError(taskId,executionId,errorMsg)
            }
            Result.failure(e)
        }
    }


    // FIXME: убрать костыль
    private fun filterOutBackupDir(
        syncSide: SyncSide,
        sourceBackupsDirPath: String?,
        targetBackupsDirPath: String?,
        fileListItem: RecursiveDirReader.FileListItem
    ): Boolean = fileListItem.absolutePath.startsWith(
        when(syncSide) {
            SyncSide.SOURCE -> sourceBackupsDirPath ?: fileListItem.absolutePath.reversed()
            SyncSide.TARGET -> targetBackupsDirPath ?: fileListItem.absolutePath.reversed()
        }
    )


    private suspend fun logExecutionStarted(taskId: String, executionId: String, message: String) {
        executionLogger.log(ExecutionLogItem.createStartingItem(
            taskId = taskId,
            executionId = executionId,
            message = message
        ))
    }


    private suspend fun logExecutionError(taskId: String, executionId: String, errorMsg: String) {
        executionLogger.updateLog(ExecutionLogItem.createErrorItem(
            taskId = taskId,
            executionId = executionId,
            message = errorMsg
        ))
    }


    private suspend fun logExecutionFinished(taskId: String, executionId: String) {
        executionLogger.updateLog(ExecutionLogItem.createFinishingItem(
            taskId = taskId,
            executionId = executionId,
            message = getString(R.string.EXECUTION_LOG_reading_source)
        ))
    }


    private suspend fun addOrUpdateFileListItem(
        executionId: String,
        syncSide: SyncSide,
        fileListItem: RecursiveDirReader.FileListItem,
        pathReadingFrom: String,
        taskId: String,
        changesDetectionStrategy: ChangesDetectionStrategy
    ) {
        val parentDirPath = calculateRelativeParentDirPath(fileListItem, pathReadingFrom)

        val existingObject = syncObjectReader.getSyncObject(
            taskId,
            syncSide,
            fileListItem.name,
            parentDirPath)


        // Если в БД нет такого объекта, добавляю как новый и заканчиваю.
        if (null == existingObject) {
            syncObjectAdder.addSyncObject(
                // TODO: сделать определение нового родительского пути более понятным
                SyncObject.createAsNew(
                    taskId = taskId,
                    executionId = executionId,
                    fsItem = fileListItem,
                    syncSide = syncSide,
                    relativeParentDirPath = parentDirPath,
                )
            )
            return
        }

        val newStateInStorage = changesDetectionStrategy.detectItemModification(
            pathReadingFrom,
            fileListItem,
            existingObject
        )

        when(newStateInStorage) {
            StateInStorage.UNCHANGED -> {
                updateObjectAsUnchanged(existingObject)
            }
            StateInStorage.NEW -> {
                throw IllegalStateException("Object with 'NEW' state in storage cannot be used in 'update' part of code!")
            }
            StateInStorage.MODIFIED -> {
                updateObjectAsModified(existingObject.id, fileListItem)
            }
            StateInStorage.DELETED -> {
                updateObjectAsDeleted(existingObject)
            }
        }
    }


    private suspend fun updateObjectAsUnchanged(existingObject: SyncObject) {
        if (existingObject.isNeverSynced) {
            markObjectAsChecked(existingObject.id)
        } else {
            markObjectAs(existingObject.id, StateInStorage.UNCHANGED)
            markObjectAsChecked(existingObject.id)
        }
    }

    private suspend fun updateObjectAsModified(objectId: String, fsItem: RecursiveDirReader.FileListItem) {
        markObjectAs(objectId, StateInStorage.MODIFIED)
        markObjectAsChecked(objectId)
        syncObjectUpdater.updateMetadata(
            objectId = objectId,
            size = fsItem.size,
            mTime = fsItem.mTime
        )
    }

    private suspend fun updateObjectAsDeleted(existingObject: SyncObject) {
        markObjectAs(existingObject.id, StateInStorage.DELETED)
        markObjectAsChecked(existingObject.id)
    }

    private suspend fun markObjectAs(objectId: String, stateInStorage: StateInStorage) {
        syncObjectUpdater.updateStateInStorage(objectId, stateInStorage)
    }


    private suspend fun markObjectAsChecked(objectId: String) {
        syncObjectUpdater.markJustChecked(objectId)
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    companion object {
        val TAG: String = StorageToDatabaseLister::class.java.simpleName
    }
}

@AssistedFactory
interface StorageToDatabaseListerAssistedFactory {
    fun create(taskId: String): StorageToDatabaseLister
}