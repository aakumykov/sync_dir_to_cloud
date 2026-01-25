package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.LogEntryType
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem.Companion.FIELD_TIMESTAMP
import com.github.aakumykov.sync_dir_to_cloud.newRandomId

@Entity(
    tableName = InstructionLogItem.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = [InstructionLogItem.FIELD_TASK_ID],
            onDelete = CASCADE,
            onUpdate = NO_ACTION
        )
    ]
)
data class InstructionLogItem(
    @PrimaryKey val id: String,
    @ColumnInfo(name = FIELD_LOG_ENTRY_TYPE) val entryType: LogEntryType,
    @ColumnInfo(name = FIELD_TASK_ID) val taskId: String,
    @ColumnInfo(name = FIELD_EXECUTION_ID) val executionId: String,
    val message: String,
    @ColumnInfo(name = FIELD_TIMESTAMP) val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "instruction_logs"
        const val FIELD_TASK_ID = "task_id"
        const val FIELD_EXECUTION_ID = "execution_id"
        const val FIELD_TIMESTAMP = "timestamp"
        const val FIELD_LOG_ENTRY_TYPE = "entry_type"

        fun create(
            logEntryType: LogEntryType,
            taskId: String,
            executionId: String,
            logMessage: String,
            timestamp: Long,
        ): InstructionLogItem {
            return InstructionLogItem(
                id = newRandomId,
                entryType = logEntryType,
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