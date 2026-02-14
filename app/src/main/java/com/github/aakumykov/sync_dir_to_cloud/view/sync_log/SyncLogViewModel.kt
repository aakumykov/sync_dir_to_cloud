package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.repository.LogOfSyncRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncTaskRepository
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val operationCancellationHolder: OperationCancellationHolder,
    private val logOfSyncRepository: LogOfSyncRepository,
    // TODO: заменить на SyncTaskStateReader
    private val syncTaskRepository: SyncTaskRepository,
) : ViewModel() {

    private var isFirstRun = true

    private val _logOfSyncListFlow: MutableStateFlow<List<LogOfSync>> = MutableStateFlow(emptyList())
    val logOfSyncListFlow: Flow<List<LogOfSync>> = _logOfSyncListFlow

    private val _isRunningFlow = MutableStateFlow(false)
    val isRunningFlow: Flow<Boolean> = _isRunningFlow

    suspend fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false

            viewModelScope.launch {
                logOfSyncRepository.listAsFlow(taskId, executionId).collect {
                    _logOfSyncListFlow.emit(it)
                }
            }

            viewModelScope.launch {
                syncTaskRepository.getTaskStateFlow(taskId).collect { executionState ->
                    _isRunningFlow.emit(ExecutionState.RUNNING == executionState)
                }
            }
        }
    }


    fun cancelJob(jobId: String) {
        viewModelScope.launch(NonCancellable) {
            operationCancellationHolder.getJob(jobId)?.cancelAndJoin()
        }
    }
}
