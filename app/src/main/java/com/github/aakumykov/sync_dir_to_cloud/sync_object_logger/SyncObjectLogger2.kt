package com.github.aakumykov.sync_dir_to_cloud.sync_object_logger

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.ExecutionScope
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/*
@ExecutionScope
class SyncObjectLogger2 @AssistedInject constructor(
    @Assisted private val taskId: String,
    @Assisted private val executionId: String,
    private val resources: Resources,
    private val syncObjectLogRepository: SyncObjectLogRepository
) {
    suspend fun logInWork(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectLogRepository.addLogItem(SyncObjectLogItem.createInWork(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationNameRes)
        ))
    }

    suspend fun logSuccess(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectLogRepository.addLogItem(SyncObjectLogItem.createSuccess(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationNameRes)
        ))
    }

    suspend fun logFail(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
        errorMessage: String
    ) {
        syncObjectLogRepository.addLogItem(SyncObjectLogItem.createFailed(
            taskId = taskId,
            executionId = executionId,
            syncObject = syncObject,
            operationName = getString(operationNameRes),
            errorMessage = errorMessage
        ))
    }


    private fun getString(@StringRes stringRes: Int): String
        = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any)
        = resources.getString(stringRes, arguments)

}*/
