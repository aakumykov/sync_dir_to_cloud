package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(
    tableName = SyncObjectLogItem.TABLE_NAME,
    /*primaryKeys = [
        TASK_ID_FIELD,
        OBJECT_ID_FIELD,
        EXECUTION_ID_FIELD,
        TIMESTAMP_FIELD
    ],*/
    /*indices = [
        Index(
            TASK_ID_FIELD,
            OBJECT_ID_FIELD,
            EXECUTION_ID_FIELD,
            TIMESTAMP_FIELD,
            unique = true
        )
    ]*/
)
@Parcelize
data class SyncObjectLogItem (
    @PrimaryKey val id: String,
    @ColumnInfo(name = TASK_ID_FIELD) val taskId: String,
    @ColumnInfo(name = OBJECT_ID_FIELD) val objectId: String,
    @ColumnInfo(name = EXECUTION_ID_FIELD) val executionId: String,
    @ColumnInfo(name = TIMESTAMP_FIELD) val timestamp: Long,
    val name: String,
    @ColumnInfo(name = OPERATION_NAME_FILED) val operationName: String,
    @ColumnInfo(name = IS_SUCCESSFUL_FIELD) val isSuccessful: Boolean
)
    : Parcelable
{
    companion object {

        const val TABLE_NAME = "sync_object_logs"

        const val TASK_ID_FIELD = "task_id"
        const val OBJECT_ID_FIELD = "object_id"
        const val EXECUTION_ID_FIELD = "execution_id"
        const val IS_SUCCESSFUL_FIELD = "is_successful"
        const val TIMESTAMP_FIELD = "timestamp"
        const val OPERATION_NAME_FILED = "operation_name"

        fun createSuccess(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                isSuccessful = true
            )
        }

        fun createFailed(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                isSuccessful = false
            )
        }

        private fun create(
            taskId: String,
            executionId: String,
            syncObject: SyncObject,
            isSuccessful: Boolean,
            operationName: String
        ): SyncObjectLogItem {
            return SyncObjectLogItem(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                objectId = syncObject.id,
                executionId = executionId,
                timestamp = currentTime(),
                name = syncObject.name,
                operationName = operationName,
                isSuccessful = isSuccessful
            )
        }
    }

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "message", toColumnName = OPERATION_NAME_FILED)
    class RenameColumnMessageToOperationName : AutoMigrationSpec
}