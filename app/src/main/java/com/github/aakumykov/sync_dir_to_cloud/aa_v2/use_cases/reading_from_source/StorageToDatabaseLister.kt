package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_source

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
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isSuccessSynced
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncSide
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import javax.inject.Inject

class StorageToDatabaseLister @Inject constructor(
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
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
        taskId: String,
        executionId: String,
    ): Result<Boolean> {

        Log.d(TAG, "readFromPath('$pathReadingFrom')")

        return try {

            logExecutionStarted(taskId,executionId)

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


    private suspend fun logExecutionStarted(taskId: String, executionId: String) {
        executionLogger.log(ExecutionLogItem.createStartingItem(
            taskId = taskId,
            executionId = executionId,
            message = getString(R.string.EXECUTION_LOG_reading_source)
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
        }
        else {
            changesDetectionStrategy.detectItemModification(
                pathReadingFrom,
                fileListItem,
                existingObject
            ).also { stateInStorage ->
                // Выяснять новый статус объекта имеет смысл лишь тогда,
                // когда они были синхронизированы. Если нет,
                if (existingObject.isSuccessSynced) {
                    when (stateInStorage) {
                        StateInStorage.MODIFIED -> {
                            syncObjectUpdater.updateSyncObject(
                                SyncObject.createFromExistingAsModified(
                                    newExecutionId = executionId,
                                    syncObject = existingObject,
                                    modifiedFSItem = fileListItem,
                                )
                            )
                        }

                        StateInStorage.UNCHANGED -> {
                            syncObjectUpdater.markAsUnchanged(existingObject.id)
                        }

                        else -> {
                            Log.i(
                                TAG,
                                "SyncObject: sate_in_storage: ${existingObject.stateInStorage}, ${existingObject.id}, ${existingObject.name}"
                            )
                        }
                    }
                } else {
                    syncObjectUpdater.markAsNew(existingObject.id)
                }
            }
        }
    }

    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    companion object {
        val TAG: String = StorageToDatabaseLister::class.java.simpleName
    }
}

/*
fun <T> T.isEqual(other: Any?): T? {
    return if (null == other)
        null
    else {
        if (other.equals(T.)) T
        null
    }
}*/
