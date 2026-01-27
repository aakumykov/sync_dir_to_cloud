package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.deleter

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.yandex_disk_cloud_writer.YandexDiskCloudWriter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DirDeleter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    /**
     * Удаляет ПУСТОЙ каталог.
     * При удалении непустого каталога выбрасывает [YandexDiskCloudWriter.IndeterminateOperationException],
     * так как в REST API Яндекс.Диска такая операция имеет неопределённое время выполнения.
     */
    @Throws(Exception::class)
    suspend fun deleteEmptyDirInTarget(basePath: String, dirName: String) {
        deleteDirWith(
            basePath = basePath,
            dirName = dirName,
            cloudWriter = targetCloudWriter
        )
    }


    @Throws(Exception::class)
    suspend fun deleteEmptyDirInSource(basePath: String, dirName: String) {
        deleteDirWith(
            basePath = basePath,
            dirName = dirName,
            cloudWriter = targetCloudWriter
        )
    }


    @Throws(Exception::class)
    private suspend fun deleteDirWith(basePath: String, dirName: String, cloudWriter: CloudWriter) {
        return suspendCoroutine { cont ->
            thread {
                try {
                    cloudWriter.deleteDir(basePath, dirName)
                    cont.resume(Unit)
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }
        }
    }


    private val sourceCloudWriter: CloudWriter
        get() = cloudWriterGetter.getSourceCloudWriter(syncTask)

    private val targetCloudWriter: CloudWriter
        get() = cloudWriterGetter.getTargetCloudWriter(syncTask)
}


@AssistedFactory
interface DirDeleterAssistedFactory5 {
    fun create(syncTask: SyncTask): DirDeleter5
}