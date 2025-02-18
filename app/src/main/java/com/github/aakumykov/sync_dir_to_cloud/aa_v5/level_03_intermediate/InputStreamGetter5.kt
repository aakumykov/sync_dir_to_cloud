package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_03_intermediate

import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudReaderGetter
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

interface InputStreamGetterAssistedFactory5 {
    fun create(syncTask: SyncTask): InputStreamGetter5
}