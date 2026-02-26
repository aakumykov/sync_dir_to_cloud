package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.file_lister_navigator_selector.extensions.visible
import com.github.aakumykov.sync_dir_to_cloud.Constants
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeGone
import com.github.aakumykov.sync_dir_to_cloud.extensions.makeVisible
import com.github.aakumykov.sync_dir_to_cloud.extensions.toPercentOf100
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

class SyncLogViewHolder(
    itemView: View,
    private val onItemClick: (logOfSync: LogOfSync) -> Unit
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

    private fun initCancelButton(logOfSync: LogOfSync) {
        itemView.findViewById<Button>(R.id.log_of_sync_stop_button).apply {
            setOnClickListener { onItemClick.invoke(logOfSync) }
        }
    }

    fun init(changePayload: LogOfSyncChangePayload) {
        changePayload.logItemType?.also { initLogItemType(it) }
        changePayload.text?.also { initText(it) }
        changePayload.subText.also { initSubText(it) }
        changePayload.progress.also { initProgress(it) }
    }

    fun init(item: LogOfSync) {
        initLogItemType(item.logItemType)
        initText(item.text)
        initSubText(item.subText)
        initProgress(item.progress)
        initCancelButton(item)
    }
}
