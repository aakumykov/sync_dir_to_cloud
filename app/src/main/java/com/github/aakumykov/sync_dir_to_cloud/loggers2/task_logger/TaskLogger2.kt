package com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository2
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class TaskLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: TaskLogRepository2,
    private val resources: Resources,
) {
    suspend fun logTaskStarted() = runNonCancellable {
        taskLogWithMessage(LogItemType.BUSY)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.message}, ${it.timestamp}")
            }
    }

    suspend fun logTaskFinished() = runNonCancellable {
        taskLogWithMessage(LogItemType.SUCCESS)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.message}, ${it.timestamp}")
            }
    }

    suspend fun logTaskCancelled(e: CancellationException) = runNonCancellable {
        taskLogWithMessage(LogItemType.CANCELLED)
            .also {
                repository.add(it)
                Log.i(TAG, "${it.message}, ${it.timestamp} (${e.errorMsgExtended})")
            }
    }

    suspend fun logTaskError(t: Throwable) = runNonCancellable {
        taskLogWithMessage(LogItemType.ERROR, t.errorMsg).also {
            repository.add(it)
            Log.e(TAG, "${it.message}, ${it.timestamp}", t)
        }
    }

    private fun taskLogWithMessage(logItemType: LogItemType, message: String? = null): TaskLogItem = TaskLogItem.create(
        entryType = logItemType,
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