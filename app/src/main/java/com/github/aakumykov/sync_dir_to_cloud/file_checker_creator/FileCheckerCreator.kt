package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType

class FileCheckerCreator(private val map: Map<StorageType,FileChecker>) {

    fun createFileChecker(storageType: StorageType): FileChecker {

        return when(storageType) {
            StorageType.LOCAL -> LocalFileChecker()
            StorageType.YANDEX_DISK -> YandexFileChecker()
        }
    }
}