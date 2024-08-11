package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncObjectLogRepository @Inject constructor(
    private val dao: SyncObjectLogDAO,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
)
    : SyncObjectLogDeleter
{
    suspend fun addLogItem(executionId: String, syncObjectLogItem: SyncObjectLogItem) {
        withContext(coroutineDispatcher) {
            dao.addLogItem(executionId, syncObjectLogItem)
        }
    }

    fun listLogItems(taskId: String, syncObjectId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return dao.listLogItems(taskId, syncObjectId, executionId)
    }

    override suspend fun deleteLogsForTask(taskId: String) {
        withContext(coroutineDispatcher) {
            dao.deleteLogItemsForTask(taskId)
        }
    }
}