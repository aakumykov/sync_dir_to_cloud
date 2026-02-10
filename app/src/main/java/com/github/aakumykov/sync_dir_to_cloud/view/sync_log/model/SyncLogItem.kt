package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

/*@DatabaseView(
    viewName = "sync_log",
    value = "SELECT id as origLogId, task_id, execution_id, message, "
)*/
data class SyncLogItem(
    val origLogId: String,
    val timestamp: Long,
    val logItemType: LogItemType,
    val taskId: String,
    val executionId: String,
    val text: String?,
) {
    val key: String get() = "${taskId}${executionId}${timestamp}"


    override fun toString(): String {
        return "SyncLogItem(origLogId='$origLogId', timestamp=$timestamp, logItemType=$logItemType, taskId='$taskId', executionId='$executionId', text=$text, key='$key')"
    }
}