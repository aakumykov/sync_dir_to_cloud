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
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class FileOperationLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: FileOperationLogRepository,
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
                text = resources.getString(operationName),
                subText = fromToFile(firstItem, secondItem),
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = jobId,
                startTime = currentTime,
                finishTime = null
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.text} ($firstItem --> $secondItem)")
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
                text = resources.getString(operationName),
                subText = fromToFile(firstItem,secondItem),
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.d(TAG, "${it.logItemType}: ${it.text} ($firstItem --> $secondItem)")
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
        val text = resources.getString(operationName) + " (${e.errorMsg})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.CANCELLED,
                taskId = taskId,
                executionId = executionId,
                text = text,
                subText = e.errorMsg,
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.i(TAG, "${it.logItemType}: ${it.text} ($firstItem --> $secondItem)")
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
        val text = resources.getString(operationName) + " (${t.errorMsgExtended})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = logItemId,
                logItemType = LogItemType.ERROR,
                taskId = taskId,
                executionId = executionId,
                text = text,
                subText = t.errorMsg,
                firstItem = firstItem,
                secondItem = secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.e(TAG, "${it.logItemType}: ${it.text} ($firstItem --> $secondItem)", t)
            }
        }
    }

    private fun fromToFile(firstItem: String?, secondItem: String?): String {
        return "$firstItem --> $secondItem"
    }

    companion object {
        val TAG: String = FileOperationLogger::class.java.simpleName
    }
}


@AssistedFactory
interface FileOperationLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): FileOperationLogger
}