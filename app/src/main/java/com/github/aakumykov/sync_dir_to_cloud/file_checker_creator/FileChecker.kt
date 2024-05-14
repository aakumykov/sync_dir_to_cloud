package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

interface FileChecker {
    suspend fun fileExists(absolutePath: String): Result<Boolean>?
}
