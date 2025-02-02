package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncTaskDirObjectReader {
    fun getNewDirs(taskId: String): List<SyncObject>?
    fun getNeverProcessedDirs(taskId: String): List<SyncObject>?
    fun getInTargetLostDirs(taskId: String): List<SyncObject>?

}