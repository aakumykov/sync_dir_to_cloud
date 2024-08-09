package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_object_logs")
data class SyncObjectLogItem (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "object_id") val objectId: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,
    val timestamp: Long,
    val name: String,
    val message: String,
    @ColumnInfo(name = "is_successful") val isSuccessful: Boolean
)