package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader

class LogOfSyncViewModel : ViewModel() {

    private var isFirstRun = true
    private val mediatorLiveData: MediatorLiveData<List<LogOfSync>> = MediatorLiveData()
    val logOfSync: LiveData<List<LogOfSync>> get() = mediatorLiveData

    private val syncObjectLogReader: SyncObjectLogReader by lazy { appComponent.getSyncObjectLogReader() }
    private val executionLogReader: ExecutionLogReader by lazy { appComponent.getExecutionLogReader() }

    private val currentSyncObjectLogItemList: MutableList<SyncObjectLogItem> = mutableListOf()
    private val currentExecutionLogItemList: MutableList<ExecutionLogItem> = mutableListOf()
    private val logOfSyncList: MutableList<LogOfSync> = mutableListOf()

    fun startWork(taskId: String, executionId: String) {
        if (isFirstRun) {
            isFirstRun = false

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
    }

    private fun processAndPublishCompiundLog() {
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
}
