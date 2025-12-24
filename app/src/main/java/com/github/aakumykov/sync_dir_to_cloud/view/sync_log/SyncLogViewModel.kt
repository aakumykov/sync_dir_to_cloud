package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.SyncOperationLogReader
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
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
    private val currentTaskExecutionLogItemList: MutableList<TaskExecutionLogItem> = mutableListOf()
    private val currentFileOperationLogItemList: MutableList<FileOperationLogItem> = mutableListOf()
    private val resultingList: MutableList<LogOfSync> = mutableListOf()
    private var isFirstRun = true


    fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
            prepareMediatorLiveData(taskId, executionId)
        }
    }


    private fun prepareMediatorLiveData(taskId: String, executionId: String) {

        mediatorLiveData.addSource(executionLogReader.getExecutionLog(taskId,executionId)) { list ->
            currentTaskExecutionLogItemList.clear()
            currentTaskExecutionLogItemList.addAll(list)
            processAndPublishCompoundLog()
        }

        mediatorLiveData.addSource(syncOperationLogReader.listAsLiveData(taskId, executionId)) { list ->
            currentFileOperationLogItemList.apply {
                clear()
                addAll(list)
            }
            processAndPublishCompoundLog()
        }
    }

    private fun processAndPublishCompoundLog() {
        resultingList.apply {
            clear()

            val syncLog = currentFileOperationLogItemList.map { LogOfSync.fromFileOperationLogItem(it) }
            addAll(syncLog)

            val executionLog = currentTaskExecutionLogItemList.map { LogOfSync.fromTaskExecutionLogItem(it) }
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
