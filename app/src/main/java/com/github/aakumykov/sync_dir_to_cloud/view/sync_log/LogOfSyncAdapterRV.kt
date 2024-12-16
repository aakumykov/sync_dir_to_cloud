package com.github.aakumykov.sync_dir_to_cloud.view.sync_log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.SyncLogViewHolderClickCallbacks

class LogOfSyncAdapterRV(
    private val cancellationCallback: SyncLogViewHolderClickCallbacks
) : RecyclerView.Adapter<LogOfSyncViewHolderRV>() {

    // TODO: List вместо MutableList
    private val list: MutableList<LogOfSync> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogOfSyncViewHolderRV {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sync_log_view_holder, parent, false)
        return LogOfSyncViewHolderRV(view, cancellationCallback).apply {
            init(view)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(viewHolder: LogOfSyncViewHolderRV, position: Int) {
        viewHolder.fill(list[position], false)
    }

    fun setList(newList: List<LogOfSync>) {
        list.apply {
            clear()
            addAll(newList)
            notifyDataSetChanged()
        }
    }
}