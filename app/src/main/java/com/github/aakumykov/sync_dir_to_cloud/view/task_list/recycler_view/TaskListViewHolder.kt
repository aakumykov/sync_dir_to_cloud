package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


class TaskListViewHolder(private val itemView: View, private val itemClickCallback: ItemClickCallback) : RecyclerView.ViewHolder(itemView) {

    private val titleView: TextView = itemView.findViewById(R.id.titleView)
    private val editButton: View = itemView.findViewById(R.id.editButton)
    private val runButton: View = itemView.findViewById(R.id.runButton)

    private lateinit var currentTask: SyncTask

    init {
        editButton.setOnClickListener { itemClickCallback.onEditItemClicked(currentTask.id) }
        runButton.setOnClickListener { itemClickCallback.onRunItemClicked(currentTask.id) }
    }

    fun fill(syncTask: SyncTask) {
        currentTask = syncTask
        titleView.text = currentTask.getTitle()
    }
}
