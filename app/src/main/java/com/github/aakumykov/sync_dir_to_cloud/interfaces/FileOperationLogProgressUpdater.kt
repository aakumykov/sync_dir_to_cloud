package com.github.aakumykov.sync_dir_to_cloud.interfaces

interface FileOperationLogProgressUpdater {
    suspend fun updateProgress(logItemId: String, progress: Float)
}
