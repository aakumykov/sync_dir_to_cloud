package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


class TaskListAdapter(private val itemClickCallback: ItemClickCallback)
    : ListAdapter<SyncTask, TaskListViewHolder>(SyncTaskDiffer())
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent,false)
        return TaskListViewHolder(view, itemClickCallback)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.fill(getItem(position))
    }
}

class SyncTaskDiffer : DiffUtil.ItemCallback<SyncTask>() {

    override fun areItemsTheSame(oldItem: SyncTask, newItem: SyncTask): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SyncTask, newItem: SyncTask): Boolean {
        return oldItem.sourcePath == newItem.sourcePath &&
                oldItem.targetPath == newItem.targetAuthId &&
                oldItem.sourceStorageType == newItem.sourceStorageType &&
                oldItem.targetStorageType == newItem.targetStorageType &&
                oldItem.isEnabled == newItem.isEnabled &&
                oldItem.state == newItem.state &&
                oldItem.executionState == newItem.executionState &&
                oldItem.schedulingState == newItem.schedulingState
    }

    /*override fun getChangePayload(oldItem: SyncTask, newItem: SyncTask): Any? {
        return super.getChangePayload(oldItem, newItem)
    }*/
}