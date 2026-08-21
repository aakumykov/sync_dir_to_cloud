package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemAbout
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeGone
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeInvisible
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeVisible
import com.github.aakumykov.sync_dir_to_cloud.extensions.toPercentOf100
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

class SyncLogViewHolder(
    itemView: View,
    private val onItemClicked: (origLogId: String, logItemAbout: LogItemAbout) -> Unit,
    private val onOperationCancellationButtonClicked: (origLogId: String) -> Unit
) : RecyclerView.ViewHolder(itemView) {


    fun init(item: LogOfSync) {
        initLogItemType(item.logItemType)
        initText(item.text)
        initSubText(item.subText)
        initProgress(item.progress, item.isActiveFileOperation)
        initItemClick(item.origLogId, item.logItemAbout)
        initCancelButton(item.origLogId, item.isActiveFileOperation)
    }


    fun initDifferential(payload: LogOfSyncChangePayload) {
        initLogItemType(payload.logItemType)
        initProgress(payload.progress, payload.isActiveFileOperation)
        initCancelButton(payload.origLogId, payload.isActiveFileOperation)
    }


    private fun initLogItemType(logItemType: LogItemType) {
        itemView.findViewById<ImageView>(R.id.log_item_state_icon).setImageResource(
            when(logItemType) {
                LogItemType.BUSY -> R.drawable.ic_sync_log_busy
                LogItemType.SUCCESS -> R.drawable.ic_sync_log_success
                LogItemType.CANCELLED -> R.drawable.ic_sync_log_cancelled
                LogItemType.ERROR -> R.drawable.ic_sync_log_error
            }
        )
    }

    private fun initText(text: String?) {
        itemView.findViewById<TextView>(R.id.log_item_text).apply {
            if (null != text) {
                this.text = text
                makeVisible()
            } else {
                this.text = Constants.EMPTY_STRING
                makeGone()
            }
        }
    }

    private fun initSubText(subText: String?) {
        itemView.findViewById<TextView>(R.id.log_item_sub_text).apply {
            subText?.also {
                text = it
                makeVisible()
            } ?: run {
                this.text = Constants.EMPTY_STRING
                makeGone()
            }
        }
    }

    private fun initProgress(progressValue: Float?, isActiveFileOperation: Boolean) {
        itemView.findViewById<ProgressBar>(R.id.log_item_progress_bar).apply {
            progress = progressValue?.toPercentOf100() ?: 0
            if (isActiveFileOperation) makeVisible() else makeInvisible()
        }
    }

    private fun initItemClick(origLogId: String, logItemAbout: LogItemAbout) {
        itemView.setOnClickListener {
            onItemClicked.invoke(origLogId, logItemAbout)
        }
    }

    private fun initCancelButton(origLogId: String, isActiveFileOperation: Boolean) {
        itemView.findViewById<ImageButton>(R.id.log_item_stop_button).apply {
            setOnClickListener { onOperationCancellationButtonClicked.invoke(origLogId) }
            if (isActiveFileOperation) makeVisible() else makeInvisible()
        }
    }
}
