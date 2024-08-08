package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import java.util.UUID

@Entity(
    tableName = "task_logs"
)
class TaskLogEntry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "entry_type") val entryType: EntryType,
    @ColumnInfo(name = "execution_id") val executionId: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "error_msg") val errorMsg: String?,
) {
    constructor(
        taskId: String,
        executionId: String,
        entryType: EntryType,
        errorMsg: String? = null
    ) : this(
        id = UUID.randomUUID().toString(),
        executionId = executionId,
        taskId = taskId,
        entryType = entryType,
        timestamp = currentTime(),
        errorMsg = null
    )

    enum class EntryType { START, FINISH, ERROR }

    override fun toString(): String {
        return "TaskLogEntry(id='$id', taskId='$taskId', entryType=$entryType, executionId='$executionId', timestamp=$timestamp, errorMsg=$errorMsg)"
    }
}