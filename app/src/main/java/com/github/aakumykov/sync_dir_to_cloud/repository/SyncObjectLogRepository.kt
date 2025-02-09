package com.github.aakumykov.sync_dir_to_cloud.repository

import androidx.lifecycle.LiveData
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SyncObjectLogRepository @Inject constructor(
    private val dao: SyncObjectLogDAO,
    @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
)
    : SyncObjectLogAdder,
    SyncObjectLogDeleter,
    SyncObjectLogReader,
    SyncObjectLogUpdater,
    SyncObjectLogProgressUpdater
{
    override suspend fun addLogItem(syncObjectLogItem: SyncObjectLogItem) {
        withContext(coroutineDispatcher) {
            dao.addLogItem(syncObjectLogItem)
        }
    }

    override suspend fun updateLogItem(syncObjectLogItem: SyncObjectLogItem) {
        withContext(coroutineDispatcher) {
            dao.updateLogItem(syncObjectLogItem)
        }
    }

    override suspend fun updateProgress(
        objectId: String,
        taskId: String,
        executionId: String,
        progressAsPartOf100: Int
    ) {
        withContext(coroutineDispatcher) {
            dao.updateProgress(objectId, taskId, executionId, progressAsPartOf100)
        }
    }

    override suspend fun deleteLogsForTask(taskId: String) {
        withContext(coroutineDispatcher) {
            dao.deleteLogItemsForTask(taskId)
        }
    }


    override fun getListAsLiveData(taskId: String, executionId: String): LiveData<List<SyncObjectLogItem>> {
        return dao.listAllLogItemsAsLiveData(taskId, executionId)
    }
}
