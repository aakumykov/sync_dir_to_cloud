package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.newRandomId
import com.github.aakumykov.sync_dir_to_cloud.utils.currentTime
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync.Companion.BASIC_LOG_ITEM_FIELDS

@DatabaseView(
    viewName = LogOfSync.TABLE_NAME,
    value = "SELECT $BASIC_LOG_ITEM_FIELDS FROM task_logs " +
            "UNION ALL SELECT $BASIC_LOG_ITEM_FIELDS FROM instruction_logs  " +
            "UNION ALL SELECT $BASIC_LOG_ITEM_FIELDS FROM file_operation_logs " +
            "ORDER BY ${BasicLogItem.FIELD_START_TIME}, ${BasicLogItem.FIELD_FINISH_TIME} ASC"
)
data class LogOfSync(
    @Deprecated("Переименовать в origLogItemId")
    @ColumnInfo(name = BasicLogItem.FIELD_LOG_ID)
    val origLogId: String,

    @ColumnInfo(name = BasicLogItem.FIELD_START_TIME)
    val startTime: Long,

    @ColumnInfo(name = BasicLogItem.FIELD_FINISH_TIME)
    val finishTime: Long,

    @ColumnInfo(name = BasicLogItem.FIELD_LOG_ITEM_TYPE)
    val logItemType: LogItemType,

    @ColumnInfo(BasicLogItem.FIELD_LOG_ITEM_ABOUT)
    val logItemAbout: LogItemAbout,

    @ColumnInfo(name = DbFieldNames.FIELD_TASK_ID)
    val taskId: String,

    @ColumnInfo(name = DbFieldNames.FIELD_EXECUTION_ID)
    val executionId: String,

    @ColumnInfo(name = BasicLogItem.FIELD_TEXT)
    val text: String?,

    @ColumnInfo(name = BasicLogItem.FIELD_SUB_TEXT)
    val subText: String?,

    @ColumnInfo(name = BasicLogItem.FIELD_PROGRESS)
    val progress: Float?
) {
    // timestamp здесь не обеспечивает уникальности, так как может быть одинаковым (!)
    val key: String get() = origLogId

    companion object {
        const val TABLE_NAME = "sync_logs"

        const val BASIC_LOG_ITEM_FIELDS =
            "${DbFieldNames.FIELD_ID} as ${BasicLogItem.FIELD_LOG_ID}, " +
            "${BasicLogItem.FIELD_START_TIME}, " +
            "${BasicLogItem.FIELD_FINISH_TIME}, " +
            "${DbFieldNames.FIELD_TASK_ID}, " +
            "${BasicLogItem.FIELD_LOG_ITEM_TYPE}, " +
            "${BasicLogItem.FIELD_LOG_ITEM_ABOUT}, " +
            "${DbFieldNames.FIELD_EXECUTION_ID}, " +
            "${BasicLogItem.FIELD_TEXT}, " +
            "${BasicLogItem.FIELD_SUB_TEXT}, " +
            BasicLogItem.FIELD_PROGRESS

        fun createPreviewStub() = createPreviewStub(LogItemAbout.random)

        fun createPreviewStub(
            logItemAbout: LogItemAbout,
            progress: Float? = 0f
        ) = LogOfSync(
            origLogId = newRandomId,
            startTime = currentTime,
            finishTime = currentTime + 60L,
            logItemType = LogItemType.random,
            logItemAbout = logItemAbout,
            taskId = newRandomId,
            executionId = newRandomId,
            text = "Предпросмотр",
            subText = "Предпросмотр",
            progress = progress
        )
    }

    override fun toString(): String {
        return "LogOfSync(origLogId='$origLogId', startTime=$startTime, finishTime=$finishTime, logItemType=$logItemType, logItemAbout=$logItemAbout, taskId='$taskId', executionId='$executionId', text=$text, subText=$subText, key='$key')"
    }
}

val LogOfSync.isActiveFileOperation: Boolean get() {
    return LogItemType.BUSY == logItemType &&
            LogItemAbout.FILE == logItemAbout
}