package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

interface ItemClickCallback {
    fun onProbeRunClicked(taskId: String)
    fun onProbeRunLongClicked(taskId: String)

    fun onTaskEditClicked(taskId: String)
    fun onTaskRunClicked(taskId: String)
    fun onTaskDeleteClicked(taskId: String)
    fun onTaskInfoClicked(taskId: String)
    fun onTaskEnableSwitchClicked(taskId: String)
}