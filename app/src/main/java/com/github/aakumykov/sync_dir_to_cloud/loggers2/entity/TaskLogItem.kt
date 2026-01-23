package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.LogEntryType
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime

@Entity(
    tableName = TaskLogItem.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TaskLogItem::class,
            parentColumns = ["id"],
            childColumns = [TaskLogItem.FIELD_TASK_ID],
            onDelete = CASCADE,
            onUpdate = NO_ACTION
        )
    ]
)
data class TaskLogItem(
    @PrimaryKey val id: String,
    @ColumnInfo(name = FIELD_TASK_ID) val taskId: String,
    @ColumnInfo(name = FIELD_EXECUTION_ID) val executionId: String,
    @ColumnInfo(name = FIELD_LOG_ENTRY_TYPE) val entryType: LogEntryType,
    val message: String?,
    val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "task_logs"
        const val FIELD_TASK_ID = "task_id"
        const val FIELD_EXECUTION_ID = "execution_id"
        const val FIELD_LOG_ENTRY_TYPE = "entry_type"

        fun create(
            entryType: LogEntryType,
            taskId: String,
            executionId: String,
            message: String?
        ) = TaskLogItem(
            entryType = entryType,
            id = newRandomId,
            message = message,
            taskId = taskId,
            executionId = executionId,
            timestamp = currentTime,
        )
    }
}