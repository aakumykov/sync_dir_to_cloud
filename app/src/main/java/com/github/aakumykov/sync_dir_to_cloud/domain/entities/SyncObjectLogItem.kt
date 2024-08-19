package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
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
    @ColumnInfo(name = ITEM_NAME_FILED) val itemName: String,
    @ColumnInfo(name = OPERATION_NAME_FILED) val operationName: String,
    @ColumnInfo(name = ERROR_MESSAGE_FIELD, defaultValue = "null") val errorMessage: String? = null,
    @ColumnInfo(name = IS_SUCCESSFUL_FIELD) val isSuccessful: Boolean,
    @ColumnInfo(name = OPERATION_STATE_FIELD, defaultValue = "SUCCESS") val operationState: OperationState,
)
    : Parcelable
{
    companion object {

        const val TABLE_NAME = "sync_object_logs"

        const val TASK_ID_FIELD = "task_id"
        const val OBJECT_ID_FIELD = "object_id"
        const val EXECUTION_ID_FIELD = "execution_id"
        const val IS_SUCCESSFUL_FIELD = "is_successful"
        const val OPERATION_STATE_FIELD = "operation_state"
        const val TIMESTAMP_FIELD = "timestamp"
        const val ITEM_NAME_FILED = "item_name"
        const val OPERATION_NAME_FILED = "operation_name"
        const val ERROR_MESSAGE_FIELD = "error_message"

        fun createInWork(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                errorMessage =  null,
                operationState = OperationState.RUNNING
            )
        }

        fun createSuccess(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                errorMessage =  null,
                operationState = OperationState.SUCCESS
            )
        }

        fun createFailed(taskId: String, executionId: String, syncObject: SyncObject, operationName: String, errorMessage: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                errorMessage = errorMessage,
                operationState = OperationState.ERROR,
            )
        }

        private fun create(
            taskId: String,
            executionId: String,
            syncObject: SyncObject,
            operationState: OperationState,
            operationName: String,
            errorMessage: String?
        ): SyncObjectLogItem {
            return SyncObjectLogItem(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                objectId = syncObject.id,
                executionId = executionId,
                timestamp = currentTime(),
                itemName = syncObject.name,
                operationName = operationName,
                operationState = operationState,
                isSuccessful = opStateToBoolean(operationState),
                errorMessage = errorMessage,
            )
        }

        private fun opStateToBoolean(operationState: OperationState): Boolean {
            return when(operationState) {
                OperationState.ERROR -> false
                else -> true
            }
        }
    }

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "message", toColumnName = OPERATION_NAME_FILED)
    class RenameColumnMessageToOperationName : AutoMigrationSpec

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "name", toColumnName = ITEM_NAME_FILED)
    class RenameColumnNameToItemName : AutoMigrationSpec
}