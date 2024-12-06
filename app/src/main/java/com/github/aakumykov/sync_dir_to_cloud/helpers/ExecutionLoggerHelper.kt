package com.github.aakumykov.sync_dir_to_cloud.helpers

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogger
import javax.inject.Inject

class ExecutionLoggerHelper @Inject constructor(
    private val executionLogger: ExecutionLogger,
    private val resources: Resources,
) {
    @Deprecated("Добавить TAG и писать в консоль")
    suspend fun logStart(taskId: String, executionId: String, operationId: String, @StringRes messageRes: Int) {
        executionLogger.log(
            ExecutionLogItem.createStartingItem(
                taskId = taskId,
                executionId = executionId,
                operationId = operationId,
                message = resources.getString(messageRes),
            )
        )
    }

    suspend fun logError(taskId: String, executionId: String, operationId: String, tag: String, e: Exception) {
        e.errorMsg.also {  errorMessage ->
            Log.e(tag, errorMessage, e)
            executionLogger.log(
                ExecutionLogItem.createErrorItem(
                    taskId = taskId,
                    executionId = executionId,
                    operationId = operationId,
                    message = errorMessage,
                )
            )
        }
    }
}