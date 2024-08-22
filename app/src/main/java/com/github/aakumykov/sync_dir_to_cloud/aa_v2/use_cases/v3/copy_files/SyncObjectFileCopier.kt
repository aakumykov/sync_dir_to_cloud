package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.counting_streams.CountingInputStream
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.round
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressCallbackCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private var lastProgressValue: Float = 0f

    suspend fun copySyncObject(
        syncObject: SyncObject,
        syncTask: SyncTask,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Int) -> Unit
    ): Result<String> {

        val sourceFilePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
        val targetFilePath = syncObject.absolutePathIn(syncTask.targetPath!!)

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(sourceFilePath).getOrThrow()

            val countingInputStream = CountingInputStream(sourceFileStream) { readCount ->

                val progress = (1f*readCount / syncObject.size).round(2)
                val progressAsPartOf100 = Math.round(progress * 100)

                if (lastProgressValue != progress) {
//                    Log.d(TAG, "progress: ${syncObject.name} - $progress ($progressAsPartOf100)")
                    lastProgressValue = progress
                    CoroutineScope(progressCallbackCoroutineDispatcher).launch { onProgressChanged.invoke(progressAsPartOf100) }
                }
            }

            cloudWriter.putFile(countingInputStream, targetFilePath, overwriteIfExists)
            return Result.success(targetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }

    companion object {
        val TAG: String = SyncObjectFileCopier::class.java.simpleName
    }
}