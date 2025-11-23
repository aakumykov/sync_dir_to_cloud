package com.github.aakumykov.sync_dir_to_cloud.view.sync_log.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.github.aakumykov.file_lister_navigator_selector.extensions.hide
import com.github.aakumykov.file_lister_navigator_selector.extensions.visible
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.appComponent
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState
import com.github.aakumykov.sync_dir_to_cloud.utils.syncLogFormattedDateTime
import com.github.aakumykov.sync_dir_to_cloud.view.other.ext_functions.showToast
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isRunning
import kotlinx.coroutines.CancellationException

class SyncLogViewHolder : ListHoldingListAdapter.ViewHolder<LogOfSync>() {

    private lateinit var operationNameView: TextView
    private lateinit var timeView: TextView
    private lateinit var detailsView: TextView
    private lateinit var sizeView: TextView
    private lateinit var stateIconView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var cancelIcon: ImageView

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

        cancelIcon = itemView.findViewById(R.id.syncLogOperationCancelIcon)
    }

    override fun fill(logOfSync: LogOfSync, isSelected: Boolean) {

        operationNameView.text = logOfSync.text

        detailsView.text = logOfSync.subText

//        sizeView.text = FileSizeHelper.bytes2size(context, item.size)

        timeView.text = syncLogFormattedDateTime(logOfSync.timestamp)

        if (logOfSync.isRunning) {
            logOfSync.jobId?.also { theJobId ->
                cancelIcon.setOnClickListener { v ->
                    appComponent.getOperationCancellationHolder().getJob(theJobId)
                        ?.cancel(CancellationException("Отменено пользователем"))
                        ?: context.showToast("Не найден JobId!")
                }
                cancelIcon.visible()

            } ?: run {
                cancelIcon.hide()
            }
        } else {
            cancelIcon.hide()
        }

        stateIconView.setImageResource(when(logOfSync.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            OperationState.CANCELLED -> R.drawable.ic_sync_log_cancelled
            else -> {
                R.drawable.ic_sync_log_waiting
            }
        })

        logOfSync.progress?.also { progressValue: Int ->
            progressBar.apply {
                progress = progressValue
                visibility = View.VISIBLE
            }
        } ?: run {
            progressBar.visibility = View.INVISIBLE
        }
    }
}