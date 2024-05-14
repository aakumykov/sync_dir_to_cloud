package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

interface FileCheckerFactory {
    fun create(authToken: String): FileChecker
}
