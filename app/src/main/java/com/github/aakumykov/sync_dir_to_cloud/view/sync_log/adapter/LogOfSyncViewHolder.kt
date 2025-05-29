package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.syncLogFormattedDateTime

class LogOfSyncViewHolder : ListHoldingListAdapter.ViewHolder<LogOfSync>() {

    private lateinit var operationNameView: TextView
    private lateinit var timeView: TextView
    private lateinit var detailsView: TextView
    private lateinit var sizeView: TextView
    private lateinit var stateIconView: ImageView
    private lateinit var progressBar: ProgressBar

    private val context: Context get() = detailsView.context

    override fun init(itemView: View) {
        operationNameView = itemView.findViewById(R.id.syncLogOperationNameView)
        timeView = itemView.findViewById(R.id.syncLogOperationTimeView)
        detailsView = itemView.findViewById(R.id.syncLogDetailsView)

        sizeView = itemView.findViewById(R.id.syncLogSizeView)
        stateIconView = itemView.findViewById(R.id.syncLogStateIconView)

        progressBar = itemView.findViewById<ProgressBar>(R.id.syncLogProgressBar).apply {
            max = 100
            visibility = View.INVISIBLE
        }
    }

    override fun fill(item: LogOfSync, isSelected: Boolean) {

        operationNameView.text = item.text

        detailsView.text = item.subText

//        sizeView.text = FileSizeHelper.bytes2size(context, item.size)

        timeView.text = syncLogFormattedDateTime(item.timestamp)


        stateIconView.setImageResource(when(item.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            else -> R.drawable.ic_sync_log_waiting
        })

        item.progress?.also { progressValue: Int ->
            progressBar.apply {
                progress = progressValue
                visibility = View.VISIBLE
            }
        } ?: run {
            progressBar.visibility = View.INVISIBLE
        }
    }
}