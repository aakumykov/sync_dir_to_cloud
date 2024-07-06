package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.net.Uri
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import java.io.File
import javax.inject.Inject

/**
 * Создаёт каталога "из" SyncObject-а в рамках SyncTask-а.
 */
class SyncObjectDirCreator (
    private val cloudWriter: CloudWriter
) {
    // FIXME: избавиться от "!!"
    /**
     * Создаёт каталог, соответствующий SyncObject в пути назначения, содержащемся в SyncTask.
     * Метод работает синхронно, поэтому требует правильного выбора потока исполнения.
     * @return Полный путь к созданной папке, обёрнутый в Result.
     */
    fun createDir(syncObject: SyncObject, syncTask: SyncTask): Result<String> {

        if (!syncObject.isDir)
            throw IllegalArgumentException("SyncObject it not a directory object: $syncObject")

        try {
            val pathSegments = Uri
                .parse(syncObject.absolutePathIn(syncTask.targetPath!!))
                .pathSegments
                .toMutableList()

            val dirName = pathSegments.removeLast()
            val targetPath = FSItem.pathSegmentsToPath(pathSegments, true)

            cloudWriter.createDir(targetPath, dirName)

            return Result.success(File(targetPath,dirName).absolutePath)

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

fun FSItem.Companion.pathSegmentsToPath(pathSegments: List<String>, isAbsolutePath: Boolean): String {
    return pathSegments.joinToString(DS).let { relativePath ->
        if (isAbsolutePath) DS + relativePath
        else relativePath
    }
}
