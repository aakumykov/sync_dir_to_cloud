package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SyncObjectLogsRepository @Inject constructor(
    private val dao: SyncObjectLogDAO,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun addLogItem(syncObjectLogItem: SyncObjectLogItem) {
        dao.addLogItem(syncObjectLogItem)
    }

    fun listLogItems(taskId: String, syncObjectId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return dao.listLogItems(taskId, syncObjectId, executionId)
    }

    suspend fun deleteLogsOfTask(taskId: String) {
        dao.deleteLogItemsForTask(taskId)
    }
}