package com.github.aakumykov.sync_dir_to_cloud.loggers2.entity

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
    tableName = TaskLogItem.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = SyncTask::class,
            parentColumns = ["id"],
            childColumns = [GlobalConstants.FIELD_TASK_ID],
            onDelete = CASCADE,
            onUpdate = NO_ACTION
        )
    ],
    indices = [
        Index(GlobalConstants.FIELD_TASK_ID)
    ]
)
// Не удаляй, это новый класс.
class TaskLogItem(
    @PrimaryKey val id: String,
    taskId: String,
    executionId: String,
    logItemType: LogItemType,
    text: String?,
    subText: String?,
    timestamp: Long,
)
    : BasicLogItem(
        logItemAbout = LogItemAbout.TASK,
        logItemType = logItemType,
        taskId = taskId,
        executionId = executionId,
        text = text,
        subText = subText,
        timestamp = timestamp,
    )
{
    companion object {
        const val TABLE_NAME = "task_logs"

        fun create(
            id: String,
            entryType: LogItemType,
            taskId: String,
            executionId: String,
            text: String?,
            subText: String?
        ) = TaskLogItem(
            id = id,
            logItemType = entryType,
            text = text,
            subText = subText,
            taskId = taskId,
            executionId = executionId,
            timestamp = currentTime,
        )
    }
}