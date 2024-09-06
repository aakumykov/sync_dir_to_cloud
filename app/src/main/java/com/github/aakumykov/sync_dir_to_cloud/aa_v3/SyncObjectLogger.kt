package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
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
    suspend fun logWaiting(syncObject: SyncObject, @StringRes operationName: Int) {
        syncLogRepository.addLogItem(SyncObjectLogItem.createWaiting(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationName)
        ))
    }

    suspend fun logSuccess(syncObject: SyncObject, @StringRes operationName: Int) {
        syncLogRepository.updateLogItem(SyncObjectLogItem.createSuccess(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationName)
        ))
    }

    suspend fun logError(syncObject: SyncObject, @StringRes operationName: Int, errorMsg: String) {
        syncLogRepository.updateLogItem(SyncObjectLogItem.createFailed(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationName),
            errorMessage = errorMsg,
        ))
    }

    private fun getString(@StringRes strRes: Int): String = resources.getString(strRes)
}


@AssistedFactory
interface SyncObjectLoggerAssistedFactory {
    fun create(@Assisted("task_id") taskId: String, @Assisted("execution_id") executionId: String): SyncObjectLogger
}