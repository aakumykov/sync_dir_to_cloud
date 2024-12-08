package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.SyncingOperationCancellationCallback
import com.github.aakumykov.sync_dir_to_cloud.R

class LogOfSyncAdapter(private val syncingOperationCancellationCallback: SyncingOperationCancellationCallback) :
    ListHoldingListAdapter<LogOfSync, LogOfSyncViewHolder>(R.layout.sync_log_view_holder) {

    override fun createViewHolder(): ViewHolder<LogOfSync> {
        return LogOfSyncViewHolder(syncingOperationCancellationCallback)
    }
}