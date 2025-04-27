package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume

class FileWriter5 @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    @Throws(Exception::class)
    suspend fun putFileToTarget(inputStream: InputStream,
                                filePath: String,
                                overwriteIfExists: Boolean
    ) {
        Log.d(TAG, "putFileToTarget('$filePath')")
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
        Log.d(TAG, "putFileToSource('$filePath')")
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
                    overwriteIfExists = overwriteIfExists,
                    writingCallback = { progress ->
                        if (!cont.isActive)
                            return@putStream
                        Log.d(TAG, "progress: $progress")
                    },
                    finishCallback = { _,_ ->
                        cont.resume(Unit)
                    }
                )
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