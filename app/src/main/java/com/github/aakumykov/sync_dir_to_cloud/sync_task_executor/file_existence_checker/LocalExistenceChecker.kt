package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.file_existence_checker

import com.github.aakumykov.cloud_reader.CloudReader

class LocalExistenceChecker(private val cloudReader: CloudReader) : ExistenceChecker {
    override suspend fun checkFileExists(absolutePath: String): Result<Boolean> {
        return cloudReader.fileExists(absolutePath)
    }
}