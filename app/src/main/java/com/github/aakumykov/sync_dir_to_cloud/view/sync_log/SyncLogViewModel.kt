package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
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
    private val logOfSync: MutableList<LogOfSync> = mutableListOf()
    private val mediatorLiveData: MediatorLiveData<List<LogOfSync>> = MediatorLiveData()


    fun startWorking(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false
            prepareMediatorLiveData(taskId, executionId)
        }
    }

    private fun prepareMediatorLiveData(taskId: String, executionId: String) {

        mediatorLiveData.addSource(
            syncObjectLogReader.getListAsLiveData(taskId, executionId)
        ) { syncObjectLogItemList ->
            syncObjectLogItemList.forEach { syncObjectLogItem: SyncObjectLogItem ->
                logOfSync.add(LogOfSync(
                    text = syncObjectLogItem.operationName,
                    subText = syncObjectLogItem.operationState.name,
                ))
                mediatorLiveData.value = logOfSync
            }
        }

        mediatorLiveData.addSource(
            executionLogReader.getExecutionLog(taskId,executionId)
        ) { executionLogItemList ->
            executionLogItemList.forEach { executionLogItem ->
                logOfSync.add(LogOfSync(
                    text = executionLogItem.message,
                    subText = executionLogItem.type.name,
                ))
                mediatorLiveData.value = logOfSync
            }
        }
    }

    fun getLogOfSync(): MediatorLiveData<List<LogOfSync>> = mediatorLiveData

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
