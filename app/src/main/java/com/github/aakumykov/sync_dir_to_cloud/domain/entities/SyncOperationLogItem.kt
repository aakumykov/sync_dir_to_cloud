package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_operation_logs")
class SyncOperationLogItem(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "task_id")
    val taskId: String,

    @ColumnInfo(name = "execution_id")
    val executionId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String,

    @ColumnInfo(name = "source_object_id")
    val sourceObjectId: String?,

    @ColumnInfo(name = "target_object_id")
    val targetObjectId: String?,

    @ColumnInfo(name = "object_name")
    val objectName: String,

    @ColumnInfo(name = "operation_name")
    val operationName: String,
)