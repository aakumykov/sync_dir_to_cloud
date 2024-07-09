package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class DirDeleterCreator @Inject constructor(
    private val dirDeleterAssistedFactory: DirDeleterAssistedFactory,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterCreator: CloudWriterCreator,
) {
    suspend fun create(syncTask: SyncTask): DirDeleter? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken?.let { authToken ->
            cloudWriterCreator.createCloudWriter(syncTask.targetStorageType!!, authToken)?.let { cloudWriter ->
                dirDeleterAssistedFactory.create(cloudWriter, syncTask.targetPath!!)
            }
        }
    }
}