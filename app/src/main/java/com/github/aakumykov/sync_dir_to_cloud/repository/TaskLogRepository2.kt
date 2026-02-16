package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskLogger2DAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository2 @Inject constructor(
    private val dao: TaskLogger2DAO,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
) {
    suspend fun add(taskLogItem: TaskLogItem) = withContext(dispatcher) {
        dao.add(taskLogItem)
    }

    fun getLogsForTask(taskId: String): LiveData<List<TaskLogItem>> {
        return dao.getLogsForTask(taskId)
    }
}