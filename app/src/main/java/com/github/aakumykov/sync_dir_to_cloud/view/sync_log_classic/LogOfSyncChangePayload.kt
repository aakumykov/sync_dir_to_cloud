package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType

data class LogOfSyncChangePayload(
    var isActiveFileOperation: Boolean = false,
    var logItemType: LogItemType? = null,
    var origLogId: String = Constants.ID_STUB,
    var text: String? = null,
    var subText: String? = null,
    var progress: Float? = null,
)