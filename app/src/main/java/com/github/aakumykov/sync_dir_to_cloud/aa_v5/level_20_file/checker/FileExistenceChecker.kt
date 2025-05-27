package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.checker

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.functions.combineFSPaths
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FileExistenceChecker @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudReaderGetter: CloudReaderGetter,
) {
    /**
     * @param dirName Имя каталога, может быть многоуровневым (?)
     */
    suspend fun dirNotExistsInTarget(dirName: String): Boolean {
        return !targetCloudReader.dirExists(
            combineFSPaths(
                syncTask.targetPath!!,
                dirName
            )
        ).getOrThrow()
    }

    /**
     * @param dirName Имя каталога, может быть многоуровневым (?)
     */
    suspend fun dirNotExistsInSource(dirName: String): Boolean {
        return !sourceCloudReader.dirExists(
            combineFSPaths(
                syncTask.sourcePath!!,
                dirName
            )
        ).getOrThrow()
    }

    private val sourceCloudReader by lazy {
        cloudReaderGetter.getSourceCloudReaderFor(syncTask)
    }

    private val targetCloudReader by lazy {
        cloudReaderGetter.getTargetCloudReaderFor(syncTask)
    }
}


@AssistedFactory
interface FileExistenceCheckerAssistedFactory {
    fun create(syncTask: SyncTask): FileExistenceChecker
}