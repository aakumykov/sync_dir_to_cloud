package com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogEntryType
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsgExtended
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.InstructionLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InstructionLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val repository: InstructionLogRepository,
) {
    suspend fun logInstructionExecutionStarted(logMessage: TextMessage) {
        InstructionLogItem.create(
            taskId = taskId,
            executionId = executionId,
            logEntryType = LogEntryType.BUSY,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            repository.add(it)
            Log.d(TAG, it.message)
//            Log.d(TAG, "Выполняется инструкция '$it'")
        }
    }


    suspend fun logInstructionExecutionFinished(logMessage: TextMessage) {
        InstructionLogItem.create(
            taskId = taskId,
            executionId = executionId,
            logEntryType = LogEntryType.SUCCESS,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            repository.add(it)
            Log.d(TAG, it.message)
        }
    }


    suspend fun logInstructionExecutionCancelled(logMessage: TextMessage) {
        InstructionLogItem.create(
            taskId = taskId,
            executionId = executionId,
            logEntryType = LogEntryType.CANCELLED,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            repository.add(it)
            Log.e(TAG, it.message)
//            Log.i(TAG, "Отменено выполнение инструкции '$it'")
        }
    }


    suspend fun logInstructionExecutionError(logMessage: TextMessage, throwable: Throwable) {
        InstructionLogItem.create(
            taskId = taskId,
            executionId = executionId,
            logEntryType = LogEntryType.ERROR,
            logMessage = logMessage.get(resources),
            timestamp = currentTime
        ).also {
            repository.add(it)
            Log.e(TAG, it.message, throwable)
//            Log.e(TAG, "Ошибка выполнения инструкции $it --> ${throwable.errorMsgExtended}", throwable)
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