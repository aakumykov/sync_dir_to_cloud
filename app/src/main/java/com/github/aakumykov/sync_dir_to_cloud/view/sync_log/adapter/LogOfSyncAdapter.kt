package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

class LogOfSyncAdapter : ListHoldingListAdapter<LogOfSync, SyncLogViewHolder>(R.layout.sync_log_view_holder) {
    override fun createViewHolder(): ViewHolder<LogOfSync> {
        return SyncLogViewHolder()
    }
}