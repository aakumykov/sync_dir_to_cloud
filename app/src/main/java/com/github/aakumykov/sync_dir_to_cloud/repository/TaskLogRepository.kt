package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncTaskLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TaskLogRepository @Inject constructor(
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher,
    private val syncTaskLogDAO: SyncTaskLogDAO
) {
    fun getLogsForTask(taskId: String): LiveData<List<TaskLogEntry>> {
        return syncTaskLogDAO.getLogsForTask(taskId)
    }
}