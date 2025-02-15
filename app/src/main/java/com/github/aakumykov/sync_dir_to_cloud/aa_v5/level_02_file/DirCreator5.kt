package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file

import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.drivers_getter.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class DirCreator5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter
) {
    @Throws(Exception::class)
    suspend fun createDir(basePath: String, dirName: String) {
        cloudWriterGetter
            .getTargetCloudWriter(syncTask)
            .createDir(basePath, dirName)
    }
}

@AssistedFactory
interface DirCreator5AssistedFactory {
    fun create(syncTask: SyncTask): DirCreator5
}