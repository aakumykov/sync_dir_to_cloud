package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageButton
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
    private val runButton: ImageButton = itemView.findViewById(R.id.runButton)
    private val moreButton: View = itemView.findViewById(R.id.moreButton)
    private val enablingSwitch: SwitchCompat = itemView.findViewById(R.id.enablingSwitch)

    private lateinit var currentTask: SyncTask

    init {
        editButton.setOnClickListener { itemClickCallback.onTaskEditClicked(currentTask.id) }

        runButton.setOnClickListener { itemClickCallback.onTaskRunClicked(currentTask.id) }

        moreButton.setOnClickListener { Toast.makeText(moreButton.context, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show() }

        // Переключается не прямо, а после изменения статуса SyncTask (в БД).
        enablingSwitch.setOnClickListener {
            itemClickCallback.onTaskEnableSwitchClicked(currentTask.id)
        }
    }

    fun fill(syncTask: SyncTask) {
        currentTask = syncTask
        displayTitle()
        displaySchedulingState()
        displayOpState()
        displayStartStopButton()
    }

    private fun displayStartStopButton() {

        val isEnabled = currentTask.isEnabled
        runButton.isEnabled = isEnabled

        if (currentTask.state == SyncTask.State.RUNNING)
            runButton.setImageResource(R.drawable.ic_task_stop)
        else
            runButton.setImageResource(R.drawable.ic_task_start)

        /*runButton.setImageResource(when(currentTask.state) {
            SyncTask.State.RUNNING -> R.drawable.ic_task_stop
            else -> R.drawable.ic_task_start
        })*/

        val colorForFilter = itemView.context.resources.getColor(when(isEnabled){
            true -> R.color.button_icon_tint_active
            false -> R.color.button_icon_tint_inactive
        })
        runButton.drawable.setColorFilter(colorForFilter, PorterDuff.Mode.MULTIPLY)
    }

    private fun displayTitle() {
        titleView.text = currentTask.getTitle()
    }

    private fun displaySchedulingState() {
        enablingSwitch.isChecked = currentTask.isEnabled
    }

    private fun displayOpState() {
        if (currentTask.isEnabled) {
            stateView.setImageResource(
                when (currentTask.state) {
                    SyncTask.State.IDLE -> R.drawable.ic_task_state_scheduled // FIXME: не то
                    SyncTask.State.RUNNING -> R.drawable.ic_task_state_running
                    SyncTask.State.SUCCESS -> R.drawable.ic_task_state_success
                    SyncTask.State.ERROR -> R.drawable.ic_task_state_error
                }
            )
        }
        else
            stateView.setImageResource(R.drawable.ic_task_state_disabled)
    }
}
