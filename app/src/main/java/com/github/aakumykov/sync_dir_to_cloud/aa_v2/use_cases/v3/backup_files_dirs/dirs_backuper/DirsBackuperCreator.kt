package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.dirs_backuper

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.backup_files_dirs.BackupDirCreatorCreator
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudReaderCreator
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class DirsBackuperCreator @Inject constructor(
    private val dirsBackuperAssistedFactory: DirsBackuperAssistedFactory,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterCreator: CloudWriterCreator,
){
    suspend fun createDirsBackuperForTask(syncTask: SyncTask): DirsBackuper? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken?.let { targetAuthToken ->
            val cloudWriter: CloudWriter? = cloudWriterCreator.createCloudWriter(syncTask.targetStorageType, targetAuthToken)
            if (null != cloudWriter) {
                dirsBackuperAssistedFactory.create(cloudWriter)
            } else {
                null
            }
        }
    }
}