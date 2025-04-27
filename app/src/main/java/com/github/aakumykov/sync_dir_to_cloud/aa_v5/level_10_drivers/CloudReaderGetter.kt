package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReadersHolder
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

/**
 * Возвращает CloudReader на основе SyncTask
 */
class CloudReaderGetter @Inject constructor(
    private val authReader: CloudAuthReader,
    private val cloudReadersHolder: CloudReadersHolder,
) {
    fun getSourceCloudReaderFor(syncTask: SyncTask): CloudReader {
        return cloudReadersHolder.getCloudReader(
            syncTask.sourceStorageType,
            authReader.getCloudAuthBlocking(syncTask.sourceAuthId!!).authToken
        )
    }

    fun getTargetCloudReaderFor(syncTask: SyncTask): CloudReader {
        return cloudReadersHolder.getCloudReader(
            syncTask.targetStorageType,
            authReader.getCloudAuthBlocking(syncTask.targetAuthId!!).authToken
        )
    }
}