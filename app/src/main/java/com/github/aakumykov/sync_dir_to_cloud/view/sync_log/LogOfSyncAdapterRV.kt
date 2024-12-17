package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks

class LogOfSyncAdapterRV(
    private val cancellationCallback: SyncLogViewHolderClickCallbacks
) : ListAdapter<LogOfSync, LogOfSyncViewHolderRV>(LogOfSyncDiffer()) {

    // TODO: List вместо MutableList
//    private val list: MutableList<LogOfSync> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogOfSyncViewHolderRV {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync_log_view_holder, parent, false)
        return LogOfSyncViewHolderRV(view, cancellationCallback).apply {
            init(view)
        }
    }

//    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(viewHolder: LogOfSyncViewHolderRV, position: Int) {
        viewHolder.fill(currentList[position], false)
    }


    class LogOfSyncDiffer : DiffUtil.ItemCallback<LogOfSync>() {

        override fun areItemsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
            return oldItem.operationId == newItem.operationId
        }

        override fun areContentsTheSame(oldItem: LogOfSync, newItem: LogOfSync): Boolean {
            return oldItem.isEqualsWith(newItem)
        }
    }
}