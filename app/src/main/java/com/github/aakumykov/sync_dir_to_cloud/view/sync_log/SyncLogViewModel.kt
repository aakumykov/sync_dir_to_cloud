package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task_log.TaskLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2
import com.github.aakumykov.sync_dir_to_cloud.loggers2.file_operation_logger_2.FileOperationLogger2AssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLogger
import com.github.aakumykov.sync_dir_to_cloud.loggers2.instruction_logger.InstructionLoggerAssistedFactory
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

        val il = instructionLogger(taskId, executionId)
        val fol = fileOperationLogger(taskId, executionId)

        il.getLogs().combine(fol.getLogs()) { instructionLogItem, fileOperationLogItem ->

        }
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
