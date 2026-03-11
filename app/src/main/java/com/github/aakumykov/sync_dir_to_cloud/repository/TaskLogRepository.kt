package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskLoggerDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    private val dao: TaskLoggerDAO,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
) {
    suspend fun add(taskLogItem: TaskLogItem) = withContext(dispatcher) {
        dao.add(taskLogItem)
    }

    suspend fun update(item: TaskLogItem) {
        dao.update(
            id = item.id,
            logItemType = item.logItemType,
            subText = item.subText,
            finishTime = item.finishTime!!
        )
    }

    fun listForTaskAsLiveData(taskId: String): LiveData<List<TaskLogItem>> {
        return dao.listAsLiveData(taskId)
    }

    suspend fun listAsFlow(taskId: String): Flow<List<TaskLogItem>> = withContext(dispatcher) {
        dao.listAsFlow(taskId)
    }
}