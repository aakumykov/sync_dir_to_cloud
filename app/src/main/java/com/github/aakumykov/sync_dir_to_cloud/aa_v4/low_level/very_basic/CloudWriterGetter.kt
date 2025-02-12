package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

/**
 * Возвращает CloudWriter на основе SyncTask
 */
class CloudWriterGetter @Inject constructor(
    private val authReader: CloudAuthReader,
    private val cloudWriterGetter: CloudWriterGetter
) {
    suspend fun getSourceCloudWriter(syncTask: SyncTask): CloudWriter? {
        return cloudWriterGetter.getCloudWriter(
            syncTask.sourceStorageType,
            authReader.getCloudAuth(syncTask.sourceAuthId)?.authToken
        )
    }

    suspend fun getTargetCloudWriter(syncTask: SyncTask): CloudWriter? {
        return cloudWriterGetter.getCloudWriter(
            syncTask.targetStorageType,
            authReader.getCloudAuth(syncTask.targetAuthId)?.authToken
        )
    }
}