package com.github.aakumykov.sync_dir_to_cloud.a0_backupers.v2_ugly.dirs_backuper

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.di.creators.CloudWritersHolder
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class DirsBackuperCreator @Inject constructor(
    private val dirsBackuperAssistedFactory: DirsBackuperAssistedFactory,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWritersHolder: CloudWritersHolder,
){
    suspend fun createDirsBackuperForTask(syncTask: SyncTask, executionId: String): DirsBackuper? {
        return cloudAuthReader.getCloudAuth(syncTask.targetAuthId!!)?.authToken?.let { targetAuthToken ->

            val cloudWriter: CloudWriter? = cloudWritersHolder.getCloudWriter(syncTask.targetStorageType, targetAuthToken)

            if (null != cloudWriter) {
                dirsBackuperAssistedFactory.create(cloudWriter, executionId)
            } else {
                null
            }
        }
    }
}