package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.execution_log.ExecutionLogReader

class LogOfSyncViewModel : ViewModel() {

    fun getLogOfSync(taskId: String, executionId: String): LiveData<List<ExecutionLogItem>> {
        return executionLogReader.getExecutionLog(taskId, executionId)
    }

//    private var isFirstRun = true
//    private val mediatorLiveData: MediatorLiveData<List<LogOfSync>> = MediatorLiveData()
//    val logOfSync: LiveData<List<LogOfSync>> get() = mediatorLiveData

    private val executionLogReader: ExecutionLogReader = appComponent.getExecutionLogReader()
}
