package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


class TaskListAdapter : RecyclerView.Adapter<TaskListViewHolder>() {

    private val list: MutableList<SyncTask> = mutableListOf()

    fun setList(newList: List<SyncTask>) {
        list.clear()
        list.addAll(newList)
        notifyItemRangeChanged(0, newList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent,false)
        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.fill(list.get(position))
    }

    override fun getItemCount(): Int = list.size
}