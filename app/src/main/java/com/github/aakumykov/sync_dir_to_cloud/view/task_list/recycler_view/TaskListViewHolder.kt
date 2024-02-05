package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SimpleState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.utils.MyLogger


class TaskListViewHolder(private val itemView: View, private val itemClickCallback: ItemClickCallback) : RecyclerView.ViewHolder(itemView) {

//    private val probeRunButton: ImageButton = itemView.findViewById(R.id.probeRunButton)

    private val titleView: TextView = itemView.findViewById(R.id.titleView)
    private val stateView: ImageView = itemView.findViewById(R.id.taskStateView)
    private val editButton: View = itemView.findViewById(R.id.editButton)
    private val runButton: ImageButton = itemView.findViewById(R.id.runButton)
    private val moreButton: View = itemView.findViewById(R.id.moreButton)
    private val enablingSwitch: SwitchCompat = itemView.findViewById(R.id.enablingSwitch)

    private lateinit var currentTask: SyncTask

    init {
//        probeRunButton.setOnClickListener { itemClickCallback.onProbeRunClicked(currentTask.id) }
//        probeRunButton.setOnLongClickListener { itemClickCallback.onProbeRunLongClicked(currentTask.id); return@setOnLongClickListener true }

        stateView.setOnClickListener { itemClickCallback.onTaskInfoClicked(currentTask.id) }
        editButton.setOnClickListener { itemClickCallback.onTaskEditClicked(currentTask.id) }
        runButton.setOnClickListener { itemClickCallback.onTaskRunClicked(currentTask.id) }

        moreButton.setOnClickListener { itemClickCallback.onTaskMoreButtonClicked(itemView, moreButton, currentTask) }

        // Переключается не прямо, а после изменения статуса SyncTask (в БД).
        enablingSwitch.setOnClickListener {
            itemClickCallback.onTaskEnableSwitchClicked(currentTask.id)
        }
    }

    fun fill(syncTask: SyncTask) {
        currentTask = syncTask
        displayTitle()
        displaySchedulingState()
        displayExecutionState()
        displayStartStopButton()
    }


    private fun displayStartStopButton() {

        MyLogger.d(TAG, "displayStartStopButton(), executionState: ${currentTask.executionState}")

        runButton.setImageResource(
            when(currentTask.executionState){
                SimpleState.IDLE -> R.drawable.ic_task_start
                SimpleState.RUNNING -> R.drawable.ic_task_stop
                SimpleState.ERROR -> R.drawable.ic_task_start
            }
        )

        /*val colorForFilter = itemView.context.resources.getColor(when(isEnabled){
            true -> R.color.button_icon_tint_active
            false -> R.color.button_icon_tint_inactive
        })
        runButton.setColorFilter(colorForFilter, PorterDuff.Mode.MULTIPLY)*/
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
                    SyncTask.State.SCHEDULING_ERROR -> R.drawable.ic_task_state_scheduling_error
                    SyncTask.State.READING_SOURCE -> R.drawable.ic_task_state_running
                    SyncTask.State.WRITING_TARGET -> R.drawable.ic_task_state_running
                    SyncTask.State.SUCCESS -> R.drawable.ic_task_state_success
                    SyncTask.State.SEMI_SUCCESS -> R.drawable.ic_task_state_semi_success
                    SyncTask.State.EXECUTION_ERROR -> R.drawable.ic_task_state_error
                }
            )
        }
        else
            stateView.setImageResource(R.drawable.ic_task_state_disabled)
    }

    private fun displayExecutionState() {
        stateView.setImageResource(
            when (currentTask.executionState) {
                SimpleState.IDLE -> if (currentTask.isEnabled) R.drawable.ic_task_state_scheduled else R.drawable.ic_task_state_disabled
                SimpleState.RUNNING -> R.drawable.ic_task_state_running
                SimpleState.ERROR -> R.drawable.ic_task_state_error
            }
        )
    }

    companion object {
        val TAG: String = TaskListViewHolder::class.java.simpleName
    }
}
