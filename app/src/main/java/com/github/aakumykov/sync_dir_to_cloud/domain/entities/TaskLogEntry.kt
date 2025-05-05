package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import java.util.UUID

@Entity(
    tableName = TaskLogEntry.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"])
    ]
)
class TaskLogEntry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "entry_type") val entryType: ExecutionLogItemType,
    @ColumnInfo(name = "execution_id") val executionId: String,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "finish_time", defaultValue = "0") val finishTime: Long,
    @ColumnInfo(name = "error_msg") val errorMsg: String?,
) {
    val executionLength: Long get() = finishTime - startTime

    constructor(
        taskId: String,
        executionId: String,
        entryType: ExecutionLogItemType,
        errorMsg: String? = null
    ) : this(
        id = UUID.randomUUID().toString(),
        executionId = executionId,
        taskId = taskId,
        entryType = entryType,
        startTime = currentTime(),
        finishTime = currentTime(),
        errorMsg = errorMsg
    )

    override fun toString(): String {
        return "TaskLogEntry(id='$id', taskId='$taskId', entryType=$entryType, executionId='$executionId', startTime=$startTime, finishTime=$finishTime, errorMsg=$errorMsg)"
    }

    companion object {
        const val TABLE_NAME = "sync_task_logs"
        const val OLD_TABLE_NAME = "task_logs"
    }

    @RenameTable(fromTableName = OLD_TABLE_NAME, toTableName = TABLE_NAME)
    class RenameTableFromTaskLogsToSyncTaskLogs : AutoMigrationSpec

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "timestamp", toColumnName = "start_time")
    class RenameColumnFromTimestampToStartTime : AutoMigrationSpec
}