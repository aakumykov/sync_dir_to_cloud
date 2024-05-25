package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import java.io.InputStream
import javax.inject.Inject

class InputStreamSupplier @Inject constructor(
    private val cloudReaderCreator: CloudReaderCreator
) {
    suspend fun getInputStreamFor(storageType: StorageType,
                          authToken: String,
                          absoluteFilePath: String
    ): Result<InputStream>? {
        return cloudReaderCreator
            .createCloudReader(storageType, authToken)
            ?.getFileInputStream(absoluteFilePath)
    }
}