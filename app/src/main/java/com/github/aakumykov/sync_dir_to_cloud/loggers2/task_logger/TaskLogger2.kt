package com.github.aakumykov.sync_dir_to_cloud.loggers2.task_logger

import android.R.id.message
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
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlin.math.log

class TaskLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: TaskLogRepository2,
    private val resources: Resources,
) {
    suspend fun logTaskStarted() = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.SUCCESS, R.string.LOG_ITEM_task_started)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.message}, ${it.timestamp}")
            }
    }

    suspend fun logTaskFinished() = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.SUCCESS, R.string.LOG_ITEM_task_finished)
            .also {
                repository.add(it)
                Log.d(TAG, "${it.message}, ${it.timestamp}")
            }
    }

    suspend fun logTaskCancelled(e: CancellationException) = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.CANCELLED, R.string.LOG_ITEM_task_cancelled)
            .also {
                repository.add(it)
                Log.i(TAG, "${it.message}, ${it.timestamp} (${e.errorMsgExtended})")
            }
    }

    suspend fun logTaskError(t: Throwable) = runNonCancellable {
        taskLogWithMessage(newRandomId, LogItemType.ERROR, R.string.LOG_ITEM_task_failed, t.errorMsg).also {
            repository.add(it)
            Log.e(TAG, "${it.message}, ${it.timestamp}", t)
        }
    }

    private fun taskLogWithMessage(
        logItemId: String,
        logItemType: LogItemType,
        @StringRes messageId: Int,
        details: String? = null
    ): TaskLogItem = TaskLogItem.create(
        id = logItemId,
        entryType = logItemType,
        taskId = taskId,
        executionId = executionId,
        message = resources.getString(messageId)
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