package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FileDeleter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    @Throws(Exception::class)
    suspend fun deleteFileInSource(basePath: String, fileName: String) {
        return deleteFileWith(cloudWriterGetter.getSourceCloudWriter(syncTask), basePath, fileName);
    }

    @Throws(Exception::class)
    suspend fun deleteFileInTarget(basePath: String, fileName: String) {
        return deleteFileWith(cloudWriterGetter.getTargetCloudWriter(syncTask), basePath, fileName);
    }

    private suspend fun deleteFileWith(cloudWriter: CloudWriter, basePath: String, fileName: String) {
        return suspendCoroutine { continuation ->
            thread {
                try {
                    cloudWriter.deleteFile(basePath, fileName)
                    continuation.resume(Unit)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}


@AssistedFactory
interface FileDeleterAssistedFactory5 {
    fun create(syncTask: SyncTask): FileDeleter5
}