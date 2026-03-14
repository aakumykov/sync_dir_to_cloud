package com.github.aakumykov.sync_dir_to_cloud.view.task_details

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
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TaskDetailsViewHolder : ListHoldingListAdapter.ViewHolder<TaskLogItem>() {

    private lateinit var titleView: TextView
    private lateinit var primaryStateIcon: ImageView
    private lateinit var secondaryStateIcon: ImageView


    override fun fill(
        item: TaskLogItem,
        isSelected: Boolean
    ) {
        titleView.text = when(item.logItemType) {
            LogItemType.BUSY -> startText(item)
            LogItemType.SUCCESS -> finishText(item)
            LogItemType.ERROR -> errorText(item)
            LogItemType.CANCELLED -> cancelledText(item)
        }
    }


    private fun startText(taskLogItem: TaskLogItem): String {
        return titleView.resources.getString(
            R.string.TASK_STATE_running,
            CurrentDateTime.format(taskLogItem.startTimeMillisOrZero)
        )
    }


    private fun finishText(taskLogItem: TaskLogItem): String {

        val timeDiffMillis = taskLogItem.timeDiffMillis
        val duration: Duration = timeDiffMillis.milliseconds

        val days = duration.inWholeDays % 365
        val hours = duration.inWholeHours % 24
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        val milliseconds = duration.inWholeMilliseconds % 1000

        return titleView.resources.getString(
            R.string.TASK_STATE_finished,
            CurrentDateTime.format(taskLogItem.startTimeMillisOrZero),
            "$days дней, $hours часов, $minutes минут, ${seconds}.${milliseconds} секунд"
        )
    }


    private fun errorText(taskLogItem: TaskLogItem): String {

        val startTime = CurrentDateTime.format(taskLogItem.startTimeMillisOrZero)
        val timeDiff = taskLogItem.timeDiffMillis

        return getString(
            R.string.TASK_STATE_error,
            taskLogItem.subText ?: "-",
            startTime,
            timeDiff
        )
    }

    private fun cancelledText(taskLogItem: TaskLogItem): String {

        val startTime = CurrentDateTime.format(taskLogItem.startTimeMillisOrZero)
        val timeDiff = taskLogItem.timeDiffMillis

        return getString(
            R.string.TASK_STATE_cancelled,
            taskLogItem.subText ?: "-",
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