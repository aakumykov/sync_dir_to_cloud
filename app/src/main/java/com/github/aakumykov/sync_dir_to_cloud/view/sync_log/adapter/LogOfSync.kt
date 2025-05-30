package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncOperationLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

data class LogOfSync(
    val taskId: String,
    val executionId: String,
    val text: String,
    val subText: String,
    val timestamp: Long,
    val operationState: OperationState,
    val progress: Int? = null,
) {
    companion object {

        fun from(item: ExecutionLogItem): LogOfSync {
            return LogOfSync(
                taskId = item.taskId,
                executionId = item.executionId,
                text = item.message,
                subText = item.details ?: "",
                timestamp = item.timestamp,
                operationState = executionLogItemTypeToOperationState(item.type),
            )
        }

        fun from(item: SyncObjectLogItem): LogOfSync {
            return LogOfSync(
                taskId = item.taskId,
                executionId = item.executionId,
                text = item.operationName,
                subText = item.errorMessage ?: item.operationState.name,
                timestamp = item.timestamp,
                operationState = item.operationState,
                progress = item.progress,
            )
        }

        fun from(item: SyncOperationLogItem): LogOfSync {
            return LogOfSync(
                taskId = item.taskId,
                executionId = item.executionId,
                text = item.operationName,
                subText = item.errorMsg ?: item.operationState.name,
                timestamp = item.timestamp,
                operationState = item.operationState,
                progress = null,
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