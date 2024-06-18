package com.github.aakumykov.sync_dir_to_cloud.storage_writer2

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.cloud_writer.stripMultiSlashes
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.factories.cloud_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream

class YandexDiskStorageWriter2 @AssistedInject constructor(
    @Assisted private val targetStorageType: StorageType,
    @Assisted private val targetAuthToken: String,
    private val cloudWriterCreator: CloudWriterCreator
): StorageWriter2 {

    private val cloudWriter: CloudWriter? get() = cloudWriterCreator.createCloudWriter(targetStorageType, targetAuthToken)

    override suspend fun createDir(basePath: String, dirName: String): Result<String> {
        return try {
            cloudWriter?.createDir(basePath, dirName)
            Result.success((basePath + FSItem.DS + dirName).stripMultiSlashes())
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun putFile(
        sourceFileInputStream: InputStream?,
        targetFilePath: String?,
        overwriteIfExists: Boolean
    ): Result<String> {
        return try {
            cloudWriter?.putFile(sourceFileInputStream!!, targetFilePath!!, overwriteIfExists)
            Result.success(targetFilePath!!)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }


    @AssistedFactory
    interface Factory : StorageWriter2_Factory {
        override fun createStorageWriter2(targetStorageType: StorageType?, targetAuthToken: String?): YandexDiskStorageWriter2
    }
}
