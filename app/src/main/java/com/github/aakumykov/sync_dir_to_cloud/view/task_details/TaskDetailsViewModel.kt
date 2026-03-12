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
import com.github.aakumykov.sync_dir_to_cloud.repository.LogOfSyncRepository
import com.github.aakumykov.sync_dir_to_cloud.repository.TaskLogRepository
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private var syncTaskReader: SyncTaskReader,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val startStopSyncTaskUseCase: StartStopSyncTaskUseCase,
    private val taskLogRepository: TaskLogRepository,
    private val logOfSyncRepository: LogOfSyncRepository,
) : ViewModel() {

    suspend fun taskDetailsItemList(taskId: String): List<TaskDetailsItem> {
         return taskLogRepository.list(taskId)
            .map { taskLogItem ->
                logOfSyncRepository.list(taskLogItem.taskId, taskLogItem.executionId)
            }
            .filter {
                it.isNotEmpty()
            }
            .map { list: List<LogOfSync> ->
                TaskDetailsItem.fromSyncLog(list)
            }
            .toList()
    }

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
        return taskLogRepository.listForTaskAsLiveData(taskId)
    }
}
