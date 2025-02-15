package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02

import android.util.Log
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.drivers_getter.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream

class FileWriter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
    private val syncOptions: SyncOptions,
) {
    // TODO: какое исключение выбрасывает и выбрасывает ли?
    @Throws(Exception::class)
    suspend fun putFile(inputStream: InputStream,
                        targetFilePath: String,
                        overwriteIfExists: Boolean = syncOptions.overwriteIfExists
    ) {
        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { inputStream.close() }
            cloudWriterGetter
                .getTargetCloudWriter(syncTask)
                .putStream(
                    inputStream = inputStream,
                    targetPath = targetFilePath,
                    overwriteIfExists = overwriteIfExists
                ) { progress ->
                    if (!cont.isActive)
                        return@putStream
                    Log.d(TAG, "progress: $progress")
                }
        }
    }

    companion object {
        val TAG: String = FileWriter5::class.java.simpleName
    }
}

@AssistedFactory
interface FileWriter5AssistedFactory {
    fun create(syncTask: SyncTask): FileWriter5
}