package com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.LogItem2
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class ExecutionLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
) {
    suspend fun logExecutionStarted(executionLogItem: LogItem2) {
        Log.d(TAG, "logTaskStarted() called with: executionLogItem = $executionLogItem")
    }

    suspend fun logExecutionFinished(executionLogItem: LogItem2) {
        Log.d(TAG, "logTaskFinished() called with: executionLogItem = $executionLogItem")
    }

    suspend fun logExecutionCancelled(executionLogItem: LogItem2, e: CancellationException) {
        Log.d(TAG, "logTaskCancelled() called with: executionLogItem = $executionLogItem, e = $e")
    }

    suspend fun logExecutionError(
        executionLogItem: LogItem2,
        t: Throwable
    ) {
        Log.d(TAG, "logTaskError() called with: executionLogItem = $executionLogItem, t = $t")
    }

    companion object {
        val TAG: String = ExecutionLogger2::class.java.simpleName
    }
}


@AssistedFactory
interface ExecutionLogger2AssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): ExecutionLogger2
}