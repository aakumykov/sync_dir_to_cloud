package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks

class LogOfSyncAdapterRV(private val callbacks: SyncLogViewHolderClickCallbacks)
    : ListAdapter<LogOfSync, LogOfSyncViewHolderRV>(LogOfSyncDiffer())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogOfSyncViewHolderRV {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync_log_view_holder, parent, false)
        return LogOfSyncViewHolderRV(itemView = view, syncLogViewHolderClickCallbacks = callbacks)
    }

    override fun onBindViewHolder(viewHolder: LogOfSyncViewHolderRV, position: Int) {
        viewHolder.fill(getItem(position))
    }
}