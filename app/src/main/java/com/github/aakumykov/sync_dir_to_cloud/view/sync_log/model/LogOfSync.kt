package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

data class LogOfSync(
    val timestamp: Long,
    val operationState: OperationState,
    val taskId: String,
    val executionId: String,
    val jobId: String? = null,
    val text: String,
    val subText: String,
    val progress: Int? = null,
) {
    companion object {

        fun fromTaskExecutionLogItem(item: TaskExecutionLogItem): LogOfSync {
            return LogOfSync(
                taskId = item.taskId,
                executionId = item.executionId,
                text = item.message,
                subText = item.details ?: "",
                timestamp = item.timestamp,
                operationState = executionLogItemTypeToOperationState(item.type),
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
        return "LogOfSync(" +
                "timestamp=$timestamp, " +
                "operationState=$operationState, " +
                "text='$text', " +
                "taskId='$taskId', " +
                "executionId='$executionId', " +
                "jobId=$jobId, " +
                "subText='$subText', " +
                "progress=$progress" +
                ")"
    }

}


val LogOfSync.isRunning: Boolean get() {
    return operationState in listOf(OperationState.RUNNING, OperationState.WAITING)
}