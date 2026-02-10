package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

@DatabaseView(
    viewName = "sync_logs",
    value = "SELECT id as orig_log_id, task_id, execution_id, message as text FROM task_logs " +
            "UNION ALL SELECT id as orig_log_id, task_id, execution_id, message as text FROM instruction_logs " +
            "UNION ALL SELECT id as orig_log_id, task_id, execution_id, message as text FROM file_operation_logs"
)
data class LogOfSync(
    @ColumnInfo(name = "orig_log_id")
    val origLogId: String,

    val timestamp: Long,

    @ColumnInfo(name = "log_item_type")
    val logItemType: LogItemType,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "execution_id")
    val executionId: String,

    val text: String?,
) {
    val key: String get() = "${taskId}${executionId}${timestamp}"

    override fun toString(): String {
        return "LogOfSync(origLogId='$origLogId', timestamp=$timestamp, logItemType=$logItemType, taskId='$taskId', executionId='$executionId', text=$text, key='$key')"
    }


}