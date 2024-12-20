package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.OperationState

class LogOfSyncViewHolderRV(
    private val itemView: View,
//    private val syncLogViewHolderClickCallbacks: SyncLogViewHolderClickCallbacks
) :
    RecyclerView.ViewHolder(itemView)
{
    private val nameView: TextView = itemView.findViewById(R.id.syncLogNameView)
    private val percentView: TextView = itemView.findViewById(R.id.syncLogPercentView)
    private val messageView: TextView = itemView.findViewById(R.id.syncLogOperationNameView)
    private val stateIconView: ImageView = itemView.findViewById(R.id.syncLogStateIconView)
    private val progressBar: ProgressBar = itemView.findViewById<ProgressBar>(R.id.syncLogProgressBar)
    private val cancelButton: ImageView = itemView.findViewById(R.id.syncOperationCancelButton)


    /*fun init(itemView: View) {
        nameView = itemView.findViewById(R.id.syncLogNameView)
        percentView = itemView.findViewById(R.id.syncLogPercentView)
        messageView = itemView.findViewById(R.id.syncLogOperationNameView)
        stateIconView = itemView.findViewById(R.id.syncLogStateIconView)

        progressBar = itemView.findViewById<ProgressBar>(R.id.syncLogProgressBar).apply {
            max = 100
            visibility = View.INVISIBLE
        }

        cancelButton = itemView.findViewById(R.id.syncOperationCancelButton)
    }*/


    fun fill(logOfSync: LogOfSync) {

        nameView.text = logOfSync.subText

        messageView.text = logOfSync.text

        stateIconView.setImageResource(when(logOfSync.operationState){
            OperationState.SUCCESS -> R.drawable.ic_sync_log_success
            OperationState.ERROR -> R.drawable.ic_sync_log_error
            OperationState.WAITING -> R.drawable.ic_sync_log_waiting
            OperationState.RUNNING -> R.drawable.ic_sync_log_running
            OperationState.PAUSED -> R.drawable.ic_sync_log_paused
            else -> R.drawable.ic_sync_log_unknown
        })


        logOfSync.progress?.also { progressValue: Int ->
            progressBar.apply {
                max = 100
                progress = progressValue
                visibility = View.VISIBLE
            }
            percentView.apply {
                text = "(${progressValue}%)"
                visibility = View.VISIBLE
            }
        } ?: run {
            progressBar.visibility = View.INVISIBLE
            percentView.visibility = View.INVISIBLE
        }


        stateIconView.setOnClickListener {
//            syncLogViewHolderClickCallbacks.onSyncLogInfoButtonClicked(logOfSync)
        }


        if (logOfSync.isCancelable) {
            when(logOfSync.operationState) {
                in arrayOf(OperationState.WAITING, OperationState.RUNNING) -> {
                    enableCancelButton(logOfSync.operationId)
                }
                else -> {
                    disableCancelButton()
                }
            }
        } else {
            disableCancelButton()
        }
    }


    private fun enableCancelButton(operationId: String) {
        cancelButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
//                syncLogViewHolderClickCallbacks.onSyncingOperationCancelButtonClicked(operationId)
            }
        }
    }


    private fun disableCancelButton() {
        cancelButton.apply {
            visibility = View.GONE
            setOnClickListener(null)
        }
    }
}