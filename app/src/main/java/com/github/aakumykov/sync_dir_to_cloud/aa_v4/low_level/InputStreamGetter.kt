package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level

import com.github.aakumykov.cloud_reader.CloudReader
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic.CloudReaderGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.extensions.isFile
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.InputStream
import javax.inject.Inject

class InputStreamGetter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudReaderGetter: CloudReaderGetter,
) {
    @Throws(Exception::class)
    suspend fun getInputStreamFor(syncObject: SyncObject): InputStream {
        if (syncObject.isFile) {
            return cloudReader()
                .getFileInputStream(syncObject.absolutePathIn(syncTask.sourcePath!!))
                .getOrThrow()
        } else {
            throw IllegalArgumentException("Input stream cannot be getted for directory ('${syncObject.name}')")
        }
    }

    private suspend fun cloudReader(st: SyncTask = syncTask): CloudReader {
        return cloudReaderGetter.getSourceCloudReaderFor(st)
    }
}

@AssistedFactory
interface InputStreamGetterAssistedFactory {
    fun create(syncTask: SyncTask): InputStreamGetter
}