package com.github.aakumykov.sync_dir_to_cloud.sync_object_logger

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter.DirDeleter.Companion.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter.DirDeleter.Companion.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogAdder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogDeleter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogReader
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectLogUpdater
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object_log.SyncObjectProgressUpdater
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val syncObjectLogAdder: SyncObjectLogAdder,
    private val syncObjectLogUpdater: SyncObjectLogUpdater,
    private val syncObjectProgressUpdater: SyncObjectProgressUpdater,
) {
    suspend fun logWaiting(
        syncObjectList: List<SyncObject>,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectList.forEach { syncObject ->
            syncObjectLogAdder.addLogItem(SyncObjectLogItem.createWaiting(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = getString(operationNameRes)
            ))
        }
    }

    suspend fun logProgress(
        objectId: String,
        taskId: String,
        executionId: String,
        progressAsPartOf100: Int
    ) {
        syncObjectProgressUpdater.updateProgress(objectId, taskId, executionId, progressAsPartOf100)
    }

    suspend fun logSuccess(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectLogUpdater.updateLogItem(
            SyncObjectLogItem.createSuccess(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = getString(operationNameRes)
            )
        )
    }

    suspend fun logFail(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
        errorMessage: String
    ) {
        syncObjectLogUpdater.updateLogItem(
            SyncObjectLogItem.createFailed(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = getString(operationNameRes),
                errorMessage = errorMessage
            )
        )
    }


    private fun getString(@StringRes stringRes: Int): String = resources.getString(stringRes)

    private fun getString(@StringRes stringRes: Int, vararg arguments: Any) =
        resources.getString(stringRes, arguments)


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(QUALIFIER_TASK_ID) taskId: String,
            @Assisted(QUALIFIER_EXECUTION_ID) executionId: String
        ): SyncObjectLogger2
    }
}