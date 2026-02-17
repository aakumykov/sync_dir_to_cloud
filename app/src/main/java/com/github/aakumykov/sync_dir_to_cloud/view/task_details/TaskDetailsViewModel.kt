package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.use_cases.sync_task.StartStopSyncTaskUseCase
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object.SyncObjectDBReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task.SyncTaskReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository2
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private var syncTaskReader: SyncTaskReader,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val startStopSyncTaskUseCase: StartStopSyncTaskUseCase,
    private val taskLogRepository2: TaskLogRepository2,
) : ViewModel() {

    suspend fun getSyncTask(taskId: String): LiveData<SyncTask> {
        return syncTaskReader.getSyncTaskAsLiveData(taskId)
    }

    suspend fun getSyncObjectList(taskId: String): LiveData<List<SyncObject>> {
        return syncObjectDBReader.getSyncObjectListAsLiveData(taskId)
    }

    fun startStopTask(taskId: String) {
        viewModelScope.launch { startStopSyncTaskUseCase.startStopSyncTask(taskId) }
    }

    fun getTaskLogLiveData(taskId: String): LiveData<List<TaskLogItem>> {
        return taskLogRepository2.listForTaskAsLiveData(taskId)
    }
}
