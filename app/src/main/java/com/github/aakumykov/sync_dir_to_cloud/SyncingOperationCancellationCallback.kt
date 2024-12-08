package com.github.aakumykov.sync_dir_to_cloud

interface SyncingOperationCancellationCallback {
    fun onSyncingOperationCancelButtonClicked(operationId: String)
}
