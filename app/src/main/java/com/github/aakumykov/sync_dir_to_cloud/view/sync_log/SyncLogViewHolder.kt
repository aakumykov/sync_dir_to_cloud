package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem

class SyncLogViewHolder : ListHoldingListAdapter.ViewHolder<SyncObjectLogItem>() {

    private lateinit var nameView: TextView
    private lateinit var messageView: TextView
    private lateinit var stateIconView: ImageView

    override fun init(itemView: View) {
        nameView = itemView.findViewById(R.id.syncLogNameView)
        messageView = itemView.findViewById(R.id.syncLogMessageView)
        stateIconView = itemView.findViewById(R.id.syncLogStateIconView)
    }

    override fun fill(item: SyncObjectLogItem, isSelected: Boolean) {
        nameView.text = item.name
        messageView.text = item.message
        stateIconView.setImageResource(if (item.isSuccessful) R.drawable.ic_sync_log_success else R.drawable.ic_sync_log_error)
    }
}