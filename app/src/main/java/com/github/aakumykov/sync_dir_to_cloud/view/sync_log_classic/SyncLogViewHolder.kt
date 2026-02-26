package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.toPercentOf100
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

class SyncLogViewHolder(
    itemView: View,
    private val onItemClick: (logOfSync: LogOfSync) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    fun init(item: LogOfSync) {
        itemView.apply {
            findViewById<ImageView>(R.id.log_of_sync_status_icon).setImageResource(
                when(item.logItemType) {
                    LogItemType.BUSY -> R.drawable.ic_sync_log_busy
                    LogItemType.SUCCESS -> R.drawable.ic_sync_log_success
                    LogItemType.CANCELLED -> R.drawable.ic_sync_log_cancelled
                    LogItemType.ERROR -> R.drawable.ic_sync_log_error
                }
            )

            findViewById<TextView>(R.id.log_of_sync_text).text = item.text

            findViewById<TextView>(R.id.log_of_sync_sub_text).apply {
                item.subText?.also {
                    text = it
                    visibility = View.VISIBLE
                } ?: run {
                    visibility = View.GONE
                }
            }

            findViewById<ProgressBar>(R.id.log_of_sync_sub_progress_bar).apply {
                if (item.isActiveFileOperation) {
                    setOnClickListener { onItemClick.invoke(item) }
                    visibility = View.VISIBLE
                    progress = item.progress?.toPercentOf100() ?: 0
                } else {
                    setOnClickListener {  }
                    visibility = View.INVISIBLE
                }
            }


            findViewById<View>(R.id.log_of_sync_stop_button).apply {
                if (item.isActiveFileOperation) {
                    setOnClickListener { onItemClick.invoke(item) }
                    visibility = View.VISIBLE
                } else {
                    setOnClickListener {  }
                    visibility = View.GONE
                }
            }
        }
    }
}
