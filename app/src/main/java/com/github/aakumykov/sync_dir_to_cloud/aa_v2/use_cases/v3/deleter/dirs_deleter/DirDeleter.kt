package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.deleter.dirs_deleter

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DirDeleter @AssistedInject constructor(
    @Assisted private val cloudWriter: CloudWriter,
    @Assisted private val targetDir: String
){
    fun deleteDir(syncObject: SyncObject): Result<SyncObject> {
        return try {
            // FIXME: сделать метод, удаляющий единичный каталог?
            cloudWriter.deleteDirRecursively(targetDir, syncObject.name)
            Result.success(syncObject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


@AssistedFactory
interface DirDeleterAssistedFactory {
    fun create(cloudWriter: CloudWriter, targetDir: String): DirDeleter
}