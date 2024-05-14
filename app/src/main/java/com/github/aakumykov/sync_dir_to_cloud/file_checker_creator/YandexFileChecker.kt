package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class YandexFileChecker @AssistedInject constructor(
    @Assisted private val authToken: String,
    private val cloudReaderCreator: CloudReaderCreator
) : FileChecker {

    override suspend fun fileExists(absolutePath: String): Result<Boolean>? {
        return cloudReaderCreator.createCloudReader(StorageType.YANDEX_DISK)?.fileExists(absolutePath)
    }

    @AssistedFactory
    interface Factory : FileCheckerFactory {
        override fun create(authToken: String): YandexFileChecker {
            return YandexFileChecker(authToken)
        }
    }
}
