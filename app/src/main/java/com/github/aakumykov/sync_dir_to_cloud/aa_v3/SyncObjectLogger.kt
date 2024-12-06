package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.NO_OPERATION_ID
import com.yandex.disk.rest.json.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectLogger @AssistedInject constructor(
    @Assisted("execution_id") private val executionId: String,
    @Assisted("task_id") private val taskId: String,
    private val syncLogRepository: SyncObjectLogRepository,
    private val resources: Resources,
){
    suspend fun logWaiting(syncObject: SyncObject, @StringRes operationName: Int,
                           operationId: String = NO_OPERATION_ID) {
        syncLogRepository.addLogItem(SyncObjectLogItem.createWaiting(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            syncObject = syncObject,
            operationName = getString(operationName)
        ))
    }

    suspend fun logSuccess(syncObject: SyncObject, @StringRes operationName: Int,
                           operationId: String = NO_OPERATION_ID) {
        syncLogRepository.updateLogItem(SyncObjectLogItem.createSuccess(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            syncObject = syncObject,
            operationName = getString(operationName)
        ))
    }

    suspend fun logError(syncObject: SyncObject,
                         @StringRes operationName: Int,
                         operationId: String = NO_OPERATION_ID,
                         errorMsg: String) {
        syncLogRepository.updateLogItem(SyncObjectLogItem.createFailed(
            taskId = taskId,
            executionId = executionId,
            operationId = operationId,
            syncObject = syncObject,
            operationName = getString(operationName),
            errorMessage = errorMsg,
        ))
    }

    suspend fun logProgress(objectId: String,
                            taskId: String,
                            executionId: String,
                            progressAsPartOf100: Int) {
        syncLogRepository.updateProgress(
            objectId = objectId,
            taskId = taskId,
            executionId = executionId,
            progressAsPartOf100 = progressAsPartOf100
        )
    }

    private fun getString(@StringRes strRes: Int): String = resources.getString(strRes)
}


@AssistedFactory
interface SyncObjectLoggerAssistedFactory {
    fun create(@Assisted("task_id") taskId: String, @Assisted("execution_id") executionId: String): SyncObjectLogger
}