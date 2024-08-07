package com.github.aakumykov.sync_dir_to_cloud.sync_task_logger

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks_logs"
)
data class TaskLog (
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "entry_type") val entryType: EntryType,
    @ColumnInfo(name = "execution_id") val executionId: String,
    @ColumnInfo(name = "task_id") val taskId: String,

    @ColumnInfo(name = "starting_time") val startingTime: Long,
    @ColumnInfo(name = "ending_time") val endingTime: Long,

    @ColumnInfo(name = "completion_state") val completionState: CompletionState?,
) {
    enum class EntryType { START, FINISH }
    enum class CompletionState { SUCCESS, ERROR, WITH_ERRORS }
}