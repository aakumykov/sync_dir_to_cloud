package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import android.util.Log
import androidx.collection.longFloatMapOf
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
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log_compose.LogItemText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private var syncTaskReader: SyncTaskReader,
    private val syncObjectDBReader: SyncObjectDBReader,
    private val startStopSyncTaskUseCase: StartStopSyncTaskUseCase,
    private val taskLogRepository: TaskLogRepository,
    private val logOfSyncRepository: LogOfSyncRepository,
) : ViewModel() {

    /*suspend fun taskDetailsItemListFlow(taskId: String): Flow<List<TaskDetailsItem>> {
         return taskLogRepository.list(taskId)
            .map { taskLogItem ->
                logOfSyncRepository.list(taskLogItem.taskId, taskLogItem.executionId)
            }
            .filter {
                it.isNotEmpty()
            }
            .map { logOfSyncList ->
                TaskDetailsItem.fromSyncLog(logOfSyncList)
            }
            .toList()
             .let {
                 flow { emit(it) }
             }
    }*/

    /*@OptIn(ExperimentalCoroutinesApi::class)
    suspend fun taskDetailsItemListFlow(taskId: String): Flow<List<TaskDetailsItem>> {
        return taskLogRepository.listAsFlow(taskId)
            .filter { it.isNotEmpty() }
            .flatMapLatest { taskLogItems ->
                val executionId = taskLogItems.first().executionId
                logOfSyncRepository.listAsFlow(taskId, executionId)
            }
            .map { logOfSyncs ->
                TaskDetailsItem.fromSyncLog(logOfSyncs)
            }
            .toList(mutableListOf())
            .let {
                flow { emit(it) }
            }
    }*/

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun taskDetailsItemListFlow(taskId: String): Flow<List<TaskDetailsItem>> {
        return taskLogRepository.listAsFlow(taskId)
            .flatMapLatest { taskLogItems ->
                flow { emit(
                    taskLogItems.map { it.executionId }
                ) }
            }
            .flatMapLatest { executionIds ->
                Log.d(TAG, executionIds.joinToString(","))
                flow { executionIds.forEach {
                    emit(
                        logOfSyncRepository.list(taskId,it)
                    )
                } }
            }
            .flatMapLatest { value ->
//                Log.d(TAG, value.joinToString(",") { it.origLogId })
                flow { emit(TaskDetailsItem.fromSyncLog(value)) }
            }
            .flatMapLatest {
                Log.d(TAG, it.toString())
                emptyFlow()
            }
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

    companion object {
        val TAG: String = TaskDetailsViewModel::class.java.simpleName
    }
}