package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask


class TaskListViewHolder(private val itemView: View, private val itemClickCallback: ItemClickCallback) : RecyclerView.ViewHolder(itemView) {

    private val titleView: TextView = itemView.findViewById(R.id.titleView)
    private val stateView: ImageView = itemView.findViewById(R.id.taskStateView)
    private val editButton: View = itemView.findViewById(R.id.editButton)
    private val runButton: View = itemView.findViewById(R.id.runButton)
    private val moreButton: View = itemView.findViewById(R.id.moreButton)
    private val enablingSwitch: SwitchCompat = itemView.findViewById(R.id.enablingSwitch)

    private lateinit var currentTask: SyncTask

    init {
        editButton.setOnClickListener { itemClickCallback.onTaskEditClicked(currentTask.id) }
        runButton.setOnClickListener { itemClickCallback.onTaskRunClicked(currentTask.id) }
        moreButton.setOnClickListener { Toast.makeText(moreButton.context, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show() }
        enablingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            itemClickCallback.onTaskEnableSwitchToggled(currentTask.id, isChecked) }
    }

    fun fill(syncTask: SyncTask) {
        currentTask = syncTask
        displayTitle()
        displayState()
    }

    private fun displayTitle() {
        titleView.text = currentTask.getTitle()
    }

    private fun displayState() {
        stateView.setImageResource(
            when(currentTask.state) {
                SyncTask.State.DISABLED -> R.drawable.ic_task_state_disabled
                SyncTask.State.ENABLED -> R.drawable.ic_task_state_scheduled
                SyncTask.State.RUNNING -> R.drawable.ic_task_state_running
                SyncTask.State.SUCCESS -> R.drawable.ic_task_state_success
                SyncTask.State.ERROR -> R.drawable.ic_task_state_error
            }
        )
    }
}
