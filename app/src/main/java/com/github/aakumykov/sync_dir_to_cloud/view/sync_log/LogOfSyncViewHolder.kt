package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.FileSizeHelper

class LogOfSyncViewHolder : ListHoldingListAdapter.ViewHolder<LogOfSync>() {

    private lateinit var nameView: TextView
    private lateinit var sizeView: TextView
    private lateinit var messageView: TextView
    private lateinit var stateIconView: ImageView
    private lateinit var progressBar: ProgressBar

    private val context: Context get() = nameView.context

    override fun init(itemView: View) {
        nameView = itemView.findViewById(R.id.syncLogNameView)
        sizeView = itemView.findViewById(R.id.syncLogSizeView)
        messageView = itemView.findViewById(R.id.syncLogMessageView)
        stateIconView = itemView.findViewById(R.id.syncLogStateIconView)

        progressBar = itemView.findViewById<ProgressBar>(R.id.syncLogProgressBar).apply {
            max = 100
            visibility = View.INVISIBLE
        }
    }

    override fun fill(item: LogOfSync, isSelected: Boolean) {

        nameView.text = item.subText

//        sizeView.text = FileSizeHelper.bytes2size(context, item.size)

        messageView.text = item.text

        stateIconView.setImageResource(when(item.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            else -> R.drawable.ic_sync_log_waiting
        })

        /*progressBar.apply {
            progress = item.progress ?: 0
            visibility = View.VISIBLE
        }*/
    }
}