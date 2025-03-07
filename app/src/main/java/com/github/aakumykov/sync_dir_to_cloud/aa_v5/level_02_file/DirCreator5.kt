package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

// TODO: сделать suspend-функции отменяемыми, если возможно.

class DirCreator5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter
) {
    /**
     * @param basePath Родительский каталог
     * @param dirName Дочерний каталог. Может быть многоуровневым.
     */
    @Throws(Exception::class)
    suspend fun createDirInTarget(basePath: String, dirName: String) {
        cloudWriterGetter
            .getTargetCloudWriter(syncTask)
            .createDir(basePath, dirName)
    }

    /**
     * @param basePath Родительский каталог
     * @param dirName Дочерний каталог. Может быть многоуровневым.
     */
    @Throws(Exception::class)
    suspend fun createDirInSource(basePath: String, dirName: String) {
        cloudWriterGetter
            .getSourceCloudWriter(syncTask)
            .createDir(basePath, dirName)
    }
}

@AssistedFactory
interface DirCreator5AssistedFactory {
    fun create(syncTask: SyncTask): DirCreator5
}