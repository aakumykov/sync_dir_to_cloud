package com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger

import android.content.res.Resources
import android.util.Log
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogEntryType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository2
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class TaskLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: TaskLogRepository2,
    private val resources: Resources,
) {
    suspend fun logTaskStarted() {
        runNonCancellable {
            taskLogWithMessage(LogEntryType.BUSY, R.string.TASK_LOG_task_started)
                .also {
                    repository.add(it)
                    Log.d(TAG, "${it.message}, ${it.timestamp}")
                }
        }
    }

    suspend fun logTaskFinished() {
        taskLogWithMessage(LogEntryType.SUCCESS, R.string.TASK_LOG_task_finished)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.message}, ${it.timestamp}")
            }
    }

    suspend fun logTaskCancelled(e: CancellationException) {
        taskLogWithMessage(LogEntryType.CANCELLED, R.string.TASK_LOG_task_cancelled)
            .also {
                repository.add(it)
                Log.i(TAG, "${it.message}, ${it.timestamp} (${e.errorMsgExtended})")
            }
    }

    suspend fun logTaskError(t: Throwable) {
        taskLogWithMessage(LogEntryType.ERROR, t.errorMsg).also {
            repository.add(it)
            Log.e(TAG, "${it.message}, ${it.timestamp}", t)
        }
    }


    private suspend fun runNonCancellable(block: suspend () -> Unit) {
        withContext(NonCancellable) {
            block.invoke()
        }
    }

    private fun string(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun taskLogWithMessage(logEntryType: LogEntryType, @StringRes messageRes: Int): TaskLogItem
        = taskLogWithMessage(logEntryType, string(messageRes))

    private fun taskLogWithMessage(logEntryType: LogEntryType, message: String): TaskLogItem = TaskLogItem.create(
        entryType = logEntryType,
        taskId = taskId,
        executionId = executionId,
        message = message
    )

    companion object {
        val TAG: String = TaskLogger2::class.java.simpleName
    }
}


@AssistedFactory
interface TaskLogger2AssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): TaskLogger2
}