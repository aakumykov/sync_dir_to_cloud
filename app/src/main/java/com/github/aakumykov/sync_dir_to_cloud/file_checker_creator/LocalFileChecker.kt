package com.github.aakumykov.sync_dir_to_cloud.file_checker_creator

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File
import java.io.IOException

class LocalFileChecker @AssistedInject constructor(@Assisted notUsedAuthToken: String) : FileChecker {

    override suspend fun fileExists(absolutePath: String): Result<Boolean> {
        return try {
            return Result.success(File(absolutePath).exists())
        }
        catch (e: IOException) {
            Result.failure(e)
        }
    }

    @AssistedFactory
    interface Factory : FileCheckerFactory {
        override fun create(authToken: String): LocalFileChecker {
            return LocalFileChecker(authToken)
        }
    }
}
