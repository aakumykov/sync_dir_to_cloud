package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_03_sync_object

import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.drivers_getter.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.InputStream

class InputStreamGetter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudReaderGetter: CloudReaderGetter
) {
    @Throws(Exception::class)
    suspend fun getInputStreamFor(syncObject: SyncObject): InputStream {
        val filePath = syncObject.absolutePathIn(syncTask.targetPath!!)
        return cloudReaderGetter
            .getSourceCloudReaderFor(syncTask)
            .getFileInputStream(filePath)
            .getOrThrow()
    }
}

interface InputStreamGetter5AssistedFactory {
    fun create(syncTask: SyncTask): InputStreamGetter5
}