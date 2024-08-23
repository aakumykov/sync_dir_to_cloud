package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DeleteColumn
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import kotlinx.parcelize.Parcelize
import java.util.UUID
import kotlin.random.Random

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
    @ColumnInfo(name = OPERATION_STATE_FIELD, defaultValue = "WAITING") val operationState: OperationState,
    @ColumnInfo(name = PROGRESS_FIELD, defaultValue = "0") val progress: Int,
)
    : Parcelable
{
    val isSuccessful: Boolean get() = OperationState.SUCCESS == operationState

    companion object {

        const val TABLE_NAME = "sync_object_logs"

        const val TASK_ID_FIELD = "task_id"
        const val OBJECT_ID_FIELD = "object_id"
        const val EXECUTION_ID_FIELD = "execution_id"
        const val OPERATION_STATE_FIELD = "operation_state"
        const val TIMESTAMP_FIELD = "timestamp"
        const val ITEM_NAME_FILED = "item_name"
        const val OPERATION_NAME_FILED = "operation_name"
        const val ERROR_MESSAGE_FIELD = "error_message"
        const val PROGRESS_FIELD = "progress"

        fun createWaiting(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                errorMessage =  null,
                operationState = OperationState.WAITING,
                progress = 0
            )
        }

        fun createSuccess(taskId: String, executionId: String, syncObject: SyncObject, operationName: String): SyncObjectLogItem {
            return create(
                taskId = taskId,
                executionId = executionId,
                syncObject = syncObject,
                operationName = operationName,
                errorMessage =  null,
                operationState = OperationState.SUCCESS,
                progress = 0
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
                progress = 0
            )
        }

        private fun create(
            taskId: String,
            executionId: String,
            syncObject: SyncObject,
            operationState: OperationState,
            operationName: String,
            errorMessage: String?,
            progress: Int,
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
                errorMessage = errorMessage,
                progress = progress,
            )
        }
    }

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "message", toColumnName = OPERATION_NAME_FILED)
    class RenameColumnMessageToOperationName : AutoMigrationSpec

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "name", toColumnName = ITEM_NAME_FILED)
    class RenameColumnNameToItemName : AutoMigrationSpec

    @DeleteColumn(tableName = TABLE_NAME, columnName = "is_successful")
    class DeleteColumnIsSuccessful : AutoMigrationSpec

    @DeleteColumn(tableName = TABLE_NAME, columnName = "progress")
    class DeleteColumnProgress : AutoMigrationSpec

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "qwerty", toColumnName = "abc")
    class RenameColumnFromQwertyToAbc : AutoMigrationSpec

    @RenameColumn(tableName = TABLE_NAME, fromColumnName = "progress_as_part_of_100", toColumnName = PROGRESS_FIELD)
    class RenameColumnProgressAsPartOf100ToProgress : AutoMigrationSpec

    @DeleteColumn(tableName = TABLE_NAME, columnName = "abc")
    class DeleteColumnAbc : AutoMigrationSpec


}