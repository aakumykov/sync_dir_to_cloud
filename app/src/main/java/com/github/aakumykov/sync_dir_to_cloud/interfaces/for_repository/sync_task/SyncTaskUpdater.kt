package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_task

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask

interface SyncTaskUpdater {
    fun updateSyncTask(syncTask: SyncTask)

    fun setTargetBackupDirName(taskId: String, dirName: String)
    fun setSourceBackupDirName(taskId: String, dirName: String)

    fun setSourceExecutionBackupDirName(taskId: String, dirName: String)
    fun setTargetExecutionBackupDirName(taskId: String, dirName: String)
}