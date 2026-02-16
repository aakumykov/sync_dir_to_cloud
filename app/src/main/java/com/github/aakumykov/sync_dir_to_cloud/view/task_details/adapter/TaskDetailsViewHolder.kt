package com.github.aakumykov.sync_dir_to_cloud.view.task_details.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.enums.LogItemType
import com.github.aakumykov.sync_dir_to_cloud.extensions.getString
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem
import com.github.aakumykov.sync_dir_to_cloud.utils.CurrentDateTime

class TaskDetailsViewHolder : ListHoldingListAdapter.ViewHolder<TaskLogItem>() {

    private lateinit var titleView: TextView
    private lateinit var primaryStateIcon: ImageView
    private lateinit var secondaryStateIcon: ImageView


    override fun fill(
        taskLogItem: TaskLogItem,
        isSelected: Boolean
    ) {
        titleView.text = when(taskLogItem.logItemType) {
            LogItemType.BUSY -> startText(taskLogItem)
            LogItemType.SUCCESS -> finishText(taskLogItem)
            LogItemType.ERROR -> errorText(taskLogItem)
            LogItemType.CANCELLED -> cancelledText(taskLogItem)
        }
    }


    private fun startText(taskLogItem: TaskLogItem): String {
        return titleView.resources.getString(
            R.string.TASK_STATE_running,
            CurrentDateTime.format(taskLogItem.timestamp)
        )
    }


    private fun finishText(taskLogItem: TaskLogItem): String {

        val startTime = CurrentDateTime.format(taskLogItem.timestamp)
        val timeDiff = CurrentDateTime.format(taskLogItem.timestamp - taskLogItem.timestamp)

        return titleView.resources.getString(
            R.string.TASK_STATE_finished,
            startTime,
            timeDiff
        )
    }


    private fun cancelledText(taskLogItem: TaskLogItem): String {
        return getString(
            R.string.TASK_STATE_cancelled,
            taskLogItem.text ?: "-",
        )
    }

    private fun errorText(taskLogItem: TaskLogItem): String {

        val startTime = CurrentDateTime.format(taskLogItem.timestamp)
        val timeDiff = CurrentDateTime.format(taskLogItem.timestamp - taskLogItem.timestamp)

        return getString(
            R.string.TASK_STATE_error,
            taskLogItem.text ?: "-",
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