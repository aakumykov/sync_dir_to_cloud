package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_02_file

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_01_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream

class FileWriter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    @Throws(Exception::class)
    suspend fun putFileToTarget(inputStream: InputStream,
                                filePath: String,
                                overwriteIfExists: Boolean
    ) {
        putStreamReal(
            cloudWriterGetter.getTargetCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists
        )
    }

    @Throws(Exception::class)
    suspend fun putFileToSource(inputStream: InputStream,
                                filePath: String,
                                overwriteIfExists: Boolean
    ) {
        putStreamReal(
            cloudWriterGetter.getSourceCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists
        )
    }

    @Throws(Exception::class)
    private suspend fun putStreamReal(
        cloudWriter: CloudWriter,
        inputStream: InputStream,
        filePath: String,
        overwriteIfExists: Boolean
    ) {
        return suspendCancellableCoroutine { cont ->

            cont.invokeOnCancellation { inputStream.close() }

            cloudWriter
                .putStream(
                    inputStream = inputStream,
                    targetPath = filePath,
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