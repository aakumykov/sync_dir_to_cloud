package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.create_dirs

import android.net.Uri
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.file_lister_navigator_selector.fs_item.FSItem
import com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.fs_item_ext.pathSegmentsToPath
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import java.io.File

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