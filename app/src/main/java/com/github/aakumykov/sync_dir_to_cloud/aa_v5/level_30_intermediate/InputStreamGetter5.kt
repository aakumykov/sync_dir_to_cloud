package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_30_intermediate

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream

class InputStreamGetter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudReaderGetter: CloudReaderGetter
) {
    @Throws(Exception::class)
    suspend fun getInputStreamInSource(syncObject: SyncObject): InputStream {
        val filePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
        return cloudReaderGetter
            .getSourceCloudReaderFor(syncTask)
            .getFileInputStream(filePath)
            .getOrThrow()
    }

    @Throws(Exception::class)
    suspend fun getInputStreamInTarget(syncObject: SyncObject): InputStream {
        val filePath = syncObject.absolutePathIn(syncTask.targetPath!!)
        return cloudReaderGetter
            .getTargetCloudReaderFor(syncTask)
            .getFileInputStream(filePath)
            .getOrThrow()
    }
}

@AssistedFactory
interface InputStreamGetterAssistedFactory5 {
    fun create(syncTask: SyncTask): InputStreamGetter5
}