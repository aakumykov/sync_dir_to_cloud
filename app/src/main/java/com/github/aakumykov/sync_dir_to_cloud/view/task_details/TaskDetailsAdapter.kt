package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.loggers2.entity.TaskLogItem

class TaskDetailsAdapter : ListHoldingListAdapter<TaskLogItem, TaskDetailsViewHolder>(
    R.layout.task_state_item
) {
    override fun createViewHolder(): ViewHolder<TaskLogItem> {
        return TaskDetailsViewHolder()
    }
}