package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

/*@DatabaseView(
    viewName = "",
    value = "SELECT log_item_type, log_item_about, task_id, execution_id, "
)*/
data class SyncLogItem(
    val taskId: String,
    val executionId: String,
    val jobId: String? = null,
    val logItemType: LogItemType,
    val logItemAbout: LogItemAbout,
    val text: String?,
    val subText: String? = null,
    val timestamp: Long,
    val progress: Int? = null,
) {
    val key: String get() = "${taskId}${executionId}${timestamp}"

    override fun toString(): String {
        return "SyncLogItem(" +
                "timestamp=$timestamp, " +
                "logItemType=$logItemType, " +
                "text='$text', " +
                "taskId='$taskId', " +
                "executionId='$executionId', " +
                "jobId=$jobId, " +
                "subText='$subText', " +
                "progress=$progress" +
                ")"
    }

}