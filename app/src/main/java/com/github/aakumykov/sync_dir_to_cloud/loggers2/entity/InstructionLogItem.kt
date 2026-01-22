package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime

data class InstructionLogItem(
    val id: String,
    val taskId: String,
    val executionId: String,
    val message: String,
    val timestamp: Long,
) {
    companion object {
        fun create(
            id: String,
            taskId: String,
            executionId: String,
            logMessage: String,
            timestamp: Long,
        ): InstructionLogItem {
            return InstructionLogItem(
                id = id,
                taskId = taskId,
                executionId = executionId,
                message = logMessage,
                timestamp = timestamp,
            )
        }
    }

    override fun toString(): String {
        return "InstructionLogItem('$message', taskId='$taskId', executionId='$executionId')"
    }
}

fun SyncTask.toInstructionLogItem(executionId: String, logMessage: String): InstructionLogItem {
    return InstructionLogItem.create(
        id = newRandomId,
        taskId = this.id,
        executionId = executionId,
        logMessage = logMessage,
        timestamp = currentTime
    )
}