package com.github.aakumykov.sync_dir_to_cloud.sync_object_logger

import android.content.res.Resources
import androidx.annotation.StringRes
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter.DirDeleter.Companion.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter.DirDeleter.Companion.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncObjectLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncObjectLogger2 @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val resources: Resources,
    private val syncObjectLogRepository: SyncObjectLogRepository
) {
    suspend fun logWaiting(
        syncObjectList: List<SyncObject>,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectList.forEach { syncObject ->
            syncObjectLogRepository.addLogItem(SyncObjectLogItem.createWaiting(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = getString(operationNameRes)
            ))
        }
    }


    suspend fun logSuccess(
        syncObject: SyncObject,
        @StringRes operationNameRes: Int,
    ) {
        syncObjectLogRepository.updateLogItem(
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
        syncObjectLogRepository.updateLogItem(
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