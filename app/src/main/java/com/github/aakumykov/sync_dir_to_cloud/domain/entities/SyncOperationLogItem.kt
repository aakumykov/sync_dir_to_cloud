package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

@Entity(
    tableName = "sync_operation_logs",
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ],
)
class SyncOperationLogItem(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "execution_id")
    val executionId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "source_object_id")
    val sourceObjectId: String?,

    @ColumnInfo(name = "target_object_id")
    val targetObjectId: String?,

    @ColumnInfo(name = "object_name")
    val objectName: String,

    @ColumnInfo(name = "operation_name")
    val operationName: String,

    @ColumnInfo(name = "operation_state")
    val operationState: OperationState,

    @ColumnInfo(name = "error_msg", defaultValue = "null")
    val errorMsg: String? = null,
)