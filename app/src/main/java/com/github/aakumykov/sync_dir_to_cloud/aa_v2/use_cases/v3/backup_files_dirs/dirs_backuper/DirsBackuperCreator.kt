package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class DirsBackuperCreator @Inject constructor(
    private val dirsBackuperAssistedFactory: DirsBackuperAssistedFactory,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterGetter: CloudWriterGetter,
){
    suspend fun createDirsBackuperForTask(syncTask: SyncTask, executionId: String): DirsBackuper? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken?.let { targetAuthToken ->

            val cloudWriter: CloudWriter? = cloudWriterGetter.getCloudWriter(syncTask.targetStorageType, targetAuthToken)

            if (null != cloudWriter) {
                dirsBackuperAssistedFactory.create(cloudWriter, executionId)
            } else {
                null
            }
        }
    }
}