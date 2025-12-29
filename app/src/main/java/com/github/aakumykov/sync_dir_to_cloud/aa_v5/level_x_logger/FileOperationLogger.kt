package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.enums.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.FileOperationLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileOperationLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: FileOperationLogRepository,
    private val resources: Resources,
) {
    suspend fun logWaiting(logItemId: String, syncInstruction: SyncInstruction, jobId: String?) {
        Log.d(
            TAG,
            "logWaiting() called with: logItemId = $logItemId, syncInstruction = $syncInstruction, jobId = $jobId"
        )
        repository.add(fileOperationWithState(logItemId, syncInstruction, OperationState.WAITING, jobId))
    }

    suspend fun logSuccess(logItemId: String) {
        Log.d(TAG, "logSuccess() called with: logItemId = $logItemId")
        repository.updateLogItemState(logItemId, OperationState.SUCCESS)
    }

    suspend fun logCancelled(logItemId: String, cancellationMessage: String) {
        Log.d(
            TAG,
            "logCancelled() called with: logItemId = $logItemId, cancellationMessage = $cancellationMessage"
        )
        repository.updateLogItemState(logItemId, OperationState.CANCELLED, cancellationMessage)
    }

    suspend fun logFail(logItemId: String, errorMsg: String) {
        Log.d(TAG, "logFail() called with: logItemId = $logItemId, errorMsg = $errorMsg")
        repository.updateLogItemState(logItemId, OperationState.ERROR, errorMsg)
//        repository.updateLogItemState(logItemId, OperationState.ERROR)
    }


    private fun fileOperationWithState(
        id: String,
        syncInstruction: SyncInstruction,
        operationState: OperationState,
        jobId: String?,
    ): FileOperationLogItem {
        return FileOperationLogItem(
            id = id,
            taskId = taskId,
            executionId = executionId,
            jobId = jobId,
            timestamp = currentTime,
            sourceObjectId = syncInstruction.objectIdInSource,
            targetObjectId = syncInstruction.objectIdInTarget,
            operationName = operationNameFor(syncInstruction.operation),
            operationState = operationState,
            objectName = syncInstruction.relativePath
        )
    }

    private fun operationNameFor(operation: SyncOperation): String {
        return resources.getString(when(operation) {
            SyncOperation.RESOLVE_COLLISION -> R.string.SYNC_OBJECT_LOGGER_resolving_collision
            SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> R.string.SYNC_OPERATION_copying_from_source_to_target
            SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> R.string.SYNC_OPERATION_copying_from_target_to_source
            SyncOperation.DELETE_IN_SOURCE -> R.string.SYNC_OPERATION_deleting_from_source
            SyncOperation.DELETE_IN_TARGET -> R.string.SYNC_OPERATION_deleting_from_target
            SyncOperation.BACKUP_IN_SOURCE -> R.string.SYNC_OPERATION_backing_up_in_source
            SyncOperation.BACKUP_IN_TARGET -> R.string.SYNC_OPERATION_backing_up_in_target
            SyncOperation.DO_NOTHING_IN_SOURCE -> R.string.SYNC_OPERATION_do_nothing_in_source
            SyncOperation.DO_NOTHING_IN_TARGET -> R.string.SYNC_OPERATION_do_nothing_in_target
        })
    }

    companion object {
        val TAG: String = FileOperationLogger::class.java.simpleName
    }
}


@AssistedFactory
interface FileOperationLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): FileOperationLogger
}