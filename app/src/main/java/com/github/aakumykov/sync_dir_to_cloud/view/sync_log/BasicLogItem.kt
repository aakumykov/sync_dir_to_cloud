package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.ColumnInfo
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.DbFieldNames
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.FileOperationLogProgressUpdater

abstract class BasicLogItem(

    @ColumnInfo(name = FIELD_LOG_ITEM_TYPE)
    val logItemType: LogItemType,

    // FIXME: система заставила сделать это поле var вместо val; почему?
    @ColumnInfo(name = FIELD_LOG_ITEM_ABOUT, defaultValue = "UNKNOWN")
    var logItemAbout: LogItemAbout,

    @ColumnInfo(name = DbFieldNames.FIELD_TASK_ID)
    val taskId: String,

    @ColumnInfo(name = DbFieldNames.FIELD_EXECUTION_ID)
    val executionId: String,

    @ColumnInfo(name = FIELD_TEXT)
    val text: String?,

    @ColumnInfo(name = FIELD_SUB_TEXT, defaultValue = Constants.STRING_NULL)
    val subText: String?,

    @ColumnInfo(name = FIELD_START_TIME)
    val startTime: Long?,

    @ColumnInfo(name = FIELD_FINISH_TIME)
    val finishTime: Long?,

    /**
     * Это поле доступно только через представление [LogOfSync] ("sync_logs").
     * Устанавливается не при создании объектов, а обновляется позже через
     * интерфейс [FileOperationLogProgressUpdater].
     */
    @ColumnInfo(name = FIELD_PROGRESS, defaultValue = Constants.STRING_NULL)
    var progress: Float? = null,
) {
    companion object {
        const val FIELD_LOG_ID = "log_id"
        const val FIELD_LOG_ITEM_TYPE = "log_item_type"
        const val FIELD_LOG_ITEM_ABOUT = "log_item_about"
        const val FIELD_TEXT = "text"
        const val FIELD_SUB_TEXT = "sub_text"
        const val FIELD_START_TIME = "start_time"
        const val FIELD_FINISH_TIME = "finish_time"
        const val FIELD_PROGRESS = "progress"
    }
}