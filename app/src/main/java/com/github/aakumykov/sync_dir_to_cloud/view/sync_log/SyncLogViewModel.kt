package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val operationCancellationHolder: OperationCancellationHolder,
)
    : ViewModel()
{
    private var isFirstRun = true

    private val _logOfSync: MutableStateFlow<List<LogOfSync>> = MutableStateFlow(emptyList())
    val logOfSync: Flow<List<LogOfSync>> = _logOfSync

    fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
        }
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(id)?.cancelAndJoin()
        }
    }
}
