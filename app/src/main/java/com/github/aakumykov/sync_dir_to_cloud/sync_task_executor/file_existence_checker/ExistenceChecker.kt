package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor.file_existence_checker

interface ExistenceChecker {
    suspend fun checkFileExists(absolutePath: String): Result<Boolean>
}