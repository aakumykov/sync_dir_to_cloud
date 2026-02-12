package com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class FileOperationLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: FileOperationLogRepository2,
    private val resources: Resources,
) {
    suspend fun logStarted(
        logItemId: String,
        jobId: String,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?
    ) {
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.BUSY,
                taskId = taskId,
                executionId = executionId,
                message = resources.getString(operationName),
                subText = "$firstItem --> $secondItem",
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
        logItemId: String,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?
    ) {
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.SUCCESS,
                taskId = taskId,
                executionId = executionId,
                message = resources.getString(operationName),
                subText = "$firstItem --> $secondItem",
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
        logItemId: String,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        e: CancellationException
    ) {
        val message = resources.getString(operationName) + " (${e.errorMsg})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.CANCELLED,
                taskId = taskId,
                executionId = executionId,
                message = message,
                subText = "$firstItem --> $secondItem",
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
        logItemId: String,
        @StringRes operationName: Int,
        firstItem: String?,
        secondItem: String?,
        t: Throwable
    ) {
        val message = resources.getString(operationName) + " (${t.errorMsgExtended})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.ERROR,
                taskId = taskId,
                executionId = executionId,
                message = message,
                subText = "$firstItem --> $secondItem",
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