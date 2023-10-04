package com.github.aakumykov.sync_dir_to_cloud.view.task_list.recycler_view

interface ItemClickCallback {
    fun onEditItemClicked(taskId: String)
    fun onRunItemClicked(taskId: String)
    fun onDeleteItemClicked(taskId: String)
    fun onItemInfoClicked(taskId: String)
}