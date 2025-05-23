package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.enums.ExecutionLogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.getString
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime

class TaskStateViewHolder : ListHoldingListAdapter.ViewHolder<TaskLogEntry>() {

    private lateinit var titleView: TextView
    private lateinit var primaryStateIcon: ImageView
    private lateinit var secondaryStateIcon: ImageView

    override fun fill(
        taskLogEntry: TaskLogEntry,
        isSelected: Boolean
    ) {
        titleView.text = when(taskLogEntry.entryType) {
            ExecutionLogItemType.START -> startText(taskLogEntry)
            ExecutionLogItemType.FINISH -> finishText(taskLogEntry)
            ExecutionLogItemType.ERROR -> errorText(taskLogEntry)
        }
    }


    private fun startText(taskLogEntry: TaskLogEntry): String {
        return titleView.resources.getString(
            R.string.TASK_STATE_running,
            CurrentDateTime.format(taskLogEntry.startTime)
        )
    }

    private fun finishText(taskLogEntry: TaskLogEntry): String {

        val startTime = CurrentDateTime.format(taskLogEntry.startTime)
        val timeDiff = CurrentDateTime.format(taskLogEntry.finishTime - taskLogEntry.startTime)

        return titleView.resources.getString(
            R.string.TASK_STATE_finished,
            startTime,
            timeDiff
        )
    }

    private fun errorText(taskLogEntry: TaskLogEntry): String {

        val startTime = CurrentDateTime.format(taskLogEntry.startTime)
        val timeDiff = CurrentDateTime.format(taskLogEntry.finishTime - taskLogEntry.startTime)

        return getString(
            R.string.TASK_STATE_error,
            taskLogEntry.errorMsg ?: "-",
            startTime,
            timeDiff
        )
    }


    override fun init(itemView: View) {
        titleView = itemView.findViewById(R.id.titleView)
        primaryStateIcon = itemView.findViewById(R.id.syncLogPrimaryStateIcon)
        secondaryStateIcon = itemView.findViewById(R.id.syncLogSecondaryStateIcon)
    }

    fun getString(@StringRes stringRes: Int, vararg args: Any): String {
        val res = titleView.getString(stringRes, args)
        return res
    }
}