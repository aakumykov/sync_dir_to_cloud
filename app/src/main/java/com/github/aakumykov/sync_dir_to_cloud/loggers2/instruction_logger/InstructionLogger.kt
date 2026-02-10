package com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger

import android.content.res.Resources
import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.InstructionLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.InstructionLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.utils.runNonCancellable
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class InstructionLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val repository: InstructionLogRepository,
) {
    suspend fun logInstructionExecutionStarted(logItemId: String, logMessage: TextMessage) {
        runNonCancellable {
            InstructionLogItem.create(
                id = logItemId,
                taskId = taskId,
                executionId = executionId,
                logItemType = LogItemType.BUSY,
                message = logMessage.get(resources),
                timestamp = currentTime
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.message}")
            }
        }
    }


    suspend fun logInstructionExecutionFinished(logItemId: String, logMessage: TextMessage) {
        runNonCancellable {
            InstructionLogItem.create(
                id = logItemId,
                taskId = taskId,
                executionId = executionId,
                logItemType = LogItemType.SUCCESS,
                message = logMessage.get(resources),
                timestamp = currentTime
            ).also {
                repository.add(it)
                Log.d(TAG, "${it.logItemType}: ${it.message}")
            }
        }
    }


    suspend fun logInstructionExecutionCancelled(logItemId: String, logMessage: TextMessage) {
        runNonCancellable {
            InstructionLogItem.create(
                id = logItemId,
                taskId = taskId,
                executionId = executionId,
                logItemType = LogItemType.CANCELLED,
                message = logMessage.get(resources),
                timestamp = currentTime
            ).also {
                repository.add(it)
                Log.i(TAG, "${it.logItemType}: ${it.message}")
            }
        }
    }


    suspend fun logInstructionExecutionError(logItemId: String, logMessage: TextMessage, throwable: Throwable) {
        runNonCancellable {
            InstructionLogItem.create(
                id = logItemId,
                taskId = taskId,
                executionId = executionId,
                logItemType = LogItemType.ERROR,
                message = logMessage.get(resources),
                timestamp = currentTime
            ).also {
                repository.add(it)
                Log.e(TAG, "${it.logItemType}: ${it.message}", throwable)
            }
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