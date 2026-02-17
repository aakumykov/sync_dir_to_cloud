package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.ForeignKey.Companion.NO_ACTION
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem

@Entity(
    tableName = FileOperationLogItem.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = [ "id" ],
            childColumns = [ GlobalConstants.FIELD_TASK_ID ],
            onDelete = CASCADE,
            onUpdate = NO_ACTION,
        )
    ],
    indices = [
        Index(GlobalConstants.FIELD_TASK_ID)
    ]
)
class FileOperationLogItem(
    @PrimaryKey val id: String,
    logItemType: LogItemType,
    taskId: String,
    executionId: String,
    text: String?,
    subText: String?,
    startTime: Long?,
    finishTime: Long?,

    @ColumnInfo(name = "first_item", defaultValue = GlobalConstants.FIELD_CONTENT_NULL)
    val firstItem: String?,

    @ColumnInfo(name = "second_item", defaultValue = GlobalConstants.FIELD_CONTENT_NULL)
    val secondItem: String?,

    @ColumnInfo(name = GlobalConstants.FIELD_JOB_ID, defaultValue = GlobalConstants.FIELD_CONTENT_NULL)
    val jobId: String?
)
    : BasicLogItem(
        logItemAbout = LogItemAbout.FILE,
        logItemType = logItemType,
        taskId = taskId,
        executionId = executionId,
        text = text,
        subText = subText,
        startTime = startTime,
        finishTime = finishTime
    )
{
    companion object {
        fun create(
            id: String,
            logItemType: LogItemType,
            taskId: String,
            executionId: String,
            text: String?,
            subText: String?,
            firstItem: String?,
            secondItem: String?,
            jobId: String?,
            startTime: Long?,
            finishTime: Long?
        )
            : FileOperationLogItem
        {
            return FileOperationLogItem(
                id = id,
                logItemType = logItemType,
                taskId = taskId,
                executionId = executionId,
                text = text,
                subText = subText,
                firstItem = firstItem,
                secondItem = secondItem,
                startTime = startTime,
                finishTime = finishTime,
                jobId = jobId
            )
        }

        const val TABLE_NAME = "file_operation_logs"
    }


}
