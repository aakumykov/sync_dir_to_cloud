package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType

@Entity(
    tableName = ExecutionLogItem.TABLE_NAME,
//    primaryKeys = ["id", "taskId", "executionId"]
)
class ExecutionLogItem (
    @PrimaryKey val id: String,
    val taskId: String,
    val executionId: String,
    val type: ExecutionLogItemType,
    val message: String,
    val timestamp: Long,
) {
    companion object {
        const val TABLE_NAME = "execution_log"
    }
}