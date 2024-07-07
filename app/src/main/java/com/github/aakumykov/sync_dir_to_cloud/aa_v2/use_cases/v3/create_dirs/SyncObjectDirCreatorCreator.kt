package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class SyncObjectDirCreatorCreator @Inject constructor(
    private val cloudWriterCreator: CloudWriterCreator,
    private val cloudAuthReader: CloudAuthReader
) {
    suspend fun createFor(syncTask: SyncTask): SyncObjectDirCreator? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.let { cloudAuth ->
            cloudWriterCreator.createCloudWriter(syncTask.targetStorageType, cloudAuth.authToken)?.let { cloudWriter ->
                SyncObjectDirCreator(cloudWriter)
            }
        }
    }
}