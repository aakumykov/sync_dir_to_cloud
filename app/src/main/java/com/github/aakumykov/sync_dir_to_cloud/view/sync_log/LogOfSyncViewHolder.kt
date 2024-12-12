package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.SyncingOperationCancellationCallback
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

class LogOfSyncViewHolder(
    private val syncingOperationCancellationCallback: SyncingOperationCancellationCallback
) :
    ListHoldingListAdapter.ViewHolder<LogOfSync>()
{
    private lateinit var nameView: TextView
    private lateinit var sizeView: TextView
    private lateinit var messageView: TextView
    private lateinit var stateIconView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var cancelButton: ImageView

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

        cancelButton = itemView.findViewById(R.id.syncOperationCancelButton)
    }

    override fun fill(item: LogOfSync, isSelected: Boolean) {

        nameView.text = item.subText

//        sizeView.text = FileSizeHelper.bytes2size(context, item.size)

        messageView.text = item.text

        stateIconView.setImageResource(when(item.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            OperationState.WAITING -> R.drawable.ic_sync_log_waiting
            OperationState.RUNNING -> R.drawable.ic_sync_log_running
            OperationState.PAUSED -> R.drawable.ic_sync_log_paused
            else -> R.drawable.ic_sync_log_unknown
        })


        item.progress?.also { progressValue: Int ->
            progressBar.apply {
                progress = progressValue
                visibility = View.VISIBLE
            }
        } ?: run {
            progressBar.visibility = View.INVISIBLE
        }


        if (item.isCancelable) {
            when(item.operationState) {
                in arrayOf(OperationState.WAITING, OperationState.RUNNING) -> {
                    cancelButton.visibility = View.VISIBLE

                    cancelButton.setOnClickListener {
                        syncingOperationCancellationCallback.onSyncingOperationCancelButtonClicked(item.operationId)
                    }
                }
                else -> {
                    disableCancelationButton()
                }
            }
        } else {
            disableCancelationButton()
        }
    }

    private fun disableCancelationButton() {
        cancelButton.apply {
            visibility = View.INVISIBLE
            setOnClickListener(null)
        }
    }
}