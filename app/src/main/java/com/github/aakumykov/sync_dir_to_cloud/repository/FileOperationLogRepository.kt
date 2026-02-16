package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationJobIdReader
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.FileOperationLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Двойка в названии косметическая.
class FileOperationLogRepository @Inject constructor(
    private val dao: FileOperationLogDAO,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
)
    : FileOperationJobIdReader
{
    suspend fun add(taskLogItem: FileOperationLogItem) = withContext(dispatcher) {
        when(taskLogItem.logItemType) {
            LogItemType.BUSY -> dao.add(taskLogItem)
            else -> dao.update(taskLogItem)
        }
    }

    override suspend fun getJobId(logItemId: String): String?
        = dao.getJobId(logItemId)
}