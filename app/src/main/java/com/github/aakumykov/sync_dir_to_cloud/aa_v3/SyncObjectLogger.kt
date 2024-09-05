package com.github.aakumykov.sync_dir_to_cloud.aa_v3

import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectLogger @AssistedInject constructor(
    @Assisted("execution_id") private val executionId: String,
    @Assisted("task_id") private val taskId: String,
    private val syncLogRepository: SyncObjectLogRepository
){
    suspend fun logWaiting(syncObject: SyncObject, @StringRes operationName: Int) {

    }

    suspend fun logSuccess(syncObject: SyncObject, @StringRes operationName: Int) {

    }

    suspend fun logError(syncObject: SyncObject, @StringRes operationName: Int, errorMsg: String) {

    }
}


@AssistedFactory
interface SyncObjectLoggerAssistedFactory {
    fun create(@Assisted("task_id") taskId: String, @Assisted("execution_id") executionId: String): SyncObjectLogger
}