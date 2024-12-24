package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.cancellation_holders.OperationCancellationHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

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

        mediatorLiveData.addSource(syncObjectLogReader.getListAsLiveData(taskId, executionId)) { syncObjectLogItemList ->
            currentSyncObjectLogItemList.clear()
            currentSyncObjectLogItemList.addAll(syncObjectLogItemList)
            processAndPublishCompiundLog()
        }

        mediatorLiveData.addSource(executionLogReader.getExecutionLog(taskId,executionId)) { executionLogItemList ->
            currentExecutionLogItemList.clear()
            currentExecutionLogItemList.addAll(executionLogItemList)
            processAndPublishCompiundLog()
        }
    }

    private fun processAndPublishCompiundLog() {
        logOfSyncList.apply {
            clear()

            val syncLog = currentSyncObjectLogItemList.map { LogOfSync.from(it) }
            addAll(syncLog)

            val executionLog = currentExecutionLogItemList.map { LogOfSync.from(it) }
            addAll(executionLog)

            sortBy { it.timestamp }

            // В RecyclerView.ListAdapter необходимо каждый раз отправлять новый список,
            // иначе он не обновляется.
            // https://stackoverflow.com/questions/49726385/listadapter-not-updating-item-in-recyclerview
            mediatorLiveData.value = this.toList()
        }
    }


    fun getListLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return syncObjectLogReader.getListAsLiveData(taskId,executionId)
    }

    fun cancelJob(id: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)

    }

    fun cancelOperation(operationId: String) {
        // FIXME: не ViewMdodelScope, а "application scope" (!)
        viewModelScope.launch {
            operationCancellationHolder.getJob(operationId).also { operationJob ->
                Log.d(TAG, "operationJob: $operationJob")
                operationJob?.cancel(CancellationException("Отменено пользователем"))
                // TODO: как сообщать, что операция не найдена?
            }
        }
    }

    companion object {
        val TAG: String = SyncLogViewModel::class.java.simpleName
    }
}
