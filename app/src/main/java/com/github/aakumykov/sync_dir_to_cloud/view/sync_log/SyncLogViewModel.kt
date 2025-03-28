package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

class SyncLogViewModel(
    private val syncObjectLogReader: SyncObjectLogReader,
    private val executionLogReader: ExecutionLogReader,
    private val operationCancellationHolder: OperationCancellationHolder,
)
    : ViewModel()
{
    private var isFirstRun = true
    private val mediatorLiveData: MediatorLiveData<List<LogOfSync>> = MediatorLiveData()
    val logOfSync: LiveData<List<LogOfSync>> get() = mediatorLiveData

    private val currentSyncObjectLogItemList: MutableList<SyncObjectLogItem> = mutableListOf()
    private val currentExecutionLogItemList: MutableList<ExecutionLogItem> = mutableListOf()
    private val logOfSyncList: MutableList<LogOfSync> = mutableListOf()


    fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
            prepareMediatorLiveData(taskId, executionId)
        }
    }


    private fun prepareMediatorLiveData(taskId: String, executionId: String) {

        mediatorLiveData.addSource(syncObjectLogReader.getListAsLiveData(taskId, executionId)) { list ->
            currentSyncObjectLogItemList.clear()
            currentSyncObjectLogItemList.addAll(list)
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

            val syncLog = currentSyncObjectLogItemList.map { LogOfSync.from(it) }
            addAll(syncLog)

            val executionLog = currentExecutionLogItemList.map { LogOfSync.from(it) }
            addAll(executionLog)

            sortBy { it.timestamp }

            mediatorLiveData.value = this
        }
    }


    fun getListLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return syncObjectLogReader.getListAsLiveData(taskId,executionId)
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(id)?.cancelAndJoin()
        }
    }
}
