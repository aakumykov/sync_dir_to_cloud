package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

interface ItemClickCallback {
    fun onTaskEditClicked(taskId: String)
    fun onTaskRunClicked(taskId: String)
    fun onTaskDeleteClicked(taskId: String, title: String)
    fun onTaskInfoClicked(taskId: String)
    fun onTaskEnableSwitchClicked(taskId: String)
}