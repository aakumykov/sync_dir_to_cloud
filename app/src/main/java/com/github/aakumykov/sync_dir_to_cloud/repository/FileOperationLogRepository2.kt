package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.FileOperationLogItem2
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.FileOperationLogDAO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Двойка в названии косметическая.
class FileOperationLogRepository2 @Inject constructor(
    private val dao: FileOperationLogDAO,
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
) {
    suspend fun add(taskLogItem: FileOperationLogItem2) = withContext(dispatcher) {
        dao.add(taskLogItem)
    }

    /*suspend fun list(taskId: String, executionId: String): List<FileOperationLogItem2> = withContext(dispatcher) {
        dao.list(taskId, executionId)
    }*/

    /*suspend fun deleteAllFor(taskId: String) = withContext(dispatcher) {
        dao.deleteAllForTask(taskId)
    }*/
}