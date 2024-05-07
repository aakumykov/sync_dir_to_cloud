package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.StorageTypeIconProvider


class TaskListViewHolder(itemView: View, private val itemClickCallback: ItemClickCallback) : RecyclerView.ViewHolder(itemView) {

//    private val probeRunButton: ImageButton = itemView.findViewById(R.id.probeRunButton)

    private val listItemView: View

    private val sourceTypeIcon: ImageView
    private val targetTypeIcon: ImageView

    private val sourcePathView: TextView
    private val targetPathView: TextView

    private val schedulingStateView: TextView
    private val stateView: ImageView
    private val editButton: View
    private val runButton: ImageButton
    private val moreButton: View

    private lateinit var currentTask: SyncTask

    init {
        itemView.apply {
            listItemView = findViewById(R.id.taskListItemView)

            sourceTypeIcon = findViewById(R.id.sourceTypeIcon)
            targetTypeIcon = findViewById(R.id.targetTypeIcon)

            sourcePathView = findViewById(R.id.sourcePath)
            targetPathView = findViewById(R.id.targetPath)

            schedulingStateView = findViewById(R.id.schedulingStateView)
            stateView = findViewById(R.id.taskStateView)
            editButton = findViewById(R.id.editButton)
            runButton = findViewById(R.id.runButton)
            moreButton = findViewById(R.id.moreButton)
        }

        initButtons()
    }

    private fun initButtons() {
//        probeRunButton.setOnClickListener { itemClickCallback.onProbeRunClicked(currentTask.id) }
//        probeRunButton.setOnLongClickListener { itemClickCallback.onProbeRunLongClicked(currentTask.id); return@setOnLongClickListener true }

        listItemView.setOnClickListener { onTaskInfoClicked() }
        stateView.setOnClickListener { onTaskInfoClicked() }

        editButton.setOnClickListener { itemClickCallback.onTaskEditClicked(currentTask.id) }
        runButton.setOnClickListener { itemClickCallback.onTaskRunClicked(currentTask.id) }
        moreButton.setOnClickListener { itemClickCallback.onTaskMoreButtonClicked(itemView, moreButton, currentTask) }
    }

    private fun onTaskInfoClicked() {
        itemClickCallback.onTaskInfoClicked(currentTask.id)
    }

    fun fill(syncTask: SyncTask) {

        currentTask = syncTask

        displayStorageTypes()
        displayStoragePaths()
//        displayTitle()
        displaySchedulingState()
        displayExecutionState()
        displayStartStopButton()
    }

    private fun displayStorageTypes() {
        sourceTypeIcon.setImageResource(StorageTypeIconProvider.getIconFor(currentTask.sourceStorageType))
        targetTypeIcon.setImageResource(StorageTypeIconProvider.getIconFor(currentTask.targetStorageType))
    }

    private fun displayStoragePaths() {
        sourcePathView.text = currentTask.sourcePath
        targetPathView.text = currentTask.targetPath
    }


    private fun displayStartStopButton() {

//        MyLogger.d(TAG, "displayStartStopButton(), syncState: ${currentTask.syncState}")

        runButton.setImageResource(
            when(currentTask.executionState){
                ExecutionState.NEVER -> R.drawable.ic_task_start
                ExecutionState.SUCCESS -> R.drawable.ic_task_start
                ExecutionState.RUNNING -> R.drawable.ic_task_stop
                ExecutionState.ERROR -> R.drawable.ic_task_start
            }
        )

        /*val colorForFilter = itemView.context.resources.getColor(when(isEnabled){
            true -> R.color.button_icon_tint_active
            false -> R.color.button_icon_tint_inactive
        })
        runButton.setColorFilter(colorForFilter, PorterDuff.Mode.MULTIPLY)*/
    }

    private fun displaySchedulingState() {
        schedulingStateView.setText(if (currentTask.isEnabled) R.string.task_is_enabled else R.string.task_is_disabled)
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
                ExecutionState.NEVER -> idleIcon()
                ExecutionState.SUCCESS -> idleIcon()
                ExecutionState.RUNNING -> R.drawable.ic_task_state_running
                ExecutionState.ERROR -> R.drawable.ic_task_state_error
            }
        )
    }

    private fun idleIcon(): Int {
        return if (currentTask.isEnabled) R.drawable.ic_task_state_scheduled
        else R.drawable.ic_task_state_disabled
    }

    companion object {
        val TAG: String = TaskListViewHolder::class.java.simpleName
    }
}
