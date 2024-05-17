package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.storage_reader.interfaces

interface StorageReader {

    suspend fun read(sourcePath: String)

    suspend fun checkDbObjectsForExistenceAtStorage(targetDirPath: String)
}