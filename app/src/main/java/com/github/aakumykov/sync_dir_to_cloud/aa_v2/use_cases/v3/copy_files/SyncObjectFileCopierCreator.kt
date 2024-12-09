package com.github.aakumykov.sync_dir_to_cloud.aa_v2.use_cases.v3.copy_files

import com.github.aakumykov.sync_dir_to_cloud.di.annotations.CoroutineFileCopyingScope
import com.github.aakumykov.sync_dir_to_cloud.di.annotations.DispatcherIO
import com.github.aakumykov.sync_dir_to_cloud.domain.entities.SyncTask
import com.github.aakumykov.sync_dir_to_cloud.factories.storage_writer.CloudWriterCreator
import com.github.aakumykov.sync_dir_to_cloud.interfaces.for_repository.cloud_auth.CloudAuthReader
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator.SourceFileStreamSupplierCreator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Создаёт SyncObjectFileCopier для SyncTask.
 */
class SyncObjectFileCopierCreator @Inject constructor(
    private val sourceFileStreamSupplierCreator: SourceFileStreamSupplierCreator,
    private val cloudAuthReader: CloudAuthReader,
    private val cloudWriterCreator: CloudWriterCreator,
    @CoroutineFileCopyingScope private val fileCopyingScope: CoroutineScope,
    @DispatcherIO private val fileCopyingDispatcher: CoroutineDispatcher,
) {
    suspend fun createFileCopierFor(syncTask: SyncTask): StreamToFileDataCopier? {

        val sourceFileStreamSupplier = sourceFileStreamSupplierCreator.create(
            syncTask.id,
            syncTask.sourceStorageType
        )

        val targetCloudWriter = cloudWriterCreator.createCloudWriter(
            syncTask.targetStorageType,
            cloudAuthReader.getCloudAuth(syncTask.targetAuthId)?.authToken
        )

        return if (null != sourceFileStreamSupplier && null != targetCloudWriter) {
            StreamToFileDataCopier(
                sourceFileStreamSupplier = sourceFileStreamSupplier,
                cloudWriter = targetCloudWriter,
                progressCallbackCoroutineScope = fileCopyingScope,
                progressCallbackCoroutineDispatcher = fileCopyingDispatcher,
            )
        } else {
            null
        }
    }
}