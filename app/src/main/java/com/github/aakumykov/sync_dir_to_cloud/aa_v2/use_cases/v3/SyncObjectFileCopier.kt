package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3

import com.github.aakumykov.cloud_writer.CloudWriter
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncObject
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.extensions.absolutePathIn
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
import javax.inject.Inject

/**
 * Копирует данные SyncObject-а из источника в приёмник указанного SyncTask.
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

/**
 * Создаёт SyncObjectFileCopier для SyncTask.
 */
class SyncObjectCopierCreator @Inject constructor(
    private val sourceFileStreamSupplierCreator: SourceFileStreamSupplierCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterCreator: CloudWriterCreator
) {
    suspend fun createFileCopierFor(syncTask: SyncTask): SyncObjectFileCopier? {

        val sourceFileStreamSupplier = sourceFileStreamSupplierCreator.create(
            syncTask.id,
            syncTask.targetStorageType
        )

        val targetCloudWriter = cloudWriterCreator.createCloudWriter(
            syncTask.targetStorageType,
            cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken
        )

        return if (null != sourceFileStreamSupplier && null != targetCloudWriter) {
            SyncObjectFileCopier(
                sourceFileStreamSupplier,
                targetCloudWriter
            )
        } else {
            null
        }
    }
}