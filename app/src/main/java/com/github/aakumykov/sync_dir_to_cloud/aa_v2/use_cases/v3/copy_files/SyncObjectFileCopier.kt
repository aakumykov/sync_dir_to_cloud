package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import android.util.Log
import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.counting_streams.CountingInputStream
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.extensions.round
import com.github.aakumykov.sync_dir_to_cloud.progress_holder.ProgressHolder
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import java.io.InputStream

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
    private val progressHolder: ProgressHolder,
) {
    suspend fun copySyncObject(
        syncObject: SyncObject,
        syncTask: SyncTask,
        overwriteIfExists: Boolean = true,
        onProgressChanged: suspend (Float) -> Unit
    ): Result<String> {

        val sourceFilePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
        val targetFilePath = syncObject.absolutePathIn(syncTask.targetPath!!)

        try {
            val sourceFileStream: InputStream = sourceFileStreamSupplier.getSourceFileStream(sourceFilePath).getOrThrow()

            val countingInputStream = CountingInputStream(sourceFileStream) { readCount ->
                val fraction = (1f*readCount / syncObject.size).round(100)
                progressHolder.putProgress(syncObject.id, fraction)
                onProgressChanged.invoke(fraction)
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