package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.reading_from_source

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.file_lister_navigator_selector.recursive_dir_reader.RecursiveDirReader
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.CloudAuth
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.factories.recursive_dir_reader.RecursiveDirReaderFactory
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskStateChanger
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.strategy.ChangesDetectionStrategy
import com.github.aakumykov.sync_dir_to_cloud.utils.calculateRelativeParentDirPath
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class SourceToDatabaseLister @Inject constructor(
    private val recursiveDirReaderFactory: RecursiveDirReaderFactory,
    private val syncObjectReader: SyncObjectReader,
    private val syncObjectAdder: SyncObjectAdder,
    private val syncObjectUpdater: SyncObjectUpdater,
    private val syncTaskStateChanger: SyncTaskStateChanger,
    private val executionLogger: ExecutionLogger,
    private val resources: Resources,
    private val operationCancellationHolder: OperationCancellationHolder,
) {
    /**
     * @param scope Используется scope задачи для того, потому что отмена
     * этой операции должна отменять всю задачу.
     */
    suspend fun readFromPath(
        scope: CoroutineScope,
        pathReadingFrom: String?,
        changesDetectionStrategy: ChangesDetectionStrategy,
        cloudAuth: CloudAuth?,
        taskId: String,
        executionId: String,
    ): Job {

        val operationId = UUID.randomUUID().toString()

        val job = scope.launch {

            try {

                logExecutionStarted(taskId, executionId, operationId)

                // TODO: убрать
                delay(5000)

                if (null == pathReadingFrom)
                    throw IllegalArgumentException("path argument is null")

                if (null == cloudAuth)
                    throw IllegalArgumentException("cloudAuth argument is null")

                syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.RUNNING)

                recursiveDirReaderFactory.create(cloudAuth.storageType, cloudAuth.authToken)
                    ?.listDirRecursivelySuspend(
                        path = pathReadingFrom,
                        foldersFirst = true,
                        debug_log_each_item = true,
                        debug_each_step_delay_for_debug_ms = 5000,
                    )
                    ?.apply {
                        syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.SUCCESS)
                    }
                    ?.forEach { fileListItem ->
                        addOrUpdateFileListItem(fileListItem, pathReadingFrom, taskId, changesDetectionStrategy)
                    }

                logExecutionFinished(taskId, executionId, operationId)

            } catch (e: Exception) {

                operationCancellationHolder.removeJob(operationId)

                val errorMsg = ExceptionUtils.getErrorMessage(e)
                Log.e(TAG, errorMsg, e)

                syncTaskStateChanger.setSourceReadingState(taskId, ExecutionState.ERROR, errorMsg)
                logExecutionError(taskId, executionId, operationId, errorMsg)

                throw e
            }

        }.also { job ->
            operationCancellationHolder.addJob(operationId = operationId, job = job)
        }

        return job
    }


    private suspend fun logExecutionStarted(
        taskId: String,
        executionId: String,
        operationId: String,
    ) {
        executionLogger.log(ExecutionLogItem.createStartingItem(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            message = getString(R.string.EXECUTION_LOG_reading_source),
            isCancelable = true,
        ))
    }


    private suspend fun logExecutionError(
        taskId: String,
        executionId: String,
        operationId: String,
        errorMsg: String,
    ) {
        executionLogger.updateLog(ExecutionLogItem.createErrorItem(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            message = errorMsg
        ))
    }


    private suspend fun logExecutionFinished(
        taskId: String,
        executionId: String,
        operationId: String,
    ) {
        executionLogger.updateLog(ExecutionLogItem.createFinishingItem(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            message = getString(R.string.EXECUTION_LOG_reading_source)
        ))
    }


    private suspend fun addOrUpdateFileListItem(
        fileListItem: RecursiveDirReader.FileListItem,
        pathReadingFrom: String,
        taskId: String,
        changesDetectionStrategy: ChangesDetectionStrategy
    ) {
        val inTargetParentDirPath = calculateRelativeParentDirPath(fileListItem, pathReadingFrom)

        val existingObject = syncObjectReader.getSyncObject(
            taskId,
            fileListItem.name,
            inTargetParentDirPath)


        if (null == existingObject) {
            syncObjectAdder.addSyncObject(
                // TODO: сделать определение нового родительского пути более понятным
                SyncObject.createAsNew(
                    taskId,
                    fileListItem,
                    inTargetParentDirPath
                )
            )
        }
        else {
            syncObjectUpdater.updateSyncObject(
                SyncObject.createFromExisting(
                    existingSyncObject = existingObject,
                    modifiedFSItem = fileListItem,
                    changesDetectionStrategy.detectItemModification(pathReadingFrom, fileListItem, existingObject)
                )
            )
        }
    }

    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    companion object {
        val TAG: String = SourceToDatabaseLister::class.java.simpleName
    }
}