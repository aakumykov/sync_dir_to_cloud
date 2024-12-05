package com.github.aakumykov.sync_dir_to_cloud

import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.LogOfSync

interface SyncLogViewHolderClickCallbacks {
    fun onSyncLogInfoButtonClicked(logOfSync: LogOfSync)
    fun onSyncingOperationCancelButtonClicked(operationId: String)
}
