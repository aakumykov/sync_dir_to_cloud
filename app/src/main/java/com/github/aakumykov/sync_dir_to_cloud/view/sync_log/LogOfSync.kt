package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

data class LogOfSync(
    val text: String,
    val subText: String,
    val timestamp: Long,
    val operationState: OperationState,
    val progress: Int? = null,
    val isCancelable: Boolean,
    @Deprecated("переименовать в cancelationId") val operationId: String,
) {
    companion object {

        fun from(executionLogItem: ExecutionLogItem): LogOfSync {
            return LogOfSync(
                text = executionLogItem.message,
                subText = executionLogItem.type.name,
                timestamp = executionLogItem.timestamp,
                operationState = executionLogItemTypeToOperationState(executionLogItem.type),
                isCancelable = false,
                operationId = executionLogItem.operationId,
            )
        }

        fun from(syncObjectLogItem: SyncObjectLogItem): LogOfSync {
            return LogOfSync(
                text = syncObjectLogItem.operationName,
                timestamp = syncObjectLogItem.timestamp,
                subText = syncObjectLogItem.itemName,
                operationState = syncObjectLogItem.operationState,
                progress = syncObjectLogItem.progress,
                isCancelable = true,
                operationId = syncObjectLogItem.operationId,
            )
        }

        private fun executionLogItemTypeToOperationState(executionLogItemType: ExecutionLogItemType): OperationState {
            return when(executionLogItemType) {
                ExecutionLogItemType.FINISH-> OperationState.SUCCESS
                ExecutionLogItemType.ERROR-> OperationState.ERROR
                else -> OperationState.RUNNING
            }
        }
    }

    override fun toString(): String {
        return "LogOfSync(text='$text', subText='$subText', timestamp=$timestamp)"
    }
}


fun LogOfSync.toSyncLogDialogInfo(): SyncLogDialogInfo {
    return SyncLogDialogInfo(
        title = text,
        subtitle = subText,
        errorMessage = if (OperationState.ERROR == operationState) subText else null,
        operationState = operationState,
        timestamp = timestamp
    )
}