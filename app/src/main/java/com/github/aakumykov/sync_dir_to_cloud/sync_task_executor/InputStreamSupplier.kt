package com.github.aakumykov.sync_dir_to_cloud.sync_task_executor

import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderLocator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream
import javax.inject.Inject

class InputStreamSupplier @AssistedInject constructor(
    @Assisted private val authToken: String,
    @Assisted private val storageType: StorageType,
    private val cloudReaderLocator: CloudReaderLocator
) {
    suspend fun getInputStreamFor(absoluteFilePath: String): Result<InputStream>? {
        return cloudReaderLocator
                .getCloudReader(storageType, authToken)
                ?.getFileInputStream(absoluteFilePath)
    }

    @AssistedFactory
    interface Factory {
        fun create(authToken: String?, storageType: StorageType?): InputStreamSupplier?
    }

    class Creator @Inject constructor(
        private val factory: Factory,
        private val cloudAuthReader: CloudAuthReader
    ) {
        suspend fun create(syncTask: SyncTask): InputStreamSupplier? {
            return factory.create(
                cloudAuthReader.getCloudAuth(syncTask.sourceAuthId)?.authToken,
                syncTask.sourceStorageType
            )
        }
    }
}