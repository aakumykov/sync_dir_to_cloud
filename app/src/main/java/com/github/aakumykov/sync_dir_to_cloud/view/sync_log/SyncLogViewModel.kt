package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.repository.LogOfSyncRepository
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val operationCancellationHolder: OperationCancellationHolder,
    private val logOfSyncRepository: LogOfSyncRepository,
) : ViewModel() {

    private var isFirstRun = true

    private val _logOfSyncListFlow: MutableStateFlow<List<LogOfSync>> = MutableStateFlow(emptyList())
    val logOfSyncListFlow: Flow<List<LogOfSync>> = _logOfSyncListFlow


    suspend fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false

            logOfSyncRepository
                .listAsFlow(taskId, executionId)
                .collect {
                    _logOfSyncListFlow.emit(it)
                }
        }
    }


    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(id)?.cancelAndJoin()
        }
    }
}
