package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.SyncOperationLogReader
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter.LogOfSync
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SyncLogViewModel(
    // FIXME: "Reader"
    private val syncOperationLogReader: SyncOperationLogReader,
    private val executionLogReader: ExecutionLogReader,
    private val operationCancellationHolder: OperationCancellationHolder,
)
    : ViewModel()
{
    val logOfSync: LiveData<List<LogOfSync>> get() = mediatorLiveData

    private val mediatorLiveData: MediatorLiveData<List<LogOfSync>> = MediatorLiveData()
    private val currentExecutionLogItemList: MutableList<ExecutionLogItem> = mutableListOf()
    private val currentSyncOperationLogItemList: MutableList<SyncOperationLogItem> = mutableListOf()
    private val logOfSyncList: MutableList<LogOfSync> = mutableListOf()
    private var isFirstRun = true


    fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
            prepareMediatorLiveData(taskId, executionId)
        }
    }


    private fun prepareMediatorLiveData(taskId: String, executionId: String) {

        mediatorLiveData.addSource(syncOperationLogReader.listAsLiveData(taskId, executionId)) { list ->
            currentSyncOperationLogItemList.apply {
                clear()
                addAll(list)
            }
            processAndPublishCompoundLog()
        }

        mediatorLiveData.addSource(executionLogReader.getExecutionLog(taskId,executionId)) { list ->
            currentExecutionLogItemList.clear()
            currentExecutionLogItemList.addAll(list)
            processAndPublishCompoundLog()
        }
    }

    private fun processAndPublishCompoundLog() {
        logOfSyncList.apply {
            clear()

            val syncLog = currentSyncOperationLogItemList.map { LogOfSync.from(it) }
            addAll(syncLog)

            val executionLog = currentExecutionLogItemList.map { LogOfSync.from(it) }
            addAll(executionLog)

            sortBy { it.timestamp }

            mediatorLiveData.value = this
        }
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(id)?.cancelAndJoin()
        }
    }
}
