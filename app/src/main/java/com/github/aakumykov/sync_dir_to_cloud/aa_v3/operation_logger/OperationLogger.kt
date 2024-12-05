package com.github.aakumykov.sync_dir_to_cloud.aa_v3.operation_logger

import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncObjectLogger
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.NO_OPERATION_ID
import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class OperationLogger @AssistedInject constructor(
    @Assisted private val syncObjectLogger: SyncObjectLogger
) {
    suspend fun logOperationStarts(
        syncObject: SyncObject,
        operationName: Int,
        operationId: String = NO_OPERATION_ID,
    ) {
        syncObjectLogger.apply {
            logWaiting(
                syncObject = syncObject,
                operationName = operationName,
                operationId = operationId,
            )
        }
    }


    suspend fun logOperationSuccess(
        syncObject: SyncObject,
        operationName: Int
    ) {
        syncObjectLogger.apply {
            logSuccess(syncObject = syncObject, operationName = operationName)
        }
    }


    suspend fun logOperationError(
        syncObject: SyncObject,
        operationName: Int,
        e: Exception
    ) {
        syncObjectLogger.apply {
            logError(
                syncObject = syncObject,
                operationName = operationName,
                errorMsg = ExceptionUtils.getErrorMessage(e)
            )
        }
    }

    suspend fun logProgress(
        objectId: String,
        taskId: String,
        executionId: String,
        progressAsPartOf100: Int
    ) {
        syncObjectLogger.apply {
            logProgress(
                objectId = objectId,
                taskId = taskId,
                executionId = executionId,
                progressAsPartOf100 = progressAsPartOf100
            )
        }
    }
}
