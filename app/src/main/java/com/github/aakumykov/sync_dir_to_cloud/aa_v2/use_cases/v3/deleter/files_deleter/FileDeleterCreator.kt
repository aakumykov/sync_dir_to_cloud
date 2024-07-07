package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter

import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class FileDeleterCreator @Inject constructor(
    private val cloudWriterCreator: CloudWriterCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val fileDeleterAssistedFactory: FileDeleterAssistedFactory,
){
    suspend fun createFileDeleterForTask(syncTask: SyncTask): FileDeleter? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken?.let { authToken ->
            cloudWriterCreator.createCloudWriter(syncTask.targetStorageType!!, authToken)?.let { cloudWriter ->
                fileDeleterAssistedFactory.create(cloudWriter, syncTask.targetPath!!)
            }
        }
    }
}