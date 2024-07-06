package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier

/**
 * Копирует данные SyncObject-а из источника в приёмник указанный в SyncTask.
 */
class SyncObjectFileCopier (
    private val sourceFileStreamSupplier: SourceFileStreamSupplier,
    private val cloudWriter: CloudWriter,
) {
    suspend fun copySyncObject(syncObject: SyncObject, syncTask: SyncTask, overwriteIfExists: Boolean = true): Result<String> {

        val sourceFilePath = syncObject.absolutePathIn(syncTask.sourcePath!!)
        val targetFilePath = syncObject.absolutePathIn(syncTask.targetPath!!)

        try {
            val sourceFileStream = sourceFileStreamSupplier.getSourceFileStream(sourceFilePath).getOrThrow()
            cloudWriter.putFile(sourceFileStream, targetFilePath, overwriteIfExists)
            return Result.success(targetFilePath)
        }
        catch (e: Exception) {
            return Result.failure(e)
        }
    }
}