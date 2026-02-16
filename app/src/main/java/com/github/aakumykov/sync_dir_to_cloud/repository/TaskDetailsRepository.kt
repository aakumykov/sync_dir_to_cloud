package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.TaskDetailsDAO
import com.github.aakumykov.sync_dir_to_cloud.view.task_details.model.TaskDetailsItem
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class TaskDetailsRepository @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val dao: TaskDetailsDAO
) {
    fun getLogsForTask(taskId: String, executionId: String): LiveData<List<TaskDetailsItem>> {
        return dao.getLogsForTask(taskId, executionId)
    }
}