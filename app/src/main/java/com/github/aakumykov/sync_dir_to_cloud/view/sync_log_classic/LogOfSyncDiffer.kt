package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.R.attr.text
import androidx.recyclerview.widget.DiffUtil
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.isActiveFileOperation

class LogOfSyncDiffer()  : DiffUtil.ItemCallback<LogOfSync>() {

    override fun areItemsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
        val isTheSame = oldItem.origLogId == newItem.origLogId
        return isTheSame
    }

    override fun areContentsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {

        val areTypeEquals = oldItem.logItemType == newItem.logItemType
        val areTextEquals = oldItem.text == newItem.text
        val areSubTextEquals = oldItem.subText == newItem.subText
        val areProgressEquals = oldItem.progress == newItem.progress

        return areTypeEquals &&
                areTextEquals &&
                areSubTextEquals &&
                areProgressEquals
    }

    override fun getChangePayload(oldItem: LogOfSync, newItem: LogOfSync): Any? {
        val changePayload = LogOfSyncChangePayload(
            logItemType = newItem.logItemType,
            origLogId = oldItem.origLogId,
        ).apply {
            isActiveFileOperation = newItem.isActiveFileOperation
            progress = newItem.progress
        }

        return if (oldItem.hashCode() != newItem.hashCode()) {
            changePayload
        }
        else {
            super.getChangePayload(oldItem, newItem)
        }
    }
}