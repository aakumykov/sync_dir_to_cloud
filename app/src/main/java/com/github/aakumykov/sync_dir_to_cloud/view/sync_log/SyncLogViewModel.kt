package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val operationCancellationHolder: OperationCancellationHolder,
    private val instructionLoggerAssistedFactory: InstructionLoggerAssistedFactory,
    private val fileOperationLogger2AssistedFactory: FileOperationLogger2AssistedFactory,
) : ViewModel() {

    private var isFirstRun = true

    private val _logOfSync: MutableStateFlow<List<LogOfSync>> = MutableStateFlow(emptyList())
    val logOfSync: Flow<List<LogOfSync>> = _logOfSync


    suspend fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
        }

        val instructionLogger = instructionLogger(taskId, executionId)
        val fileOperationLogger = fileOperationLogger(taskId, executionId)

        val instructionLogs: List<LogOfSync> = instructionLogger
            .list()
            .distinctBy {
                "${it.taskId}--${it.executionId}--${it.message}"
            }
            .map {
            LogOfSync(
                timestamp = it.timestamp,
                logItemType = it.logItemType,
                taskId = it.taskId,
                executionId = it.executionId,
                jobId = null,
                text = it.message,
            )
        }

        val fileOperationLogs: List<LogOfSync> = fileOperationLogger
            .list()
            .distinctBy {
                "${it.taskId}--${it.executionId}--${it.message}"
            }
            .map{
                LogOfSync(
                    timestamp = it.timestamp,
                    logItemType = it.logItemType,
                    taskId = it.taskId,
                    executionId = it.executionId,
                    jobId = null,
                    text = it.message,
                    subText = "${it.firstItem}, ${it.secondItem}"
                )
            }

        val commonList = instructionLogs + fileOperationLogs

        commonList.sortedBy { it.timestamp }.also { _logOfSync.emit(it) }
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(id)?.cancelAndJoin()
        }
    }


    private fun instructionLogger(taskId: String, executionId: String): InstructionLogger {
        return instructionLoggerAssistedFactory.create(taskId, executionId)
    }

    private fun fileOperationLogger(taskId: String, executionId: String): FileOperationLogger2 {
        return fileOperationLogger2AssistedFactory.create(taskId, executionId)
    }
}
