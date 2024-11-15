package com.github.aakumykov.sync_dir_to_cloud.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry.Companion
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import java.util.Date
import java.util.UUID

@Entity(
    tableName = ExecutionLogItem.TABLE_NAME,
    indices = [
        /*Index(value = [
                ExecutionLogItem.TASK_ID_FIELD_NAME,
                ExecutionLogItem.EXECUTION_ID_FIELD_NAME,
                ExecutionLogItem.TIMESTAMP_FIELD_NAME,
                ExecutionLogItem.TYPE_FIELD_NAME,
            ],
            unique = true
        )*/
    ]
)
class ExecutionLogItem(
    @PrimaryKey val id: String,
    @ColumnInfo(name = TASK_ID_FIELD_NAME) val taskId: String,
    @ColumnInfo(name = EXECUTION_ID_FIELD_NAME) val executionId: String,
    @ColumnInfo(name = TIMESTAMP_FIELD_NAME) val timestamp: Long,
    @ColumnInfo(name = TYPE_FIELD_NAME) val type: ExecutionLogItemType,
    val message: String,
) {
    companion object {

        fun createStartingItem(taskId: String,
                            executionId: String,
                            message: String,
        ): ExecutionLogItem = create(
            taskId = taskId,
            executionId = executionId,
            itemType = ExecutionLogItemType.START,
            message = message,
        )


        fun createFinishingItem(taskId: String,
                                executionId: String,
                                message: String,
        ): ExecutionLogItem = create(
            taskId = taskId,
            executionId = executionId,
            itemType = ExecutionLogItemType.FINISH,
            message = message,
        )


        fun createErrorItem(taskId: String,
                                executionId: String,
                                message: String,
        ): ExecutionLogItem = create(
            taskId = taskId,
            executionId = executionId,
            itemType = ExecutionLogItemType.ERROR,
            message = message,
        )


        private fun create(taskId: String,
                           executionId: String,
                           itemType: ExecutionLogItemType,
                           message: String,
        ): ExecutionLogItem = ExecutionLogItem(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            executionId = executionId,
            timestamp = Date().time,
            type = itemType,
            message = message,
        )

        const val TABLE_NAME = "execution_log"
        const val TASK_ID_FIELD_NAME = "task_id"
        const val EXECUTION_ID_FIELD_NAME = "execution_id"
        const val TIMESTAMP_FIELD_NAME = "timestamp"
        const val TYPE_FIELD_NAME = "type"
    }

}