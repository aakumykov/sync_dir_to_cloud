package com.github.aakumykov.sync_dir_to_cloud.repository

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.extensions.tag
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.LogOfSyncDAO
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogOfSyncRepository @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineDispatcher,
    private val dao: LogOfSyncDAO,
) {
    suspend fun listAsFlow(taskId: String, executionId: String): Flow<List<LogOfSync>> {
        return withContext(dispatcher) {
            dao.listAsFlow(taskId, executionId)
        }
    }

    suspend fun get(logItemAbout: LogItemAbout, origLogId: String): LogOfSync? {
        return withContext(dispatcher) {
            dao.get(logItemAbout, origLogId)
        }
    }
}