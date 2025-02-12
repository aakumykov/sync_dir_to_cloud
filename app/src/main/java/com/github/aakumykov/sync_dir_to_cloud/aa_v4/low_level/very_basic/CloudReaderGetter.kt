package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

/**
 * Возвращает CloudReader на основе SyncTask
 */
class CloudReaderGetter @Inject constructor(
    private val authReader: CloudAuthReader,
    private val cloudReaderGetter: CloudReaderGetter,
) {
    suspend fun getSourceCloudReaderFor(syncTask: SyncTask): CloudReader? {
        return cloudReaderGetter.getCloudReader(
            syncTask.sourceStorageType,
            authReader.getCloudAuth(syncTask.sourceAuthId)?.authToken
        )
    }

    suspend fun getTargetCloudReaderFor(syncTask: SyncTask): CloudReader? {
        return cloudReaderGetter.getCloudReader(
            syncTask.targetStorageType,
            authReader.getCloudAuth(syncTask.targetAuthId)?.authToken
        )
    }
}