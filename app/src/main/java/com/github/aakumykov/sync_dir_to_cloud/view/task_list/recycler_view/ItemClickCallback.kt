package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

import android.view.View
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface ItemClickCallback {
    fun onProbeRunClicked(taskId: String)
//    fun onProbeRunLongClicked(taskId: String)

    fun onTaskEditClicked(taskId: String)
    fun onTaskRunClicked(taskId: String)
    fun onTaskInfoClicked(taskId: String)
    fun onTaskEnableSwitchClicked(taskId: String)

    fun onTaskMoreButtonClicked(itemView: View, anchorView: View, syncTask: SyncTask)
}