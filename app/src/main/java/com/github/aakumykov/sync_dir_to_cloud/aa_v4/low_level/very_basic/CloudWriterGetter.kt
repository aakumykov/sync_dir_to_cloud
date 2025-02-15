package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudWritersHolder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

/**
 * Возвращает CloudWriterGetter на основе SyncTask
 */
class CloudWriterGetter @Inject constructor(
    private val authReader: CloudAuthReader,
    private val cloudWritersHolder: CloudWritersHolder
) {
    fun getSourceCloudWriter(syncTask: SyncTask): CloudWriter {
        return cloudWritersHolder.getCloudWriter(
            syncTask.sourceStorageType,
            authReader.getCloudAuthBlocking(syncTask.sourceAuthId!!).authToken
        )
    }

    fun getTargetCloudWriter(syncTask: SyncTask): CloudWriter {
        return cloudWritersHolder.getCloudWriter(
            syncTask.targetStorageType,
            authReader.getCloudAuthBlocking(syncTask.targetAuthId!!).authToken
        )
    }
}