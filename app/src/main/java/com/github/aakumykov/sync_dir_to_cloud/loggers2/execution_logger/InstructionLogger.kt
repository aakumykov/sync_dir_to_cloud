package com.github.aakumykov.sync_dir_to_cloud.loggers2.execution_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
) {
    fun logInstructionExecutionStarted(logItemId: String, logMessage: TextMessage) {
        InstructionLogItem.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            Log.d(TAG, "Выполнение инструкции $it")
        }
    }


    fun logInstructionExecutionFinished(logItemId: String, logMessage: TextMessage) {
        InstructionLogItem.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage.get(resources),
            timestamp = currentTime
        ).also {
//            Log.d(TAG, "Выполнение инструкции завершено $it")
        }
    }


    fun logInstructionExecutionCancelled(logItemId: String, logMessage: TextMessage) {
        InstructionLogItem.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            Log.i(TAG, "Выполнение инструкции отменено $it")
        }
    }


    fun logInstructionExecutionError(logItemId: String, logMessage: TextMessage, throwable: Throwable) {
        InstructionLogItem.create(
            id = logItemId,
            taskId = taskId,
            executionId = executionId,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            Log.e(TAG, "Ошибка выполнения инструкции $it --> ${throwable.errorMsgExtended}")
        }
    }


    companion object {
        val TAG: String = InstructionLogger::class.java.simpleName
    }
}


@AssistedFactory
interface InstructionLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): InstructionLogger
}