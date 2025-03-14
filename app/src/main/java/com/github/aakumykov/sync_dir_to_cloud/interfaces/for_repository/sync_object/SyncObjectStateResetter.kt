package com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.sync_object


interface SyncObjectStateResetter {

    // TODO: вынести в отдельный интерфейс
    suspend fun markAllObjectsAsNotChecked(taskId: String)

    suspend fun resetTargetReadingBadState(taskId: String)
    suspend fun resetBackupBadState(taskId: String)
    suspend fun resetDeletionBadState(taskId: String)
    suspend fun resetRestorationBadState(taskId: String)
    suspend fun resetSyncBadState(taskId: String)
    suspend fun markAllNotCheckedObjectsAsDeleted(taskId: String)
}