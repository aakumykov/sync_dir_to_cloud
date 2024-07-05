package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.net.Uri
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import javax.inject.Inject

class SyncObjectDirCreator constructor(
    private val cloudWriter: CloudWriter
) {
    // FIXME: избавиться от "!!"
    /**
     * @return Полный путь к созданной папке, обёрнутый в Result.
     */
    suspend fun createDir(syncObject: SyncObject, syncTask: SyncTask): Result<String> {
        try {
            val dirNameInTarget = syncObject.absolutePathIn(syncTask.targetPath!!)
            val pathUri = Uri.parse(dirNameInTarget)
            cloudWriter.createDir(pathUri.path!!, pathUri.lastPathSegment!!)
            return Result.success(dirNameInTarget)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}

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