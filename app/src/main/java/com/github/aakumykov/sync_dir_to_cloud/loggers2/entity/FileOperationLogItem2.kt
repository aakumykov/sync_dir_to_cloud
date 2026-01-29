package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime

@Entity(
    tableName = FileOperationLogItem2.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = [ "id" ],
            childColumns = [ GlobalConstants.FIELD_TASK_ID ],
            onDelete = CASCADE,
            onUpdate = NO_ACTION,
        )
    ]
)
data class FileOperationLogItem2(
    @PrimaryKey val id: String,

    @ColumnInfo(name = GlobalConstants.FIELD_LOG_ITEM_TYPE)
    val logItemType: LogItemType,

    @ColumnInfo(name = GlobalConstants.FIELD_TASK_ID)
    val taskId: String,

    @ColumnInfo(name = GlobalConstants.FIELD_EXECUTION_ID)
    val executionId: String,

    val message: String,

    @ColumnInfo(defaultValue = "")
    val firstItem: String,

    @ColumnInfo(defaultValue = "null")
    val secondItem: String?,

    val timestamp: Long,

    @ColumnInfo(
        name = GlobalConstants.FIELD_JOB_ID,
        defaultValue = GlobalConstants.FIELD_CONTENT_NULL)
    val jobId: String?
) {
    companion object {
        fun create(
            logItemType: LogItemType,
            taskId: String,
            executionId: String,
            message: String,
            firstItem: String,
            secondItem: String?,
            jobId: String?
        )
            : FileOperationLogItem2
        {
            return FileOperationLogItem2(
                id = newRandomId,
                logItemType = logItemType,
                taskId = taskId,
                executionId = executionId,
                message = message,
                firstItem = firstItem,
                secondItem = secondItem,
                timestamp = currentTime,
                jobId = jobId
            )
        }

        const val TABLE_NAME = "file_operation_logs_2"
    }
}
