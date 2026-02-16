package com.github.aakumykov.sync_dir_to_cloud.view.task_details.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

@DatabaseView(
    viewName = TaskDetailsItem.TABLE_NAME,
    value = "SELECT ${TaskDetailsItem.BASIC_FIELDS_TO_SELECT}, " +
            "t1.timestamp AS start_timestamp, " +
            "t1.timestamp AS finish_timestamp " +
            "FROM task_logs AS t1 " +
            "INNER JOIN task_logs AS t2 " +
            "ON t1.text = t2.text " +
            "AND t1.log_item_type = 'BUSY' " +
            "AND t2.log_item_type != 'BUSY'"
)
data class TaskDetailsItem(
    val id: String,
    @ColumnInfo(name = "log_item_type") val logItemType: LogItemType,
    @ColumnInfo(name = "log_item_about") val logItemAbout: LogItemAbout,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,
    val text: String,
    @ColumnInfo(name = "sub_text") val subText: String,
    @ColumnInfo(name = "start_timestamp") val startTimestamp: Long,
    @ColumnInfo(name = "finish_timestamp") val finishTimestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "task_details"

        const val BASIC_FIELDS_TO_SELECT =
                "t1.id, " +
                "t1.log_item_type, " +
                "t1.log_item_about, " +
                "t1.task_id, " +
                "t1.execution_id, " +
                "t1.text, " +
                "t1.sub_text"
    }
}
