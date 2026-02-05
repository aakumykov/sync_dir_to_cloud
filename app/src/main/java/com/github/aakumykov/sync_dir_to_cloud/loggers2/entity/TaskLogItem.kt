package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime

@Entity(
    tableName = TaskLogItem.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = [GlobalConstants.FIELD_TASK_ID],
            onDelete = CASCADE,
            onUpdate = NO_ACTION
        )
    ]
)
// Не удаляй, это новый класс.
data class TaskLogItem(
    @PrimaryKey val id: String,
    @ColumnInfo(name = GlobalConstants.FIELD_TASK_ID) val taskId: String,
    @ColumnInfo(name = GlobalConstants.FIELD_EXECUTION_ID) val executionId: String,
    @ColumnInfo(name = GlobalConstants.FIELD_LOG_ITEM_TYPE) val logItemType: LogItemType,
    val message: String?,
    @ColumnInfo(name = GlobalConstants.FIELD_TIMESTAMP) val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "task_logs"

        fun create(
            entryType: LogItemType,
            taskId: String,
            executionId: String,
            message: String?
        ) = TaskLogItem(
            logItemType = entryType,
            id = newRandomId,
            message = message,
            taskId = taskId,
            executionId = executionId,
            timestamp = currentTime,
        )
    }
}