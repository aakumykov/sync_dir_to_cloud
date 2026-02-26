package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

data class LogOfSyncChangePayload(
    var logItemType: LogItemType,
    var origLogId: String,
    var isActiveFileOperation: Boolean = false,
    var progress: Float? = null,
)