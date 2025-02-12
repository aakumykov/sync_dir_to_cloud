package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.files_backuper

import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class FilesBackuperCreator @Inject constructor(
    private val filesBackuperAssistedFactory: FilesBackuperAssistedFactory,
    private val cloudReaderGetter: CloudReaderGetter,
    private val cloudWriterGetter: CloudWriterGetter,
    private val cloudAuthReader: CloudAuthReader
) {
    // FIXME: Почему он nullable?
    suspend fun createFilesBackuperForSyncTask(syncTask: SyncTask, executionId: String): FilesBackuper? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.let { targetAuth ->

            val targetCloudReader = cloudReaderGetter.getCloudReader(syncTask.targetStorageType, targetAuth.authToken)
            val targetCloudWriter = cloudWriterGetter.getCloudWriter(syncTask.targetStorageType, targetAuth.authToken)

            return if (null != targetCloudReader && null != targetCloudWriter) {
                filesBackuperAssistedFactory
                    .createFilesBackuper(targetCloudReader, targetCloudWriter, executionId)
            } else {
                null
            }
        }
    }
}