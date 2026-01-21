package com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.LogItem2
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

class ExecutionLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
) {
    fun logExecutionStarted(logItemId: String, logMessage: TextMessage) {
        LogItem2.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage.get(resources)
        ).also {
            Log.d(TAG, "Выполнение инструкции начато $it")
        }
    }

    fun logExecutionFinished(logItemId: String, logMessage: TextMessage) {
        LogItem2.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage.get(resources)
        ).also {
            Log.d(TAG, "Выполнение инструкции завершено $it")
        }
    }

    fun logExecutionCancelled(logItemId: String, logMessage: TextMessage) {
        LogItem2.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage.get(resources)
        ).also {
            Log.i(TAG, "Выполнение инструкции отменено $it")
        }
    }

    fun logExecutionError(logItemId: String, logMessage: TextMessage, throwable: Throwable) {
        LogItem2.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage.get(resources)
        ).also {
            Log.e(TAG, "Ошибка выполнения инструкции $it --> ${throwable.errorMsgExtended}")
        }
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