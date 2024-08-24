package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry
import com.github.aakumykov.sync_dir_to_cloud.extensions.getString
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime

class TaskStateViewHolder : ListHoldingListAdapter.ViewHolder<TaskLogEntry>() {

    private lateinit var titleView: TextView

    override fun fill(taskLogEntry: TaskLogEntry, isSelected: Boolean) {
        titleView.text = when(taskLogEntry.entryType) {
            TaskLogEntry.EntryType.START -> startText(taskLogEntry)
            TaskLogEntry.EntryType.FINISH -> finishText(taskLogEntry)
            TaskLogEntry.EntryType.ERROR -> errorText(taskLogEntry)
        }
    }


    private fun startText(taskLogEntry: TaskLogEntry): String {
        return getString(R.string.TASK_STATE_running, CurrentDateTime.format(taskLogEntry.startTime))
    }

    private fun finishText(taskLogEntry: TaskLogEntry): String {
        val timeDiff = taskLogEntry.finishTime - taskLogEntry.startTime
        return getString(
            R.string.TASK_STATE_finished,
            taskLogEntry.startTime,
            CurrentDateTime.format(timeDiff)
        )
    }

    private fun errorText(taskLogEntry: TaskLogEntry): String {
        val timeDiff = taskLogEntry.finishTime - taskLogEntry.startTime
        return getString(
            R.string.TASK_STATE_error,
            taskLogEntry.errorMsg ?: "-",
            taskLogEntry.startTime,
            CurrentDateTime.format(timeDiff)
        )
    }


    override fun init(itemView: View) {
        titleView = itemView.findViewById(R.id.titleView)
    }

    fun getString(@StringRes stringRes: Int, vararg args: Any): String {
        val res = titleView.getString(stringRes, args)
        return res
    }
}