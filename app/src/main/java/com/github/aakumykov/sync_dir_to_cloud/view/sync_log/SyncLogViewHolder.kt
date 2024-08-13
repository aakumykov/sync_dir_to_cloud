package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.View
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

class SyncLogViewHolder : ListHoldingListAdapter.ViewHolder<SyncObjectLogItem>() {

    private lateinit var titleView: TextView;

    override fun init(itemView: View) {
        titleView = itemView.findViewById(R.id.syncLogTitleView)
    }

    override fun fill(item: SyncObjectLogItem, isSelected: Boolean) {
        titleView.text = "${item.name} [${item.message}]"
    }
}