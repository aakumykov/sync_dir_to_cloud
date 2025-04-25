package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_x_logger

import android.content.res.Resources
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_EXECUTION_ID
import com.github.aakumykov.sync_dir_to_cloud.QUALIFIER_TASK_ID
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncInstruction
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.common.SyncOperation
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.randomUUID
import com.github.aakumykov.sync_dir_to_cloud.repository.sync_operation_log_repository.SyncOperationLogRepository
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SyncOperationLogger @AssistedInject constructor(
    @Assisted(QUALIFIER_TASK_ID) private val taskId: String,
    @Assisted(QUALIFIER_EXECUTION_ID) private val executionId: String,
    private val repository: SyncOperationLogRepository,
    private val resources: Resources,
) {
    suspend fun logWaiting(syncInstruction: SyncInstruction): String {
        return syncInstructionWithState(syncInstruction, OperationState.WAITING).let {
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


    private fun syncInstructionWithState(syncInstruction: SyncInstruction, operationState: OperationState): SyncOperationLogItem {
        return SyncOperationLogItem(
            id = randomUUID,
            taskId = taskId,
            executionId = executionId,
            timestamp = currentTime(),
            sourceObjectId = syncInstruction.objectIdInSource,
            targetObjectId = syncInstruction.objectIdInTarget,
            operationName = operationNameFor(syncInstruction.operation),
            operationState = operationState,
            objectName = syncInstruction.relativePath
        )
    }

    private fun operationNameFor(operation: SyncOperation): String {
        return resources.getString(when(operation) {
            SyncOperation.RESOLVE_COLLISION -> R.string.SYNC_OBJECT_LOGGER_resolving_collision
            SyncOperation.COPY_FROM_SOURCE_TO_TARGET -> R.string.SYNC_OPERATION_copying_from_source_to_target
            SyncOperation.COPY_FROM_TARGET_TO_SOURCE -> R.string.SYNC_OPERATION_copying_from_target_to_source
            SyncOperation.DELETE_IN_SOURCE -> R.string.SYNC_OPERATION_deleting_from_source
            SyncOperation.DELETE_IN_TARGET -> R.string.SYNC_OPERATION_deleting_from_target
            SyncOperation.BACKUP_IN_SOURCE -> R.string.SYNC_OPERATION_backing_up_in_source
            SyncOperation.BACKUP_IN_TARGET -> R.string.SYNC_OPERATION_backing_up_in_target
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