package com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_20_file.creator

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.aa_v5.level_10_drivers.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.errorMsg
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class StreamToFileWriter @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val cloudWriterGetter: CloudWriterGetter,
) {
    @Throws(StreamWriterCancelledException::class)
    suspend fun putStreamToTarget(inputStream: InputStream,
                                  filePath: String,
                                  overwriteIfExists: Boolean,
                                  progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        Log.d(TAG, "putFileToTarget('$filePath', $overwriteIfExists)")
        putStreamReal(
            cloudWriterGetter.getTargetCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists,
            progressCallback
        )
    }


    @Throws(StreamWriterCancelledException::class)
    suspend fun putStreamToSource(
        inputStream: InputStream,
        filePath: String,
        overwriteIfExists: Boolean,
        progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        Log.d(TAG, "putFileToSource('$filePath')")
        putStreamReal(
            cloudWriterGetter.getSourceCloudWriter(syncTask),
            inputStream,
            filePath,
            overwriteIfExists,
            progressCallback
        )
    }


    @Throws(StreamWriterCancelledException::class)
    private suspend fun putStreamReal(
        cloudWriter: CloudWriter,
        inputStream: InputStream,
        filePath: String,
        overwriteIfExists: Boolean,
        progressCallback: ((transferredBytes: Long) -> Unit)? = null,
    ) {
        return suspendCancellableCoroutine { cont ->

            cont.invokeOnCancellation {
                inputStream.close()
                throw StreamWriterCancelledException("Cancelled writing stream to file '$filePath'")
            }

            try {
                cloudWriter
                    .putStream(
                        inputStream = inputStream,
                        targetPath = filePath,
                        overwriteIfExists = overwriteIfExists,
                        writingCallback = { progress ->

                            TimeUnit.MILLISECONDS.sleep(100)

                            if (!cont.isActive)
                                return@putStream

//                            Log.d(TAG, "progress: $progress")
                            progressCallback?.invoke(progress)

                        },
                        finishCallback = { _,_ ->
                            cont.resume(Unit)
                        }
                    )
            } catch (t: Throwable) {
                Log.d(TAG, t.errorMsg)
                throw t
            } finally {
                inputStream.close()
            }
        }
    }


    companion object {
        val TAG: String = StreamToFileWriter::class.java.simpleName
    }
}

@AssistedFactory
interface StreamToFileWriterAssistedFactory {
    fun create(syncTask: SyncTask): StreamToFileWriter
}