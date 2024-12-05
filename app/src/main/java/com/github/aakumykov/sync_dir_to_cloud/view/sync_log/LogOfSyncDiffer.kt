package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import androidx.recyclerview.widget.DiffUtil

class LogOfSyncDiffer : DiffUtil.ItemCallback<LogOfSync>() {

    override fun areItemsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
        return oldItem.operationId == newItem.operationId &&
                oldItem.text == newItem.text &&
                oldItem.subText == newItem.subText
    }

    override fun areContentsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
        return oldItem.text == newItem.text &&
                oldItem.subText == newItem.subText &&
                oldItem.timestamp == newItem.timestamp &&
                oldItem.operationState == newItem.operationState &&
                oldItem.errorMessage == newItem.errorMessage &&
                oldItem.progress == newItem.progress &&
                oldItem.isCancelable == newItem.isCancelable &&
                oldItem.operationId == newItem.operationId
    }

    override fun getChangePayload(oldItem: LogOfSync, newItem: LogOfSync): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}