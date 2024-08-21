package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObjectLogItem
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.progress_holder.ProgressHolder

class SyncLogViewHolder(progressHolder: ProgressHolder) : ListHoldingListAdapter.ViewHolder<SyncObjectLogItem>() {

    private lateinit var nameView: TextView
    private lateinit var messageView: TextView
    private lateinit var stateIconView: ImageView

    override fun init(itemView: View) {
        nameView = itemView.findViewById(R.id.syncLogNameView)
        messageView = itemView.findViewById(R.id.syncLogMessageView)
        stateIconView = itemView.findViewById(R.id.syncLogStateIconView)
    }

    override fun fill(item: SyncObjectLogItem, isSelected: Boolean) {
        nameView.text = item.itemName
        messageView.text = item.operationName
        stateIconView.setImageResource(when(item.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            else -> R.drawable.ic_sync_log_busy
        })
    }
}