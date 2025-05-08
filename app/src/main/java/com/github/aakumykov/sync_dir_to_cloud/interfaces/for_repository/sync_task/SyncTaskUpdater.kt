package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskUpdater {
    fun updateSyncTask(syncTask: SyncTask)
    fun setTargetBackupDir(taskId: String, dirName: String)
    fun setSourceBackupDir(taskId: String, dirName: String)
    fun setSourceExecutionBackupDir(taskId: String, dirName: String)
    fun setTargetExecutionBackupDir(taskId: String, dirName: String)
}