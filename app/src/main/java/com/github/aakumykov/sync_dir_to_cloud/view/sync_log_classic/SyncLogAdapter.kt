package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

typealias OnSyncLogItemClick = (origLogItem: String) -> Unit

class SyncLogAdapter(
    private val onItemClick: OnSyncLogItemClick
) : ListAdapter<LogOfSync, SyncLogViewHolder>(LogOfSyncDiffer()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SyncLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync_log_item, parent,false)
        return SyncLogViewHolder(view, onItemClick = onItemClick)
    }


    override fun onBindViewHolder(
        holder: SyncLogViewHolder,
        position: Int
    ) {
        holder.init(getItem(position))
    }


    override fun onBindViewHolder(
        holder: SyncLogViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        payloads.lastOrNull()?.also {
            val payload = it as LogOfSyncChangePayload
            payload.logItemType?.also { holder.init(payload) }
        } ?: run {
            onBindViewHolder(holder, position)
        }
    }
}
