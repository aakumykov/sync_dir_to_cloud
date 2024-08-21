package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.progress_holder.ProgressHolder
import javax.inject.Inject

class SyncLogListAdapter @Inject constructor(private val progressHolder: ProgressHolder)
    : ListHoldingListAdapter<SyncObjectLogItem, SyncLogViewHolder>(R.layout.sync_log_view_holder) {
    override fun createViewHolder(): ViewHolder<SyncObjectLogItem> {
        return SyncLogViewHolder(progressHolder)
    }
}