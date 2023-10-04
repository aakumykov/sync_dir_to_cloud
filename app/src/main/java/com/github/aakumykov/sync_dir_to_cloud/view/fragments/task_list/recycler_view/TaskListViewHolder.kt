package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list.recycler_view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleView: TextView

    init {
        titleView = itemView.findViewById(R.id.titleView)
    }

    fun fill(syncTask: SyncTask) {
        titleView.text = syncTask.id
    }
}
