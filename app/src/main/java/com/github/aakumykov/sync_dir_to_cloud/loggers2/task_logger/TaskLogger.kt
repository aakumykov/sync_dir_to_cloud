package com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class TaskLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: TaskLogRepository2,
    private val resources: Resources,
) {
    suspend fun logTaskStarted() = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.BUSY, R.string.LOG_ITEM_task_started,
            startTime = currentTime)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.text}, ${it.startTime}")
            }
    }

    suspend fun logTaskFinished() = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.SUCCESS, R.string.LOG_ITEM_task_finished,
            finishTime = currentTime)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.text}, ${it.finishTime}")
            }
    }

    suspend fun logTaskCancelled(e: CancellationException) = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.CANCELLED, R.string.LOG_ITEM_task_cancelled,
            finishTime = currentTime)
            .also {
                repository.add(it)
                Log.i(TAG, "${it.text}, ${it.finishTime} (${e.errorMsgExtended})")
            }
    }

    suspend fun logTaskError(t: Throwable) = runNonCancellable {
        taskLogWithMessage(
            newRandomId,
            LogItemType.ERROR,
            R.string.LOG_ITEM_task_failed,
            t.errorMsg,
            finishTime = currentTime
        ).also {
            repository.add(it)
            Log.e(TAG, "${it.text}, ${it.finishTime}", t)
        }
    }

    private fun taskLogWithMessage(
        logItemId: String,
        logItemType: LogItemType,
        @StringRes messageId: Int,
        details: String? = null,
        startTime: Long? = null,
        finishTime: Long? = null
    ): TaskLogItem = TaskLogItem.create(
        id = logItemId,
        entryType = logItemType,
        taskId = taskId,
        executionId = executionId,
        text = resources.getString(messageId),
        subText = details,
        startTime = startTime,
        finishTime = finishTime
    )

    companion object {
        val TAG: String = TaskLogger::class.java.simpleName
    }
}


@AssistedFactory
interface TaskLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): TaskLogger
}