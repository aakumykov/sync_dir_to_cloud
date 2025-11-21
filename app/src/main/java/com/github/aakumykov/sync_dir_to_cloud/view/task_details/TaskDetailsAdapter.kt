package com.github.aakumykov.sync_dir_to_cloud.view.task_details

import com.github.aakumykov.list_holding_list_adapter.ListHoldingListAdapter
import com.github.aakumykov.sync_dir_to_cloud.R
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.TaskLogEntry

class TaskDetailsAdapter : ListHoldingListAdapter<TaskLogEntry, TaskDetailsViewHolder>(
    R.layout.task_state_item
) {
    override fun createViewHolder(): ViewHolder<TaskLogEntry> {
        return TaskDetailsViewHolder()
    }
}