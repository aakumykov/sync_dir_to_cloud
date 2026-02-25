package com.github.aakumykov.sync_dir_to_cloud.view.sync_log_classic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.view.sync_log.model.LogOfSync

class SyncLogAdapter(
    private val onItemClick: (logOfSync: LogOfSync) -> Unit
) : RecyclerView.Adapter<SyncLogViewHolder>() {

    private val list: MutableList<LogOfSync> = mutableListOf()

    fun setList(list: List<LogOfSync>) {
        this.list.addAll(list)
        notifyItemRangeChanged(0, list.size)
    }

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
        holder.init(list[position])
    }

    override fun getItemCount(): Int = list.size

}
