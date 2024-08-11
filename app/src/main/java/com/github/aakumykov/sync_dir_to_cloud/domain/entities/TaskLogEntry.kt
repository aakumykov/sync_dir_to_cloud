package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import java.util.UUID

@Entity(
    tableName = TaskLogEntry.TABLE_NAME
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
        errorMsg = errorMsg
    )

    enum class EntryType { START, FINISH, ERROR }

    override fun toString(): String {
        return "TaskLogEntry(id='$id', taskId='$taskId', entryType=$entryType, executionId='$executionId', timestamp=$timestamp, errorMsg=$errorMsg)"
    }

    companion object {
        const val TABLE_NAME = "sync_task_logs"
        const val OLD_TABLE_NAME = "task_logs"
    }

    @RenameTable(fromTableName = OLD_TABLE_NAME, toTableName = TABLE_NAME)
    class RenameTableFromTaskLogsToSyncTaskLogs : AutoMigrationSpec
}