package com.github.aakumykov.sync_dir_to_cloud.view.task_state

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.ExecutionLogItem
import com.github.aakumykov.sync_dir_to_cloud.extensions.getString
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime

class TaskStateViewHolder : ListHoldingListAdapter.ViewHolder<ExecutionLogItem>() {

    private lateinit var titleView: TextView

    override fun fill(executionLogItem: ExecutionLogItem, isSelected: Boolean) {
        titleView.text = when(executionLogItem.entryType) {
            ExecutionLogItem.EntryType.START -> startText(executionLogItem)
            ExecutionLogItem.EntryType.FINISH -> finishText(executionLogItem)
            ExecutionLogItem.EntryType.ERROR -> errorText(executionLogItem)
        }
    }


    private fun startText(executionLogItem: ExecutionLogItem): String {
        return titleView.resources.getString(
            R.string.TASK_STATE_running,
            CurrentDateTime.format(executionLogItem.startTime)
        )
    }

    private fun finishText(executionLogItem: ExecutionLogItem): String {

        val startTime = CurrentDateTime.format(executionLogItem.startTime)
        val timeDiff = CurrentDateTime.format(executionLogItem.finishTime - executionLogItem.startTime)

        return titleView.resources.getString(
            R.string.TASK_STATE_finished,
            startTime,
            timeDiff
        )
    }

    private fun errorText(executionLogItem: ExecutionLogItem): String {

        val startTime = CurrentDateTime.format(executionLogItem.startTime)
        val timeDiff = CurrentDateTime.format(executionLogItem.finishTime - executionLogItem.startTime)

        return getString(
            R.string.TASK_STATE_error,
            executionLogItem.errorMsg ?: "-",
            startTime,
            timeDiff
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