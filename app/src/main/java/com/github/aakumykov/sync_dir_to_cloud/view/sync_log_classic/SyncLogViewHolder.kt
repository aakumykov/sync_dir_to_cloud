package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeGone
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeVisible
import com.github.aakumykov.sync_dir_to_cloud.extensions.toPercentOf100
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

class SyncLogViewHolder(
    itemView: View,
    private val onItemClick: OnSyncLogItemClick
) : RecyclerView.ViewHolder(itemView) {

    private fun initLogItemType(logItemType: LogItemType) {
        itemView.findViewById<ImageView>(R.id.log_of_sync_status_icon).setImageResource(
            when(logItemType) {
                LogItemType.BUSY -> R.drawable.ic_sync_log_busy
                LogItemType.SUCCESS -> R.drawable.ic_sync_log_success
                LogItemType.CANCELLED -> R.drawable.ic_sync_log_cancelled
                LogItemType.ERROR -> R.drawable.ic_sync_log_error
            }
        )
    }

    private fun initText(text: String?) {
        itemView.findViewById<TextView>(R.id.log_of_sync_text).apply {
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
        itemView.findViewById<TextView>(R.id.log_of_sync_sub_text).apply {
            subText?.also {
                text = it
                makeVisible()
            } ?: run {
                this.text = Constants.EMPTY_STRING
                makeGone()
            }
        }
    }

    private fun initProgress(progressValue: Float?) {
        itemView.findViewById<ProgressBar>(R.id.log_of_sync_sub_progress_bar).apply {
            progress = progressValue?.toPercentOf100() ?: 0
        }
    }

    private fun initCancelButton(origLogId: String) {
        itemView.findViewById<ImageButton>(R.id.log_of_sync_stop_button).apply {
            setOnClickListener { onItemClick.invoke(origLogId) }
        }
    }

    fun init(payload: LogOfSyncChangePayload) {
        payload.logItemType?.also { initLogItemType(it) }
        payload.text?.also { initText(it) }
        payload.subText.also { initSubText(it) }
        payload.progress.also { initProgress(it) }
        initCancelButton(payload.origLogId)
    }

    fun init(item: LogOfSync) {
        initLogItemType(item.logItemType)
        initText(item.text)
        initSubText(item.subText)
        initProgress(item.progress)
        initCancelButton(item.origLogId)
    }
}
