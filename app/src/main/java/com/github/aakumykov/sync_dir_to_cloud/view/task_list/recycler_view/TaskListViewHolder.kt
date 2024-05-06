package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionState
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.view.cloud_auth_list.StorageTypeIconProvider


class TaskListViewHolder(itemView: View, private val itemClickCallback: ItemClickCallback) : RecyclerView.ViewHolder(itemView) {

//    private val probeRunButton: ImageButton = itemView.findViewById(R.id.probeRunButton)

    private val sourceTypeIcon: ImageView
    private val targetTypeIcon: ImageView

    private val sourcePathView: TextView
    private val targetPathView: TextView

    private val titleView: TextView
    private val stateView: ImageView
    private val editButton: View
    private val runButton: ImageButton
    private val moreButton: View
    private val enablingSwitch: SwitchCompat

    private lateinit var currentTask: SyncTask

    init {
        itemView.apply {
            sourceTypeIcon = findViewById(R.id.sourceTypeIcon)
            targetTypeIcon = findViewById(R.id.targetTypeIcon)

            sourcePathView = findViewById(R.id.sourcePath)
            targetPathView = findViewById(R.id.targetPath)

            titleView = findViewById(R.id.titleView)
            stateView = findViewById(R.id.taskStateView)
            editButton = findViewById(R.id.editButton)
            runButton = findViewById(R.id.runButton)
            moreButton = findViewById(R.id.moreButton)
            enablingSwitch = findViewById(R.id.enablingSwitch)
        }

        initButtons()
    }

    private fun initButtons() {
//        probeRunButton.setOnClickListener { itemClickCallback.onProbeRunClicked(currentTask.id) }
//        probeRunButton.setOnLongClickListener { itemClickCallback.onProbeRunLongClicked(currentTask.id); return@setOnLongClickListener true }

        sourceTypeIcon.setOnClickListener { onTaskInfoClicked() }
        targetTypeIcon.setOnClickListener { onTaskInfoClicked() }
        sourcePathView.setOnClickListener { onTaskInfoClicked() }
        targetPathView.setOnClickListener { onTaskInfoClicked() }
        titleView.setOnClickListener { onTaskInfoClicked() }
        stateView.setOnClickListener { onTaskInfoClicked() }

        editButton.setOnClickListener { itemClickCallback.onTaskEditClicked(currentTask.id) }
        runButton.setOnClickListener { itemClickCallback.onTaskRunClicked(currentTask.id) }
        moreButton.setOnClickListener { itemClickCallback.onTaskMoreButtonClicked(itemView, moreButton, currentTask) }

        // Переключается не прямо, а после изменения статуса SyncTask (в БД).
        enablingSwitch.setOnClickListener {
            itemClickCallback.onTaskEnableSwitchClicked(currentTask.id)
        }
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
