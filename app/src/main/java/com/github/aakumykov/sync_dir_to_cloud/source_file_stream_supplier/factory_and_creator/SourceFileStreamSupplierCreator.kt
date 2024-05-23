package com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.factory_and_creator

import com.github.aakumykov.sync_dir_to_cloud.enums.StorageType
import com.github.aakumykov.sync_dir_to_cloud.source_file_stream_supplier.SourceFileStreamSupplier
import javax.inject.Inject

class SourceFileStreamSupplierCreator @Inject constructor(
    private val map: Map<StorageType, SourceFileStreamSupplierFactory>
) {
    @Deprecated("Передавать готовый SyncTask или интерфейс с нужными данными")
    fun create(taskId: String, storageType: StorageType?): SourceFileStreamSupplier? {
        return map[storageType]?.create(taskId)
    }
}
