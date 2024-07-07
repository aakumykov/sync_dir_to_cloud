package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.files_deleter

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.extensions.relativePath
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileDeleter @AssistedInject constructor(
    @Assisted private val cloudWriter: CloudWriter,
    @Assisted private val targetDir: String
) {
    /**
     * @return SyncObject, соответствующий удалённому объекту.
     */
    fun deleteFile(syncObject: SyncObject): Result<SyncObject> {
        return try {
            cloudWriter.deleteFile(targetDir, syncObject.relativePath)
            Result.success(syncObject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@AssistedFactory
interface FileDeleterAssistedFactory {
    fun create(cloudWriter: CloudWriter, targetDirPath: String): FileDeleter
}