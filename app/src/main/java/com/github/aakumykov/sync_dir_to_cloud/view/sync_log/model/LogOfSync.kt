package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

@DatabaseView(
    viewName = LogOfSync.TABLE_NAME,
    value = "SELECT id as log_id, task_id, log_item_type, log_item_about, execution_id, message as text, timestamp FROM task_logs " +
            "UNION ALL SELECT id as log_id, task_id, log_item_type, log_item_about, execution_id, message as text, timestamp FROM instruction_logs " +
            "UNION ALL SELECT id as log_id, task_id, log_item_type, log_item_about, execution_id, message as text, timestamp FROM file_operation_logs"
)
data class LogOfSync(
    @ColumnInfo(name = "log_id")
    val origLogId: String,

    val timestamp: Long,

    @ColumnInfo(name = "log_item_type")
    val logItemType: LogItemType,

    @ColumnInfo("log_item_about")
    val logItemAbout: LogItemAbout,

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

    companion object {
        const val TABLE_NAME = "sync_logs"
    }
}