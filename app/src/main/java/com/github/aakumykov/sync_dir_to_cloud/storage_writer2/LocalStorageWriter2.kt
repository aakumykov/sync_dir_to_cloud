package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.sync_dir_to_cloud.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream

// TODO: зачем передавать сюда Creator, м.б. получать его готовым?
class LocalStorageWriter2 @AssistedInject constructor(
    @Assisted private val targetStorageType: StorageType,
    @Assisted private val targetAuthToken: String,
    private val cloudWriterCreator: CloudWriterCreator
): StorageWriter2 {

    override suspend fun createDir(basePath: String, dirName: String): Result<String> {
        return try {
            cloudWriterCreator.createCloudWriter(targetStorageType, targetAuthToken)?.createDir(basePath, dirName)
            return Result.success(dirName)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun putFile(
        sourceFileInputStream: InputStream?,
        targetFilePath: String?,
        overwriteIfExists: Boolean
    ): Result<String> {

        // FIXME: избавиться от "!!"

        return try {
            cloudWriterCreator.createCloudWriter(targetStorageType, targetAuthToken)
                ?.putFile(sourceFileInputStream!!, targetFilePath!!, overwriteIfExists)
            return Result.success(targetFilePath!!)
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    @AssistedFactory
    interface Factory : StorageWriter2_Factory {
        override fun createStorageWriter2(targetStorageType: StorageType, targetAuthToken: String): LocalStorageWriter2
    }
}