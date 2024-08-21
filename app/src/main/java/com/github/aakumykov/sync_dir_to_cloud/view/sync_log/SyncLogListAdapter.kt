package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

class SyncLogListAdapter : ListHoldingListAdapter<SyncObjectLogItem, SyncLogViewHolder>(R.layout.sync_log_view_holder) {
    override fun createViewHolder(): ViewHolder<SyncObjectLogItem> {
        return SyncLogViewHolder()
    }
}