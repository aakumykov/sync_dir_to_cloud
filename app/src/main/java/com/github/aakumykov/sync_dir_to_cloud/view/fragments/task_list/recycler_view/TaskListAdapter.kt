package com.github.aakumykov.sync_dir_to_cloud.view.fragments.task_list.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

class TaskListAdapter : RecyclerView.Adapter<TaskListViewHolder>() {

    private val list: MutableList<SyncTask> = mutableListOf()

    fun setList(newList: List<SyncTask>) {
        list.clear()
        list.addAll(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        return TaskListViewHolder(parent.inflateView(R.layout.task_list_item))
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.fill(list.get(position))
    }

    override fun getItemCount(): Int = list.size

    private fun ViewGroup.inflateView(@LayoutRes res: Int): View {
        return LayoutInflater.from(context).inflate(res, this, false)
    }
}