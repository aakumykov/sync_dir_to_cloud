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
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.BasicLogItem

@Entity(
    tableName = InstructionLogItem.TABLE_NAME,
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
class InstructionLogItem(
    @PrimaryKey
    val id: String,
    logItemType: LogItemType,
    taskId: String,
    executionId: String,
    message: String?,
    subText: String?,
    timestamp: Long,
)
    : BasicLogItem(
        logItemAbout = LogItemAbout.INSTRUCTION,
        logItemType = logItemType,
        taskId = taskId,
        executionId = executionId,
        message = message,
        subText = subText,
        timestamp = timestamp,
    )
{
    companion object {
        const val TABLE_NAME = "instruction_logs"

        fun create(
            id: String,
            logItemType: LogItemType,
            taskId: String,
            executionId: String,
            message: String?,
            subText: String?,
            timestamp: Long,
        ): InstructionLogItem {
            return InstructionLogItem(
                id = id,
                logItemType = logItemType,
                taskId = taskId,
                executionId = executionId,
                message = message,
                subText = subText,
                timestamp = timestamp,
            )
        }
    }

    override fun toString(): String {
        return "InstructionLogItem(id='$id', logItemType=$logItemType, taskId='$taskId', executionId='$executionId', message='$message', timestamp=$timestamp)"
    }


}