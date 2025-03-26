package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger

import android.content.res.Resources
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction6
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation6
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.repository.SyncOperationLoggerRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncOperationLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: SyncOperationLoggerRepository,
    private val resources: Resources,
) {
    suspend fun logWaiting(syncInstruction6: SyncInstruction6): String {
        return syncInstructionWithState(syncInstruction6, OperationState.WAITING).let {
            repository.add(it)
            it.id
        }
    }

    suspend fun logSuccess(logItemId: String) {
        repository.updateLogItemState(logItemId, OperationState.SUCCESS)
    }

    suspend fun logFail(logItemId: String, errorMsg: String) {
        repository.updateLogItemState(logItemId, OperationState.ERROR, errorMsg)
    }


    private fun syncInstructionWithState(syncInstruction: SyncInstruction6, operationState: OperationState): SyncOperationLogItem {
        return SyncOperationLogItem(
            id = randomUUID,
            taskId = taskId,
            executionId = executionId,
            timestamp = currentTime(),
            sourceObjectId = syncInstruction.objectIdInSource!!,
            targetObjectId = syncInstruction.objectIdInTarget!!,
            operationName = operationNameFor(syncInstruction.operation),
            operationState = operationState,
            objectName = syncInstruction.relativePath
        )
    }

    private fun operationNameFor(operation: SyncOperation6): String {
        return resources.getString(when(operation) {
            SyncOperation6.RESOLVE_COLLISION -> R.string.SYNC_OBJECT_LOGGER_resolving_collision
            SyncOperation6.COPY_FROM_SOURCE_TO_TARGET -> R.string.SYNC_OPERATION_copying_from_source_to_target
            SyncOperation6.COPY_FROM_TARGET_TO_SOURCE -> R.string.SYNC_OPERATION_copying_from_target_to_source
            SyncOperation6.DELETE_IN_SOURCE -> R.string.SYNC_OPERATION_deleting_from_source
            SyncOperation6.DELETE_IN_TARGET -> R.string.SYNC_OPERATION_deleting_from_target
            SyncOperation6.BACKUP_IN_SOURCE -> R.string.SYNC_OPERATION_backing_up_in_source
            SyncOperation6.BACKUP_IN_TARGET -> R.string.SYNC_OPERATION_backing_up_in_target
        })
    }
}


@AssistedFactory
interface SyncOperationLoggerAssistedFactory {
    fun create(
        @Assisted(QUALIFIER_TASK_ID) taskId: String,
        @Assisted(QUALIFIER_EXECUTION_ID) executionId: String,
    ): SyncOperationLogger
}