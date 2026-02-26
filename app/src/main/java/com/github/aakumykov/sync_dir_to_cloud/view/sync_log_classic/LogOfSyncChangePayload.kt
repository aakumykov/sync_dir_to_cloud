package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

data class LogOfSyncChangePayload(
    var isActiveFileOperation: Boolean = false,
    var progress: Float? = null,
)