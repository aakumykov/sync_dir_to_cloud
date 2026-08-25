package com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationLogProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.FileOperationLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class DatabaseFileOperationLogger @Inject constructor(
    private val repository: FileOperationLogRepository,
    private val resources: Resources,
): FileOperationLogger, FileOperationLogProgressUpdater {

    data class LogBaseInfo(
        val taskId: String,
        val executionId: String,
        val logItemId: String,
        @param:StringRes val operationName: Int,
        val firstItem: String?,
        val secondItem: String?
    )

    override suspend fun updateProgress(logItemId: String, progress: Float) {
        repository.updateProgress(logItemId, progress)
    }

    override suspend fun logStarted(baseInfo: LogBaseInfo, jobId: String) {
        runNonCancellable {
            FileOperationLogItem.create(
                id = baseInfo.logItemId,
                logItemType = LogItemType.BUSY,
                taskId = baseInfo.taskId,
                executionId = baseInfo.executionId,
                text = resources.getString(baseInfo.operationName),
                subText = fromToFile(baseInfo.firstItem, baseInfo.secondItem),
                firstItem = baseInfo.firstItem,
                secondItem = baseInfo.secondItem,
                jobId = jobId,
                startTime = currentTime,
                finishTime = null
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.text} (${baseInfo.firstItem} --> ${baseInfo.secondItem})")
            }
        }
    }

    override suspend fun logFinished(baseInfo: LogBaseInfo) {
        runNonCancellable {
            FileOperationLogItem.create(
                id = baseInfo.logItemId,
                logItemType = LogItemType.SUCCESS,
                taskId = baseInfo.taskId,
                executionId = baseInfo.executionId,
                text = resources.getString(baseInfo.operationName),
                subText = fromToFile(baseInfo.firstItem, baseInfo.secondItem),
                firstItem = baseInfo.firstItem,
                secondItem = baseInfo.secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.d(TAG, "${it.logItemType}: ${it.text} (${baseInfo.firstItem} --> ${baseInfo.secondItem})")
            }
        }
    }

    override suspend fun logCancelled(baseInfo: LogBaseInfo, e: CancellationException) {
        val text = resources.getString(baseInfo.operationName) + " (${e.errorMsg})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = baseInfo.logItemId,
                logItemType = LogItemType.CANCELLED,
                taskId = baseInfo.taskId,
                executionId = baseInfo.executionId,
                text = text,
                subText = e.errorMsg,
                firstItem = baseInfo.firstItem,
                secondItem = baseInfo.secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.i(TAG, "${it.logItemType}: ${it.text} (${baseInfo.firstItem} --> ${baseInfo.secondItem})")
            }
        }
    }

    override suspend fun logError(baseInfo: LogBaseInfo, t: Throwable) {
        val text = resources.getString(baseInfo.operationName) + " (${t.errorMsgExtended})"
        runNonCancellable {
            FileOperationLogItem.create(
                id = baseInfo.logItemId,
                logItemType = LogItemType.ERROR,
                taskId = baseInfo.taskId,
                executionId = baseInfo.executionId,
                text = text,
                subText = t.errorMsg,
                firstItem = baseInfo.firstItem,
                secondItem = baseInfo.secondItem,
                jobId = null,
                startTime = null,
                finishTime = currentTime
            ).also {
                repository.update(it)
                Log.e(TAG, "${it.logItemType}: ${it.text} (${baseInfo.firstItem} --> ${baseInfo.secondItem})")
            }
        }
    }

    private fun fromToFile(firstItem: String?, secondItem: String?): String {
        return "$firstItem --> $secondItem"
    }

    companion object {
        val TAG: String = DatabaseFileOperationLogger::class.java.simpleName
    }
}