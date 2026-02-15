package com.github.aakumykov.sync_dir_to_cloud.interfaces

interface FileOperationJobIdReader {
    suspend fun getJobId(logItemId: String): String?
}
