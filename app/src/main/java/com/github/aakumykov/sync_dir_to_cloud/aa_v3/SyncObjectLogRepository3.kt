package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import android.content.res.Resources
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import com.github.aakumykov.sync_dir_to_cloud.repository.room.dao.SyncObjectLogDAO
import com.github.aakumykov.sync_dir_to_cloud.view.other.utils.TextMessage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectLogRepository3 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val syncObjectLogDAO: SyncObjectLogDAO,
    // FIXME: зачем это здесь? @DispatcherIO private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun logWaiting(syncObject: SyncObject, operationName: TextMessage) {
        syncObjectLogDAO.addLogItem(
            SyncObjectLogItem.createWaiting(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName.get(resources)
            )
        )
    }

    suspend fun logProgress(objectId: String, progress: Int) {
        syncObjectLogDAO.updateProgress(
            objectId = objectId,
            taskId = taskId,
            executionId = executionId,
            progress = progress
        )
    }

    suspend fun logSuccess(syncObject: SyncObject, operationName: TextMessage) {
        syncObjectLogDAO.updateLogItem(
            SyncObjectLogItem.createSuccess(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName.get(resources)
            )
        )
    }

    suspend fun logFail(syncObject: SyncObject,
                        operationName: TextMessage,
                        throwable: Throwable) {
        syncObjectLogDAO.updateLogItem(
            SyncObjectLogItem.createFailed(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName.get(resources),
                errorMessage = throwable.errorMsg
            )
        )
    }
}

@AssistedFactory
interface SyncObjectLogRepository3AssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
    ): SyncObjectLogRepository3
}