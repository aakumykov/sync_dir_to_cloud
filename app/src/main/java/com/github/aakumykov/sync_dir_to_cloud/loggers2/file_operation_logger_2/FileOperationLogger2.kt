package com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem2
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class FileOperationLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: FileOperationLogRepository2,
    private val resources: Resources,
) {
    suspend fun getLogs(): Flow<FileOperationLogItem2> {
        return repository.getAsFlow(taskId, executionId)
    }

    suspend fun logStarted(
        jobId: String,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?
    ) {
        runNonCancellable {
            FileOperationLogItem2.create(
                logItemType = LogItemType.BUSY,
                taskId = taskId,
                executionId = executionId,
                message = resources.getString(operationName),
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = jobId
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.message} ($firstItem --> $secondItem)")
            }
        }
    }

    suspend fun logFinished(
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?
    ) {
        runNonCancellable {
            FileOperationLogItem2.create(
                logItemType = LogItemType.SUCCESS,
                taskId = taskId,
                executionId = executionId,
                message = resources.getString(operationName),
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.message} ($firstItem --> $secondItem)")
            }
        }
    }

    suspend fun logCancelled(
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        e: CancellationException
    ) {
        val message = resources.getString(operationName) + " (${e.errorMsg})"
        runNonCancellable {
            FileOperationLogItem2.create(
                logItemType = LogItemType.CANCELLED,
                taskId = taskId,
                executionId = executionId,
                message = message,
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null
            ).also {
                repository.add(it)
                Log.i(TAG, "${it.logItemType}: ${it.message} ($firstItem --> $secondItem)")
            }
        }
    }

    suspend fun logError(
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        t: Throwable
    ) {
        val message = resources.getString(operationName) + " (${t.errorMsgExtended})"
        runNonCancellable {
            FileOperationLogItem2.create(
                logItemType = LogItemType.ERROR,
                taskId = taskId,
                executionId = executionId,
                message = message,
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null
            ).also {
                repository.add(it)
                Log.e(TAG, "${it.logItemType}: ${it.message} ($firstItem --> $secondItem)", t)
            }
        }
    }

    companion object {
        val TAG: String = FileOperationLogger2::class.java.simpleName
    }
}


@AssistedFactory
interface FileOperationLogger2AssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): FileOperationLogger2
}