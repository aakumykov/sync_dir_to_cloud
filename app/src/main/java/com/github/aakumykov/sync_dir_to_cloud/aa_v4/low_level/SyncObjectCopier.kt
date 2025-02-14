package com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.copy_between_streams_with_counting.copyBetweenStreamsWithCountingSuspend
import com.github.aakumykov.sync_dir_to_cloud.aa_v3.SyncOptions
import com.github.aakumykov.sync_dir_to_cloud.aa_v4.low_level.very_basic.CloudWriterGetter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.utils.ProgressCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.IOException
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.coroutines.resume

class SyncObjectCopier @AssistedInject constructor(
    @Assisted private val syncTask: SyncTask,
    private val inputStreamGetterAssistedFactory: InputStreamGetterAssistedFactory,
    private val cloudWriterGetter: CloudWriterGetter,
    private val syncOptions: SyncOptions,
) {
    private val inputStreamGetter: InputStreamGetter by lazy {
        inputStreamGetterAssistedFactory.create(syncTask)
    }


    @Throws(IOException::class)
    suspend fun copyObjectFromSourceToTarget(syncObject: SyncObject,
                                             overwriteIfExists: Boolean = syncOptions.overwriteIfExists,
                                             onProgress: (Int) -> Unit = {}) {
        if (syncObject.isDir) createDir(syncObject)
        else putFile(syncObject, overwriteIfExists, onProgress)
    }


    @Throws(IOException::class)
    private suspend fun createDir(syncObject: SyncObject) {
        cloudWriterGetter
            .getTargetCloudWriter(syncTask)
            .createDir(
                basePath = syncTask.targetPath!!,
                dirName = syncObject.name
            )
    }


    @Throws(IOException::class)
    private suspend fun putFile(
        syncObject: SyncObject,
        overwriteIfExists: Boolean,
        onProgress: (Int) -> Unit = {}
    ) {
        inputStreamGetter
            .getInputStreamFor(syncObject)
            .use { inputStream ->

                return suspendCancellableCoroutine { cont ->
                    thread {
                        cont.invokeOnCancellation { inputStream.close() }

                        val inputFileSize = syncObject.size
                        val progressCalculator = ProgressCalculator(inputFileSize)

                        cloudWriterGetter
                            .getTargetCloudWriter(syncTask)
                            .putStream(
                                inputStream = inputStream,
                                targetPath = syncObject.absolutePathIn(syncTask.targetPath!!),
                                overwriteIfExists = overwriteIfExists,
                                writingCallback = {  bytesWritten: Long ->
                                    if (!cont.isActive)
                                        return@putStream

                                    TimeUnit.MILLISECONDS.sleep(30)

                                    onProgress.invoke(
                                        progressCalculator.progressAsPartOf100(
                                            1f * bytesWritten / inputFileSize
                                        )
                                    )
                                }
                            )

                        cont.resume(Unit)
                    }
                }

            }
    }


    companion object {
        val TAG: String = SyncObjectCopier::class.java.simpleName
    }
}

@AssistedFactory
interface SyncObjectCopierAssistedFactory {
    fun create(syncTask: SyncTask): SyncObjectCopier
}