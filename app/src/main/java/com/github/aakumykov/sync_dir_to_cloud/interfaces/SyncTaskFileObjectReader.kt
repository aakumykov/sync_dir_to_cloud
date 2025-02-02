package com.github.aakumykov.sync_dir_to_cloud.interfaces

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject

interface SyncTaskFileObjectReader {
    suspend fun getNewFiles(taskId: String): List<SyncObject>?
    fun getForgottenFiles(taskId: String): List<SyncObject>?
    fun getModifiedFiles(taskId: String): List<SyncObject>?
    fun getInTargetLostFiles(taskId: String): List<SyncObject>?
}
