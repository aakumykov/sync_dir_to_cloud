package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import java.util.UUID

@Entity(
    tableName = "sync_object_logs",
    /*primaryKeys = [
        "task_id", "object_id", "execution_id"
    ]*/
)
@Deprecated("Переименовать в SyncObjectLogInfo и пояснить такое название")
data class SyncObjectLogItem (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "object_id") val objectId: String,
    @ColumnInfo(name = "execution_id") val executionId: String,
    val timestamp: Long,
    val name: String,
    val message: String,
    @ColumnInfo(name = "is_successful") val isSuccessful: Boolean
) {
    companion object {
        fun createSuccess(taskId: String, executionId: String, syncObject: SyncObject, message: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                message = message,
                isSuccessful = true
            )
        }

        fun createFailed(taskId: String, executionId: String, syncObject: SyncObject, message: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                message = message,
                isSuccessful = false
            )
        }

        private fun create(
            taskId: String,
            executionId: String,
            syncObject: SyncObject,
            isSuccessful: Boolean,
            message: String
        ): SyncObjectLogItem {
            return SyncObjectLogItem(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                objectId = syncObject.id,
                executionId = executionId,
                timestamp = currentTime(),
                name = syncObject.name,
                message = message,
                isSuccessful = isSuccessful
            )
        }
    }
}