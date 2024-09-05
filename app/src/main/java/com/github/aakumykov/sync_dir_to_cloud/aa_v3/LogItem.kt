package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

data class LogItem(
    val executionId: String,
    val taskId: String,
    val objectId: String,
    @StringRes val operationName: Int,
    val errorMsg: String? = null
) {
    companion object {
        fun createWaiting(
            executionId: String,
            taskId: String,
            syncObject: SyncObject,
            @StringRes operationName: Int
        ): LogItem = LogItem(
                executionId = executionId,
                taskId = taskId,
                objectId = syncObject.id,
                operationName = operationName,
            )

        fun createSuccess(
            executionId: String,
            taskId: String,
            syncObject: SyncObject,
            operationName: Int
        ): LogItem = LogItem(
            executionId = executionId,
            taskId = taskId,
            objectId = syncObject.id,
            operationName = operationName,
        )

        fun createFailed(
            executionId: String,
            taskId: String,
            syncObject: SyncObject,
            operationName: Int,
            errorMsg: String
        ): LogItem = LogItem(
            executionId = executionId,
            taskId = taskId,
            objectId = syncObject.id,
            operationName = operationName,
            errorMsg = errorMsg,
        )
    }
}
