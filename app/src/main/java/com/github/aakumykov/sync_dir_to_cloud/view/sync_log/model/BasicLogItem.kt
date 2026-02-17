package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model

import androidx.room.ColumnInfo
import com.github.aakumykov.sync_dir_to_cloud.GlobalConstants
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

abstract class BasicLogItem(

    @ColumnInfo(name = FIELD_LOG_ITEM_TYPE)
    val logItemType: LogItemType,

    // FIXME: система заставила сделать это поле var вместо val; почему?
    @ColumnInfo(name = FIELD_LOG_ITEM_ABOUT, defaultValue = "UNKNOWN")
    var logItemAbout: LogItemAbout,

    @ColumnInfo(name = GlobalConstants.FIELD_TASK_ID)
    val taskId: String,

    @ColumnInfo(name = GlobalConstants.FIELD_EXECUTION_ID)
    val executionId: String,

    val text: String?,

    @ColumnInfo(name = FIELD_SUB_TEXT, defaultValue = GlobalConstants.FIELD_CONTENT_NULL)
    val subText: String?,

    @ColumnInfo(name = FIELD_START_TIME)
    val startTime: Long?,

    @ColumnInfo(name = FIELD_FINISH_TIME)
    val finishTime: Long?,
) {
    companion object {
        const val FIELD_LOG_ITEM_TYPE = "log_item_type"
        const val FIELD_LOG_ITEM_ABOUT = "log_item_about"
        const val FIELD_SUB_TEXT = "sub_text"
        const val FIELD_START_TIME = "start_time"
        const val FIELD_FINISH_TIME = "finish_time"
    }
}